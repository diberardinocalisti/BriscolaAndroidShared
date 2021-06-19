package gameEngine;

import android.content.Context;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import java.util.ArrayList;

public class Game {
    public static AppCompatActivity activity;
    public static final Integer nGiocatori = 2, nCarte = 3, maxPunti = 120, scoreLimit = 3;
    public static final String[] semi = {"bastoni", "denara", "spade", "coppe"};
    public static Carta briscola;

    public static final Integer I_BRISCOLA = 6, I_MAZZO = 7;
    public static final Integer[] I_CAMPO_GIOCO = new Integer[] {8,9};

    public static Giocatore[] giocatori;
    public static CPU CPU;
    public static Giocatore user;

    // Tutte le carte presenti nel campo di gioco;
    protected static View[] carte;

    // Tutte le carte dei giocatori (quelle che possono essere giocate);
    protected static Button[] carteBottoni;

    public static ArrayList<Carta> mazzo;
    protected static Carta[] mazzoIniziale;

    protected static Giocatore giocante, ultimoVincitore;
    protected static boolean canPlay, lastManche, terminata;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initialize(AppCompatActivity activity){
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

        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());

            carte[i] = activity.findViewById(id);

            if(i < nCarte * nGiocatori){
                carte[i].setOnClickListener(new onClick());
                carteBottoni[i] = (Button) carte[i];
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity){
        initialize(activity);
        Engine.inizializza();
    }

}
