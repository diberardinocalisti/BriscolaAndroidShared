package game.danielesimone.briscola;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscola.R;

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
import multiplayer.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;

import static Login.loginClass.getFBUserId;
import static Login.loginClass.getFullFBName;
import static multiplayer.engineMultiplayer.codiceStanza;

public class ActivityGame extends AppCompatActivity {
    public static boolean multiplayer = false;
    public static boolean attesa = false;
    public static boolean finishAttesa = false;
    public static boolean leftGame = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Bundle extras = getIntent().getExtras();
        multiplayer = extras.getBoolean("multiplayer");
        leftGame = false;

        Utility.enableTopBar(this);

        if(multiplayer) {
            startMultiPlayer();
        } else {
            startSinglePlayer();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void startSinglePlayer() {
        multiplayer = false;
        attesa = false;

        Game.startGame(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("SetTextI18n")
    protected void startMultiPlayer() {
        if(ActivityMultiplayerGame.onStop){
            Utility.goTo(ActivityGame.this, MainActivity.class);
            ActivityMultiplayerGame.onStop = false;
        }

        setContentView(R.layout.stanza_di_attesa);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        Utility.showAd(this);

        attesa = true;

        TextView codice = findViewById(R.id.codice);
        TextView stato = findViewById(R.id.stato);
        TextView nomeHost = findViewById(R.id.nome1);
        ImageView picHost = findViewById(R.id.icon);
        Button chiudi = findViewById(R.id.chiudisala);

        nomeHost.setText(loginClass.getName());
        codice.setText(this.getString(R.string.code) + codiceStanza);
        stato.setText(this.getString(R.string.state) + this.getString(R.string.waiting));
        loginClass.setImgProfile(this, getFBUserId(), picHost);

        chiudi.setOnClickListener(v -> ActivityGame.this.onBackPressed());

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals("enemy")){
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

        if(multiplayer && ActivityMultiplayerGame.onStop){
           Utility.goTo(ActivityGame.this, MainActivity.class);
           ActivityMultiplayerGame.onStop = false;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onStop() {
        super.onStop();

        if(multiplayer && attesa && !finishAttesa){
            FirebaseClass.deleteFieldFirebase(null, codiceStanza);
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
        Utility.oneLineDialog(this, this.getString(R.string.confirmleavegame), () -> {
            leftGame = true;
            Utility.goTo(this, multiplayer ? MultiplayerActivity.class : MainActivity.class);
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}