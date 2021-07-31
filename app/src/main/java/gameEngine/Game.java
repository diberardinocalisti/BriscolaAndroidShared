package gameEngine;

import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import java.util.ArrayList;

import Home.SharedPref;

public class Game {
    public static AppCompatActivity activity;
    public static final Integer viewAnimDuration = 350, accelMultip = 2, textAnimDuration = 250;
    public static final Integer intermezzo = 750;
    public static final Integer intermezzoCPU = 650;
    public static final Integer intermezzoManche = 100;
    public static final Integer nGiocatori = 2, nCarte = 3, maxPunti = 120, dimensioneMazzo = 40;
    public static final String[] semi = {"bastoni", "denara", "spade", "coppe"};
    public static String tipoCarte;
    public static Carta briscola;
    public static TextView centerText;

    public static final Integer I_BRISCOLA = 6, I_MAZZO = 7;
    public static final int[][] I_CAMPO_GIOCO = new int[][]{
            {8,9},
            {10,11}
    };

    public static Giocatore[] giocatori;
    public static CPU CPU;
    public static Giocatore user, opp;

    // Tutte le carte presenti nel campo di gioco;
    public static View[] carte;

    // Tutte le carte dei giocatori (quelle che possono essere giocate);
    public static Button[] carteBottoni;

    public static ArrayList<Carta> mazzo;
    public static Carta[] mazzoIniziale;

    public static Giocatore giocante, ultimoVincitore;
    public static boolean canPlay, terminata = true;
    public static short lastManche = 0;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initialize(AppCompatActivity activity){
        Utility.enableTopBar(activity);
        Utility.ridimensionamento(activity, activity.findViewById(R.id.campogioco));

        Game.activity = activity;

        giocatori = new Giocatore[nGiocatori];
        mazzo = new ArrayList<>();
        giocante = null;
        ultimoVincitore = null;
        CPU = null;
        canPlay = true;
        lastManche = 0;
        terminata = false;
        carte = new View[12];
        carteBottoni = new Button[nCarte * 2];
        mazzoIniziale = new Carta[dimensioneMazzo];
        tipoCarte = SharedPref.getTipoCarte().toLowerCase();
        centerText = activity.findViewById(R.id.avviso);

        Engine.aggiornaNCarte(mazzoIniziale.length);

        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());

            carte[i] = activity.findViewById(id);
            carte[i].setVisibility(View.INVISIBLE);

            if(i < nCarte * nGiocatori){
                carteBottoni[i] = (Button) carte[i];
                carteBottoni[i].setOnClickListener(null);
            }
        }

        Utility.enableTopBar(activity);
        Engine.pulisciTavolo();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity) {
        initialize(activity);
        Engine.inizializza();
    }
}
