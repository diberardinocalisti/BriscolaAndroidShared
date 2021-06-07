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

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        imgP = (ProfilePictureView) findViewById(R.id.friendProfilePicture);

        impostazioni = (ImageButton) findViewById(R.id.impostazioni);

        if(isFacebookLoggedIn())
        {
            imgP.setVisibility(View.VISIBLE);
            setImgProfile(imgP);
        }else
        {
            imgP.setVisibility(View.INVISIBLE);
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