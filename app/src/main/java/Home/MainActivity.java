package Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.LoginButton;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.ArrayList;

import UI.BottomDialog;
import firebase.FirebaseClass;
import game.danielesimone.briscola.ActivityGame;
import game.danielesimone.briscola.R;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;
import multiplayer.engineMultiplayer;

import static Home.LoadingScreen.gameRunning;
import static Login.LoginActivity.fbUID;
import static Login.loginClass.*;

public class MainActivity extends AppCompatActivity {

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPref.setContext(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        gameRunning = true;

        ActivityMultiplayerGame.onStop = false;
        Game.terminata = true;

        setListeners();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            boolean showRateApp = extras.getBoolean("askRateApp");
            if(showRateApp)
                rateApp();
        }
    }

    protected void rateApp(){
        Utility.oneLineDialog(this, this.getString(R.string.rateapptitle), () -> {
            final String link = "https://play.google.com/store/apps/details?id=%s";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(link, getPackageName())));
            startActivity(browserIntent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setListeners(){
        Button singleplayer = findViewById(R.id.singleplayer);
        Button multiplayer = findViewById(R.id.multiplayer);
        Button mioProfilo = findViewById(R.id.mioprofilo);
        Button closeGame = findViewById(R.id.closegame);
        ImageButton info = findViewById(R.id.info);
        ImageButton contact = findViewById(R.id.contact);
        ImageButton history = findViewById(R.id.history);
        ImageButton ranking = findViewById(R.id.ranking);
        ImageButton friends = findViewById(R.id.friends);

        singleplayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityGame.class);
            intent.putExtra("multiplayer",false);
            this.startActivity(intent);
        });

        multiplayer.setOnClickListener(v -> {
            if(isLoggedIn()){
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

        ranking.setOnClickListener(v -> new Ranking(this));
        history.setOnClickListener(v -> new Storico(this));
        friends.setOnClickListener(v -> {
            if(isFacebookLoggedIn()){
                new Friends(this);
            }else{
                String message = this.getString(R.string.mustbeloggedwithfacebook);
                Utility.oneLineDialog(this, message, () -> {
                    LoginActivity.doLogout();
                    Utility.goTo(this, LoginActivity.class);
                });
            }
        });
        closeGame.setOnClickListener(v -> this.onBackPressed());
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

        if(LoginActivity.login && isFacebookLoggedIn()){
            FirebaseClass.editFieldFirebase(fbUID,"nome", loginClass.getFBNome());
            FirebaseClass.editFieldFirebase(fbUID,"cognome", loginClass.getFBCognome());
            LoginActivity.login = false;
        }

        if(!ActivityGame.multiplayer)
            return;

        if(!ActivityGame.leftGame)
            engineMultiplayer.accediHost(this, engineMultiplayer.codiceStanza);
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
