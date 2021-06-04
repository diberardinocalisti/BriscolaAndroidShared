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

    protected static Giocatore[] giocatori;
    protected static Button[] carte, carteBottoni;
    protected static ArrayList<Carta> mazzo;
    protected static Giocatore giocante, ultimoVincitore;
    protected static boolean canPlay = true, lastManche = false, terminata = false;
    protected static Short endEvent;

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
        endEvent = 0;
        carte = new Button[10];
        carteBottoni = new Button[nCarte * 2];

        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());

            carte[i] = activity.findViewById(id);

            if(i < nCarte * 2){
                carte[i].setOnClickListener(new onClick());
                carteBottoni[i] = carte[i];
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity){
        //initialize(activity);
        /**
         * Dovrebbe invocare il metodo "inizializza" ma non è ancora pronto,
         * per ora viene mostrata la prima carta del mazzo quando si clicca su un bottone.
         */
        //Engine.inizializza();
        Engine.creaGiocatori();
        Engine.creaMazzo();
    }
}
