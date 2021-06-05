package gameEngine;

import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class Game {
    public static AppCompatActivity activity;
    public static final Integer nGiocatori = 2, nCarte = 3;
    public static final String[] semi = {"bastoni", "denara", "spade", "coppe"};
    public static boolean carteScoperte = false;
    public static Integer scoreLimit = 3;
    public static Carta briscola;

    public static final Integer I_BRISCOLA = 6;
    public static final Integer[] I_CAMPO_GIOCO = new Integer[] {8,9};

    protected static Giocatore[] giocatori;

    // Tutte le carte presenti nel campo di gioco;
    protected static Button[] carte;

    // Tutte le carte dei giocatori (quelle che possono essere giocate);
    protected static Button[] carteBottoni;

    protected static ArrayList<Carta> mazzo;
    protected static Giocatore giocante, ultimoVincitore;
    protected static boolean canPlay = true, lastManche = false, terminata = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    private static void initialize(AppCompatActivity activity){
        Game.activity = activity;

        giocatori = new Giocatore[nGiocatori];
        mazzo = new ArrayList<>();
        giocante = null;
        ultimoVincitore = null;
        canPlay = true;
        lastManche = false;
        terminata = false;
        carte = new Button[10];
        carteBottoni = new Button[nCarte * 2];

        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());

            carte[i] = activity.findViewById(id);

            if(i < nCarte * nGiocatori){
                carte[i].setOnClickListener(new onClick());
                carteBottoni[i] = carte[i];
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity){
        initialize(activity);
        Engine.inizializza();

        for(Giocatore g : giocatori){
            for(Carta c : g.carte)
                System.out.println(c.getButton().getId());
        }
    }
}
