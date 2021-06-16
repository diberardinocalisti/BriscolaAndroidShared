package multiplayer.Game;

import android.bluetooth.BluetoothA2dp;
import android.content.Intent;
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

    //I primi 3 bottoni sono dell'avversario
    private Button carte[] = new Button[6];


    //button9 carta dell'avversario button10 mia carta

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        inizializza(this);

        onStop = false;

        final String[][] daDare = {new String[3]};

        roleId = (role.equals("HOST") ? "host" : "enemy");

        /*if(role.equals("HOST")){
            engineMultiplayer.startMultiplayerGame(ActivityMultiplayerGame.this);
        }*/

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                boolean nuovoPlayer = false;

                for(DataSnapshot d : dataSnapshot.getChildren()) {
                    if(d.getKey().equals("host")) {
                        host = String.valueOf(d.getValue());
                        nuovoPlayer = true;
                    }else if(d.getKey().equals("enemy")) {
                        enemy = String.valueOf(d.getValue());
                        nuovoPlayer = true;
                    }
                }

                if(host.equals("null") && !onStop)
                {
                    if(role.equals("HOST")){
                        Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"L'avversario abbandonato la partita!\nHai vinto a tavolino!",Toast.LENGTH_SHORT).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }
                    FirebaseClass.deleteFieldFirebase(null,codiceStanza);

                }
                else if(enemy.equals("null") && !onStop)
                {
                    if(!role.equals("HOST")){
                        Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(getApplicationContext(),"L'avversario abbandonato la partita!\nHai vinto a tavolino!",Toast.LENGTH_SHORT).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }
                    FirebaseClass.deleteFieldFirebase(null,codiceStanza);
                }else{
                    if(nuovoPlayer)
                        creaGiocatori();
                }

            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
        });

       FirebaseClass.getFbRefSpeicific(codiceStanza).child("carteRimanenti").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String c = dataSnapshot.getValue(String.class);
                //GameRoom g = dataSnapshot.getValue(GameRoom.class);
            }

            @Override public void onCancelled(DatabaseError databaseError){}
        });

       /*
        try {
            daDare = getInitialCards();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(String s: daDare)
            System.out.println("s --> " + s);*/
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


}
