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
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

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
import static gameEngine.Game.mazzo;
import static gameEngine.Game.mazzoIniziale;
import static gameEngine.Game.semi;
import static multiplayer.engineMultiplayer.*;

public class ActivityMultiplayerGame extends AppCompatActivity {
    public static boolean start = false;
    public static String roleId;
    public static String host = "host", enemy = "enemy";
    public static boolean onStop = false;
    public static String mazzoOnline = "";
    public static boolean initialManche = false;
    public static GameRoom snapshot;
    public static boolean distribuisci = false;
    private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.campo_da_gioco);

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        getSupportActionBar().hide();

        onStop = false;
        distribuisci = false;

        roleId = (role.equals("HOST") ? "host" : "enemy");

        Game.canPlay = roleId.equals("host");

        /*Toast.makeText(getApplicationContext(),"canPlAY --> " + Game.canPlay,Toast.LENGTH_SHORT).show();*/

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

                    if(!distribuisci) {
                        engineMultiplayer.distribuisciCarte();
                        engineMultiplayer.onClick();
                    }else{
                        engineMultiplayer.cartaGiocata();
                    }

                    engineMultiplayer.aggiornaNCarte();
                }

                if(onStop) {
                    FirebaseClass.deleteFieldFirebase(null, codiceStanza);
                    ActivityMultiplayerGame.this.finish();
                }
            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
        });


    }

    //Un utente è uscito dal campo da gioco
    @Override
    protected void onStop() {
        super.onStop();
        FirebaseClass.editFieldFirebase(codiceStanza, roleId, "null");
        onStop = true;
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
