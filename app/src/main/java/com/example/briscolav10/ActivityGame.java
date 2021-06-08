package com.example.briscolav10;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Settings;
import multiplayer.Game.ActivityMultiplayerGame;

import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.setImgProfile;
import static multiplayer.engineMultiplayer.codiceStanza;

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
                            if(value != "null")
                            {
                                Intent i = new Intent(ActivityGame.this,ActivityMultiplayerGame.class);
                                ActivityGame.this.startActivity(i);
                                Toast.makeText(getBaseContext(),value + " si è unito alla partita!",Toast.LENGTH_LONG).show();
                            }
                        }else
                            System.out.println("NULL");
                    }
                }

                @Override
                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError databaseError) {

                }


            });
        }

        //View contentView = this.findViewById(android.R.id.content).getRootView();

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