package gameEngine;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;

import Home.SharedPref;

import static gameEngine.Game.activity;

public class Game {
    public static AppCompatActivity activity;
    public static final Integer viewAnimDuration = 350, accelMultip = 2;
    public static final Integer intermezzo = 750;
    public static final Integer intermezzoCPU = 1500;
    public static final Integer nGiocatori = 2, nCarte = 3, maxPunti = 120, dimensioneMazzo = 40;
    public static final String[] semi = {"bastoni", "denara", "spade", "coppe"};
    public static String tipoCarte;
    public static Carta briscola;
    public static TextView centerText;

    public static final Integer I_BRISCOLA = 6, I_MAZZO = 7;
    public static final Integer[] I_CAMPO_GIOCO = new Integer[] {8,9};

    public static Giocatore[] giocatori;
    public static CPU CPU;
    public static Giocatore user;

    // Tutte le carte presenti nel campo di gioco;
    public static View[] carte;

    // Tutte le carte dei giocatori (quelle che possono essere giocate);
    public static Button[] carteBottoni;

    public static ArrayList<Carta> mazzo;
    public static Carta[] mazzoIniziale;

    public static Giocatore giocante, ultimoVincitore;
    public static boolean canPlay, lastManche, terminata;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initialize(AppCompatActivity activity){
        Utility.enableTopBar(activity);
        Utility.ridimensionamento(activity, activity.findViewById(R.id.parent));

        Game.activity = activity;

        giocatori = new Giocatore[nGiocatori];
        mazzo = new ArrayList<>();
        canPlay = true;
        lastManche = false;
        terminata = false;
        giocante = null;
        ultimoVincitore = null;
        CPU = null;
        canPlay = true;
        lastManche = false;
        terminata = false;
        carte = new View[10];
        carteBottoni = new Button[nCarte * 2];
        mazzoIniziale = new Carta[40];
        tipoCarte = SharedPref.getTipoCarte().toLowerCase();
        centerText = activity.findViewById(R.id.avviso);

        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());

            carte[i] = activity.findViewById(id);

            if(i < nCarte * nGiocatori){
                carte[i].setOnClickListener(new onClick());
                carteBottoni[i] = (Button) carte[i];
                carteBottoni[i].setVisibility(View.INVISIBLE);
            }
        }

        Engine.pulisciTavolo();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity) {
        initialize(activity);
        Engine.inizializza();
    }
}
