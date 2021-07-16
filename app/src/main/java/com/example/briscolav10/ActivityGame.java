package com.example.briscolav10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Home.MainActivity;
import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

import static Login.loginClass.getFBNome;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.setImgProfile;
import static multiplayer.engineMultiplayer.codiceStanza;

public class ActivityGame extends AppCompatActivity {
    ProfilePictureView imgP;
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

        //Se l'utente ha scelto modalità singlePlayer
        if (multiplayer) {
            startMultiPlayer();
        } else {
            try {
                startSinglePlayer();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void startSinglePlayer() throws InterruptedException {
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

        imgP = findViewById(R.id.friendProfilePictureUser);

        if(isFacebookLoggedIn())
            setImgProfile(imgP);

        Game.startGame(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    protected void startMultiPlayer(){
        if(ActivityMultiplayerGame.onStop){
            Utility.goTo(ActivityGame.this, MainActivity.class);
            ActivityMultiplayerGame.onStop = false;
        }

        setContentView(R.layout.stanza_di_attesa);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.enableTopBar(this);

        attesa = true;

        ((TextView) findViewById(R.id.codice)).setText(this.getString(R.string.code) + codiceStanza);
        ((TextView) findViewById(R.id.stato)).setText(this.getString(R.string.state) + this.getString(R.string.waiting));
        ((TextView) findViewById(R.id.nome1)).setText(getFBNome());

        Button chiudi = findViewById(R.id.chiudisala);
        loginClass.setImgProfile(findViewById(R.id.friendProfilePicture1));

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals("enemy"))
                    {
                        //è entrato l'avverrsario nella stanza
                        if(!value.equals("null")  && !ActivityMultiplayerGame.onStop && !ActivityMultiplayerGame.start)
                        {
                            finishAttesa = true;
                            Intent i = new Intent(ActivityGame.this,ActivityMultiplayerGame.class);
                            ActivityGame.this.startActivity(i);
                            System.out.println(value + " si è unito alla partita!");
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError databaseError) {
                System.out.println("ERROREEEE");
            }
        });

        chiudi.setOnClickListener(v -> {
            Utility.oneLineDialog(ActivityGame.this, this.getString(R.string.leavegame), () -> {
                //Elimino la stanza dal db
                FirebaseClass.deleteFieldFirebase(null,codiceStanza);

                //Lo riporto nella homepage
                Utility.goTo(ActivityGame.this, MainActivity.class);
            });
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

        if(multiplayer && attesa && !finishAttesa){
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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
/*
        switch(id){
            case R.id.impostazioni:
                new Settings().createSettingsMenu(this);
                return true;

            case R.id.leavegame:
                Utility.confirmDenyDialog(this, this.getString(R.string.leavegame),
                        this.getString(R.string.confirmleavegame),
                        (dialog, which) -> Utility.goTo(Game.activity, MainActivity.class),
                        null);
                return true;
        }
*/
        return super.onOptionsItemSelected(item);
    }
}