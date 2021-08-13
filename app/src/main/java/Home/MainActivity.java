package Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import firebase.FirebaseClass;
import game.danielesimone.briscola.ActivityGame;
import game.danielesimone.briscola.R;
import com.facebook.AccessToken;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Game;
import game.danielesimone.briscola.Storico;
import gameEngine.Utility;
import multiplayer.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;
import multiplayer.engineMultiplayer;

import static Home.LoadingScreen.gameRunning;

public class MainActivity extends AppCompatActivity {
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        gameRunning = true;

        if(loginClass.isFacebookLoggedIn()){
            AccessToken accessToken = AccessToken.getCurrentAccessToken();
            LoginActivity.fbUID = accessToken.getUserId();
        }

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        ActivityMultiplayerGame.onStop = false;
        Game.terminata = true;
        SharedPref.setContext(this);

        setListeners();
    }

    protected void setListeners(){
        Button singleplayer = findViewById(R.id.singleplayer);
        Button multiplayer = findViewById(R.id.multiplayer);
        Button mioProfilo = findViewById(R.id.mioprofilo);
        Button closeGame = findViewById(R.id.closegame);
        ImageButton info = findViewById(R.id.info);
        ImageButton contact = findViewById(R.id.contact);
        ImageButton history = findViewById(R.id.history);

        singleplayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityGame.class);
            intent.putExtra("multiplayer",false);
            this.startActivity(intent);
        });

        multiplayer.setOnClickListener(v -> {
            if(loginClass.isFacebookLoggedIn()){
                Intent i = new Intent(this, MultiplayerActivity.class);
                this.startActivity(i);
            }else{
                Intent i = new Intent(this, LoginActivity.class);
                this.startActivity(i);
            }
        });

        info.setOnClickListener(v -> {
            String title = this.getString(R.string.intro);
            String msg = this.getString(R.string.howtoplay);
            Utility.createDialog(this, title, msg);
        });

        mioProfilo.setOnClickListener(v -> {
            Intent in = new Intent(this, LoginActivity.class);
            this.startActivity(in);
        });

        contact.setOnClickListener(v -> {
            String title = this.getString(R.string.contactus);
            String msg = this.getString(R.string.contactustxt);
            Utility.createDialog(this, title, msg);
        });

        history.setOnClickListener(v -> Utility.goTo(this, Storico.class));
        closeGame.setOnClickListener(v -> this.onBackPressed());
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
        Utility.oneLineDialog(this, this.getString(R.string.confirmleave), MainActivity.super::onBackPressed);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(ActivityGame.multiplayer && ActivityMultiplayerGame.onStop)
            ActivityMultiplayerGame.onStop = false;

        if(LoginActivity.login){
            FirebaseClass.editFieldFirebase(LoginActivity.fbUID,"nome", loginClass.getFBNome());
            FirebaseClass.editFieldFirebase(LoginActivity.fbUID,"cognome", loginClass.getFBCognome());
            LoginActivity.login = false;
        }

        if(!ActivityGame.multiplayer)
            return;

        if(!ActivityGame.leftGame)
            engineMultiplayer.accediHost(this, engineMultiplayer.codiceStanza);
    }
}
