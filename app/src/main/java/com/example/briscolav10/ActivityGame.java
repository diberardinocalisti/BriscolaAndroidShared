package com.example.briscolav10;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.ProfilePictureView;

import gameEngine.Game;
import gameEngine.Settings;

import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.setImgProfile;

public class ActivityGame extends AppCompatActivity {

    ProfilePictureView imgP;
    ImageButton impostazioni;
    ActivityGame a;
    public static boolean multiplayer = false;
    public static boolean attesa = false;
    private static final int STANZA_ATTESA_ID = 1300113;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        multiplayer = extras.getBoolean("multiplayer");

        //Se l'utente ha scelto modalità singlePlayer
        if(!multiplayer)
        {
            setContentView(R.layout.campo_da_gioco);
            multiplayer = false;
            attesa = false;

            imgP = findViewById(R.id.friendProfilePictureUSER);
            impostazioni = findViewById(R.id.impostazioni);

            if(isFacebookLoggedIn())
            {
                setImgProfile(imgP);
            }

            impostazioni.setOnClickListener(v -> {
                Settings s = new Settings();
                s.createSettingsMenu(ActivityGame.this);
            });

            Game.startGame(this);
        }else
        {
            setContentView(R.layout.stanza_di_attesa);
            attesa = true;

            Button waiting;
            waiting = findViewById(R.id.waiting);
        }
        View contentView = this.findViewById(android.R.id.content).getRootView();

    }


    @Override
    protected void onStop() {
        super.onStop();

        if(multiplayer && attesa)
        {
            Toast.makeText(getApplicationContext(),"CHIUSA",Toast.LENGTH_LONG).show();
        }

    }
}