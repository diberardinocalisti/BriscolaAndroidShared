package multiplayer.Game;

import android.bluetooth.BluetoothA2dp;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

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

import static gameEngine.Engine.doLogic;
import static gameEngine.Engine.getOtherCarta;
import static gameEngine.Engine.getOtherPlayer;
import static gameEngine.Engine.muoviCarta;
import static gameEngine.Engine.prossimoTurno;
import static gameEngine.Engine.terminaManche;
import static gameEngine.Game.I_CAMPO_GIOCO;
import static gameEngine.Game.activity;
import static gameEngine.Game.carte;
import static gameEngine.Game.giocante;
import static gameEngine.Game.giocatori;
import static multiplayer.engineMultiplayer.*;

public class ActivityMultiplayerGame extends AppCompatActivity {
    public static boolean start = false;
    public String onStopUser;
    public boolean stopApp = false;
    public String roleId, noteRoleId;
    public String host, enemy;
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


        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.campo_da_gioco);
        getSupportActionBar().hide();

        onStop = false;
        distribuisci = false;

        roleId = (role.equals("HOST") ? "host" : "enemy");

        /*if(role.equals("HOST")){
            engineMultiplayer.startMultiplayerGame(ActivityMultiplayerGame.this);
        }*/

        inizializza(this);

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot.getValue(GameRoom.class);

                if(!onStop){
                    checkIfSomeoneLeft();

                    if(!distribuisci)
                        distribuisciCarte();
                    else
                    {
                        String t = snapshot.getTurno();

                        String app = (t.equals("enemy") ? snapshot.getGiocataDaHost() : snapshot.getGiocataDaEnemy());

                        if(app == null)
                            return;

                        String nome = app.split("#")[0];
                        int indice = Integer.parseInt(app.split("#")[1]);

                        if(nome.equals("null"))
                            return;

                        Carta c = Engine.getCartaFromName(nome);

                        Game.canPlay = (roleId.equals(snapshot.getTurno()));

                        giocante = Game.canPlay ? giocatori[0] : Game.user;

                        if(!roleId.equals(t))
                        {
                            Object event = new Object();
                            Engine.muoviCarta(c.getButton(),Game.carte[c.getPortatore().index + I_CAMPO_GIOCO[0]],false,true,event);

                            System.out.println(c.getPortatore().index + " " +  I_CAMPO_GIOCO[0] + " indici");

                            new Thread(() -> {
                                try {
                                    synchronized (event){
                                        event.wait();
                                        activity.runOnUiThread(() -> {
                                            Game.canPlay = true;

                                            giocante.lancia(c);
                                            final Giocatore vincente = doLogic(c, getOtherCarta(c));

                                            if(vincente == null) {
                                                prossimoTurno(getOtherPlayer(giocante));
                                            }else{
                                                new Handler().postDelayed(() -> terminaManche(vincente), 1750);
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }else{
                            View daMuovere = Game.carteBottoni[indice];

                            Object event = new Object();
                            muoviCarta(daMuovere, Game.carte[I_CAMPO_GIOCO[0]], false, true, event);

                            new Thread(() -> {
                                try {
                                    synchronized (event){
                                        event.wait();
                                        activity.runOnUiThread(() -> {
                                            Game.canPlay = true;

                                            giocante.lancia(c);
                                            final Giocatore vincente = doLogic(c, getOtherCarta(c));

                                            if(vincente == null) {
                                                prossimoTurno(getOtherPlayer(giocante));
                                            }else{
                                                new Handler().postDelayed(() -> terminaManche(vincente), 1750);
                                            }
                                        });
                                    }
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }

                        /*if(Game.canPlay)
                        {
                            Integer indexCarta = Game.user.getIndexFromCarta(c);
                        }else
                        {

                        }*/



                        //Gestisco l'onClick

                        //Modifico il campo nel db
                        //Porto la carta al centro sul mio dispositivo
                        //Porto la carta al centro sull'altro telefono

                    }


                    aggiornaNCarte();
                }

                if(onStop) {
                    FirebaseClass.deleteFieldFirebase(null, codiceStanza);
                    ActivityMultiplayerGame.this.finish();
                }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void distribuisciCarte(){
        if(role.equals("HOST")){
            mazzoOnline = engineMultiplayer.creaMazzoFirebase();
            snapshot.setCarteRimanenti(mazzoOnline);

            FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",mazzoOnline);

            Game.user.svuotaMazzo();

            for(int i = 0; i < Game.nCarte; i++) {
                Game.user.pesca();
            }
        }else{
            new Handler().postDelayed(() -> {
                mazzoOnline = snapshot.getCarteRimanenti();
                Engine.creaMazzo(mazzoOnline);

                Game.user.svuotaMazzo();

                for(int i = 0; i < Game.nCarte; i++)
                    Game.user.pesca();
            }, 1750);
        }

        distribuisci = true;

        for(Button b : Game.user.bottoni)
        {
            b.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!Game.canPlay)
                        return;

                    Carta c = Engine.getCartaFromButton(b);
                    int index = Game.user.getIndexFromCarta(c);
                    //Toast.makeText(getApplicationContext(),"Carta --> " + c.getNome(), Toast.LENGTH_SHORT).show();
                    if(role.equals("HOST"))
                    {
                        //Modifico giocataDaHost
                        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost",c.getNome()+"#"+index);
                        FirebaseClass.editFieldFirebase(codiceStanza,"turno","enemy");
                    }else
                    {
                        //Modifico giocataDaEnemy
                        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy",c.getNome()+"#"+index);
                        FirebaseClass.editFieldFirebase(codiceStanza,"turno","host");
                    }

                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void aggiornaNCarte(){
        String carteDisponibili = snapshot.getCarteRimanenti();
        String[] strTok = carteDisponibili.split(DELIMITER);

        Engine.aggiornaNCarte(strTok.length);
    }

    protected void checkIfSomeoneLeft(){
        String host = snapshot.getHost();
        String enemy = snapshot.getEnemy();

        if(host.equals("null") && !enemy.equals("null"))
        {
            if(role.equals("HOST")) {
                Toast.makeText(getApplicationContext(), this.getString(R.string.youleft), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),this.getString(R.string.enemyleft), Toast.LENGTH_SHORT).show();
                Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
            }
            onStop = true;
        }

        if(enemy.equals("null") && !host.equals("null")) {
            if (!role.equals("HOST")){
                Toast.makeText(getApplicationContext(), this.getString(R.string.youleft), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(), this.getString(R.string.enemyleft), Toast.LENGTH_SHORT).show();
                Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
            }

            onStop = true;
        }
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
