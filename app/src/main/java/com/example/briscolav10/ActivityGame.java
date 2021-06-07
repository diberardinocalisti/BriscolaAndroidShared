package com.example.briscolav10;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        Bundle extras = getIntent().getExtras();
        multiplayer = extras.getBoolean("multiplayer");

        imgP = (ProfilePictureView) findViewById(R.id.friendProfilePicture2);

        impostazioni = (ImageButton) findViewById(R.id.impostazioni);

        if(isFacebookLoggedIn())
        {
            setImgProfile(imgP);
        }

        impostazioni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Settings s = new Settings();
                s.createSettingsMenu(ActivityGame.this);
            }
        });

        Game.startGame(this);

    }
}