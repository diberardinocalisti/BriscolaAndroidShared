package com.example.briscolav10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
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
import androidx.core.content.ContextCompat;

import Home.MainActivity;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Utility;

import static gameEngine.Game.activity;
import static gameEngine.Game.maxPunti;
import static gameEngine.Game.nGiocatori;

public class postPartita extends AppCompatActivity {
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.postpartita);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        Bundle extras = getIntent().getExtras();

        View postpartita = findViewById(R.id.parent);
        TextView esito = findViewById(R.id.esito);
        TextView punti = findViewById(R.id.nPunti);
        Button restart = findViewById(R.id.restart);
        Button exit = findViewById(R.id.exit);

        int punteggio = extras.getInt("punteggio");
        String[] daMostrare = extras.getStringArray("carte");

        String background, stato;
        if(punteggio < maxPunti/nGiocatori) {
            background = "sconfitta";
            stato = this.getResources().getString(R.string.lost);
        }else if(punteggio == maxPunti/nGiocatori) {
            background = "pareggio";
            stato = this.getResources().getString(R.string.tie);
        }else{
            background = "vittoria";
            stato = this.getResources().getString(R.string.win);
        }

        // Aggiorna lo sfondo in base all'esito della partita (blu/rosso/grigio);
        int resID = activity.getResources().getIdentifier(background, "drawable", activity.getPackageName());

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            postpartita.setBackgroundDrawable(ContextCompat.getDrawable(activity, resID) );
        }else{
            postpartita.setBackground(ContextCompat.getDrawable(activity, resID));
        }

        // Scrive lo stato della partita (vittoria/sconfitta/pareggio);
        esito.setText(stato);

        // Scrive i punti ottenuti;
        punti.setText("+" + punteggio);

        for(int i = 0; i < daMostrare.length; i++){
            Carta carta = Engine.getCartaFromName(daMostrare[i]);
            String idS = "carta" + (i+1);

            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
            ImageView bottone = findViewById(id);

            bottone.setBackground(Carta.getVuoto());
            new Thread(() -> {
                try {
                    Thread.sleep(Game.viewAnimDuration);
                    activity.runOnUiThread(() -> Engine.flipAnim(bottone, carta.getImage(), false, null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        restart.setOnClickListener(v -> {
            Intent i = new Intent(this, ActivityGame.class);
            i.putExtra("multiplayer", ActivityGame.multiplayer);
            this.startActivity(i);
        });

        exit.setOnClickListener(v -> Utility.goTo(this, MainActivity.class));
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
