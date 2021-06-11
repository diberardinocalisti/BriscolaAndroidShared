package com.example.briscolav10;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import Home.MainActivity;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Utility;
import static gameEngine.Game.activity;

public class postPartita extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postpartita);
        Bundle extras = getIntent().getExtras();

        View postpartita = findViewById(R.id.postpartita);
        TextView esito = findViewById(R.id.esito);
        TextView punti = findViewById(R.id.nPunti);
        Button restart = findViewById(R.id.restart);
        Button exit = findViewById(R.id.exit);

        int punteggio = extras.getInt("punteggio");
        String daMostrare[] = extras.getStringArray("carte");

        String stato;
        if(punteggio < Game.maxPunti/2)
            stato = "sconfitta";
        else if(punteggio == Game.maxPunti/2)
            stato = "pareggio";
        else stato = "vittoria";

        // Aggiorna lo sfondo in base all'esito della partita (blu/rosso/grigio);
        int resID = activity.getResources().getIdentifier(stato, "drawable", activity.getPackageName());
        postpartita.setBackground(activity.getResources().getDrawable(resID));

        // Scrive lo stato della partita (vittoria/sconfitta/pareggio);
        esito.setText(stato.toUpperCase() + "!");

        // Scrive i punti ottenuti;
        punti.setText(("+" + punteggio + " punti").toUpperCase());

        for(int i = 0; i < daMostrare.length; i++){
            Carta carta = Engine.getCartaFromName(daMostrare[i]);
            String idS = "carta" + (i+1);

            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
            ImageView bottone = findViewById(id);

            bottone.setImageResource(carta.getImage(0));
        }

        restart.setOnClickListener(v -> {
            Intent i = new Intent(this, ActivityGame.class);
            i.putExtra("multiplayer", ActivityGame.multiplayer);
            this.startActivity(i);
        });

        exit.setOnClickListener(v -> Utility.goTo(this, MainActivity.class));
    }
}
