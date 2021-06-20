package multiplayer.Game;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Giocatore;
import gameEngine.Utility;
import gameEngine.onClick;
import multiplayer.GameRoom;
import multiplayer.GiocatoreMP;
import multiplayer.engineMultiplayer;
import okhttp3.internal.Util;

import static multiplayer.engineMultiplayer.*;

public class ActivityMultiplayerGame extends AppCompatActivity {
    public static boolean start = false;
    private String onStopUser;
    private boolean stopApp = false;
    private String roleId, noteRoleId;
    private String host, enemy;
    public static boolean onStop = false;
    public static String mazzoOnline = "";
    public static boolean initialManche = false;
    public static GameRoom snapshot;
    public static boolean distribuisci = false;

    //I primi 3 bottoni sono dell'avversario
    private Button carte[] = new Button[6];


    //button9 carta dell'avversario button10 mia carta

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.campo_da_gioco);
        getSupportActionBar().hide();

        Utility.ridimensionamento(this, findViewById(R.id.parent));

        inizializza(this);

        onStop = false;
        distribuisci = false;

        final String[][] daDare = {new String[3]};

        roleId = (role.equals("HOST") ? "host" : "enemy");

        /*if(role.equals("HOST")){
            engineMultiplayer.startMultiplayerGame(ActivityMultiplayerGame.this);
        }*/

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot.getValue(GameRoom.class);

                if(!onStop)
                {
                    String carteRimanenti = snapshot.getCarteRimanenti();
                    String host = snapshot.getHost();
                    String enemy = snapshot.getEnemy();
                    String pescato = snapshot.getHostPescato();

                    if(host.equals("null") && !enemy.equals("null"))
                    {
                        if(role.equals("HOST"))
                            Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Il tuo avversario ha abbandonato la partita!\nHai vinto a tavolino!",Toast.LENGTH_SHORT).show();
                            Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                        }
                        onStop = true;
                    }

                    if(enemy.equals("null") && !host.equals("null"))
                    {
                        if(!role.equals("HOST"))
                            Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                        else
                        {
                            Toast.makeText(getApplicationContext(),"Il tuo avversario ha abbandonato la partita!\nHai vinto a tavolino!",Toast.LENGTH_SHORT).show();
                            Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                        }

                        onStop = true;
                    }

                    if(!distribuisci)
                    {
                        mazzoOnline = engineMultiplayer.creaMazzoFirebase();
                        FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",mazzoOnline);
                        snapshot.setCarteRimanenti(mazzoOnline);
                        carteRimanenti = snapshot.getCarteRimanenti();

                        /*TODO: Direi di servirsi del metodo pesca di classe GiocatoreMP per pescare le 3 carte iniziali, in questo metodo
                            invece ciclerei CARTE_INIZIALI volte invocando appunto il metodo pesca di classe GiocatoreMP
                            (ancora da ridefinire per il multiplayer), in questo modo evitiamo ripetizioni e ci semplifichiamo il lavoro;
                        */
                        //System.out.println("Distribuisco");
                        if(!carteRimanenti.equals("null"))
                        {
                            Toast.makeText(getApplicationContext(),"Distribuisco le carte...",Toast.LENGTH_SHORT).show();
                            if(role.equals("HOST"))
                            {
                                //pesco subito
                                String pescate = mazzoOnline.split(";")[0] + ";" + mazzoOnline.split(";")[1] + ";" + mazzoOnline.split(";")[2];

                                Toast.makeText(getApplicationContext(),pescate,Toast.LENGTH_LONG).show();

                                FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",arrayListToString(removeCardsFromArray(pescate)));
                            }else
                            {
                                Handler handler = new Handler();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        String pescate = mazzoOnline.split(";")[0] + ";" + mazzoOnline.split(";")[1] + ";" + mazzoOnline.split(";")[2];

                                        Toast.makeText(getApplicationContext(),pescate,Toast.LENGTH_LONG).show();

                                        FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",arrayListToString(removeCardsFromArray(pescate)));
                                    }
                                },1750);
                            }
                        }

                        distribuisci = true;
                    }

                }


                System.out.println(snapshot);

                if(onStop)
                    FirebaseClass.deleteFieldFirebase(null,codiceStanza);

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }
        });


    }


    //Un utente è uscito dal campo da gioco
    @Override
    protected void onStop() {
        super.onStop();

        FirebaseClass.editFieldFirebase(codiceStanza, roleId, "null");

        onStop = true;
    }

    protected void creaGiocatori(){
        String[] players;

        if(this.roleId.equals("host"))
            players = new String[]{host, enemy};
        else
            players = new String[]{enemy, host};

        for(int i = 0; i < Game.nGiocatori; i++)
            Game.giocatori[i] = new GiocatoreMP(players[i], i);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

}
