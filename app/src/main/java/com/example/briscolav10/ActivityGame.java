package com.example.briscolav10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import java.io.IOException;

import Home.MainActivity;
import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Utility;
import multiplayer.ActivityMultiplayerGame;

import static Login.loginClass.getFBUserId;
import static Login.loginClass.getFullFBName;
import static multiplayer.engineMultiplayer.codiceStanza;

public class ActivityGame extends AppCompatActivity {
    private AdView mAdView;

    public static boolean multiplayer = false;
    public static boolean attesa = false;
    public static boolean finishAttesa = false;    //Se mando l'utente alla pagina del gioco è comunque onmStop() e quindi verrebbe eliminata la staanza

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        multiplayer = extras.getBoolean("multiplayer");

        Utility.enableTopBar(this);

        //Se l'utente ha scelto modalità singlePlayer
        if (multiplayer) {
            try {
                startMultiPlayer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                startSinglePlayer();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void startSinglePlayer() throws IOException {
        setContentView(R.layout.campo_da_gioco);

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        multiplayer = false;
        attesa = false;

        Game.startGame(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    protected void startMultiPlayer() throws IOException {
        if(ActivityMultiplayerGame.onStop){
            Utility.goTo(ActivityGame.this, MainActivity.class);
            ActivityMultiplayerGame.onStop = false;
        }

        setContentView(R.layout.stanza_di_attesa);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        attesa = true;

        TextView codice = findViewById(R.id.codice);
        TextView stato = findViewById(R.id.stato);
        TextView nomeHost = findViewById(R.id.nome1);
        ImageView picHost = findViewById(R.id.icon);
        Button chiudi = findViewById(R.id.chiudisala);

        codice.setText(this.getString(R.string.code) + codiceStanza);
        stato.setText(this.getString(R.string.state) + this.getString(R.string.waiting));
        nomeHost.setText(getFullFBName());
        loginClass.setImgProfile(this, getFBUserId(), picHost);

        chiudi.setOnClickListener(v -> {
            Utility.oneLineDialog(ActivityGame.this, this.getString(R.string.confirmleavegame), () -> {
                //Elimino la stanza dal db
                FirebaseClass.deleteFieldFirebase(null, codiceStanza);

                //Lo riporto nella homepage
                Utility.goTo(ActivityGame.this, MainActivity.class);
            });
        });

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals("enemy")){
                        //è entrato l'avverrsario nella stanza
                        if(!value.equals("null")  && !ActivityMultiplayerGame.onStop){
                            finishAttesa = true;
                            Intent i = new Intent(ActivityGame.this, ActivityMultiplayerGame.class);
                            ActivityGame.this.startActivity(i);
                        }
                    }
                }
            }

            @Override public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError databaseError){}
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ActivityMultiplayerGame.onStop)
        {
           Utility.goTo(ActivityGame.this,MainActivity.class);
           ActivityMultiplayerGame.onStop = false;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        Game.gameClosed = true;
        
        if(attesa && !finishAttesa){
            FirebaseClass.deleteFieldFirebase(null, codiceStanza);
            Toast.makeText(getApplicationContext(), this.getString(R.string.sessionclosed), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    @Override
    public void onBackPressed() {
        Utility.oneLineDialog(this, this.getString(R.string.confirmleavegame), ActivityGame.super::onBackPressed);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }
}