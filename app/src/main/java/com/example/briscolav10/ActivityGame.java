package com.example.briscolav10;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Home.MainActivity;
import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Settings;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

import static Login.loginClass.getFBNome;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.setImgProfile;
import static multiplayer.engineMultiplayer.codiceStanza;

public class ActivityGame extends AppCompatActivity {
    private final int STANZA_ATTESA_ID = 1300113;

    ProfilePictureView imgP;
    ImageButton impostazioni;
    ActivityGame a;

    public boolean multiplayer = false;
    public boolean attesa = false;
    private String codice_stanza;
    public boolean finishAttesa = false;    //Se mando l'utente alla pagina del gioco è comunque onmStop() e quindi verrebbe eliminata la staanza

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

            imgP = findViewById(R.id.friendProfilePicture2);
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

            ((TextView) findViewById(R.id.codice)).setText("Codice: " + codiceStanza);

            ((TextView) findViewById(R.id.nome1)).setText(getFBNome());

            Button chiudi = (Button) findViewById(R.id.chiudisala);

            loginClass.setImgProfile((ProfilePictureView) findViewById(R.id.friendProfilePicture1));


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
                                finishAttesa = true;
                                Intent i = new Intent(ActivityGame.this,ActivityMultiplayerGame.class);
                                ActivityGame.this.startActivity(i);
                                Toast.makeText(getBaseContext(),value + " si è unito alla partita!",Toast.LENGTH_LONG).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError databaseError) {

                }


            });

            chiudi.setOnClickListener(v -> {

                DialogInterface.OnClickListener action = (dialog, which) -> {

                    //Elimino la stanza dal db
                    FirebaseClass.deleteFieldFirebase(null,codiceStanza);

                    //Lo riporto nella homepage
                    Utility.goTo(ActivityGame.this, MainActivity.class);
                };

                Utility.confirmDialog(ActivityGame.this,"Conferma la chiusura della sessione","Sicuro di voler abbandonare la sessione?",action,null);

            });
        }

        //View contentView = this.findViewById(android.R.id.content).getRootView();

    }


    @Override
    protected void onPause() {
        super.onPause();

        if(multiplayer && attesa && !finishAttesa)
        {
            FirebaseClass.deleteFieldFirebase(null,codiceStanza);
            Toast.makeText(getApplicationContext(),"La sessione è stata interrotta!\nCrea una nuova stanza per giocare di nuovo",Toast.LENGTH_LONG).show();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        //Toast.makeText(getApplicationContext(),"ON DESTROY",Toast.LENGTH_LONG).show();
    }
}