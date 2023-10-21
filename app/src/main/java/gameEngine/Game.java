package gameEngine;

import android.os.Build;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import game.danielesimone.briscola.R;
import multiplayer.engineMultiplayer;

import static gameEngine.ActivityGame.*;
import static gameEngine.Game.activity;

public class Game {
    public final static String GAME_VERSION = "2.5";
    public final static Object gameLocker = new Object();
    public static AppCompatActivity activity;

    public static final Integer viewAnimDuration = 350, accelMultip = 2, textAnimDuration = 250, fadeAnimDuration = 1000;
    public static final Integer intermezzo = 750, intermezzoCPU = 650, intermezzoManche = 100;
    public static final Integer nGiocatori = 2, nCarte = 3, maxPunti = 120, dimensioneMazzo = 40;
    public static short lastManche = 0;

    public static String tipoCarte;
    public static final String[] semi = {"bastoni", "denara", "spade", "coppe"};
    public static final Integer I_BRISCOLA = 6, I_MAZZO = 7;
    public static final int[][] I_CAMPO_GIOCO = new int[][]{
            {8,9},
            {10,11}
    };

    public static ArrayList<Carta> mazzo;
    public static Carta[] mazzoIniziale;
    public static Carta briscola;
    public static Timer timer;

    public static TextView centerText;
    public static View[] carte;
    public static View bottoneBriscola;
    public static Button[] carteBottoni;

    public static Giocatore[] giocatori;
    public static Giocatore user, opp, giocante, ultimoVincitore;
    public static CPU CPU;

    public static boolean terminata = true, cartaGiocata, difficoltàScelta = false;
    private static boolean canPlay;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initialize(AppCompatActivity activity){
        activity.setContentView(R.layout.campo_da_gioco);

        Utility.enableTopBar(activity);
        Utility.ridimensionamento(activity, activity.findViewById(R.id.campo_parent));

        Game.activity = activity;
        gameEngine.CPU.difficulties = activity.getResources().getStringArray(R.array.difficulties);

        giocatori = new Giocatore[nGiocatori];
        mazzo = new ArrayList<>();
        mazzoIniziale = new Carta[dimensioneMazzo];
        carte = new View[12];
        carteBottoni = new Button[nCarte * 2];
        tipoCarte = SharedPref.getTipoCarte().toLowerCase();

        centerText = activity.findViewById(R.id.avviso);
        bottoneBriscola = activity.findViewById(R.id.showbriscola);

        giocante = null;
        ultimoVincitore = null;
        CPU = null;

        canPlay = false;
        terminata = false;
        leftGame = false;
        cartaGiocata = false;
        lastManche = 0;

        timer = new Timer();
        timer.start();

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

        bottoneBriscola.setVisibility(View.INVISIBLE);
        bottoneBriscola.setOnClickListener(v -> Engine.tempShowBriscola());

        engineMultiplayer.createChat();
        ImageView chatIcon = activity.findViewById(R.id.chat);
        if(ActivityGame.multiplayer){
            chatIcon.setVisibility(View.VISIBLE);
            chatIcon.setOnClickListener(v -> engineMultiplayer.openChat());
        }else{
            chatIcon.setVisibility(View.INVISIBLE);
        }

        Utility.showAd(activity);
        Utility.enableTopBar(activity);
        Engine.pulisciTavolo();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void startGame(AppCompatActivity activity) {
        initialize(activity);
        Engine.inizializza();
    }

    public static void disableActions(){
        canPlay = false;
    }

    public static void enableActions(){
        canPlay = true;
        synchronized (gameLocker){
            gameLocker.notifyAll();
        }
    }

    public static boolean areActionsDisabled(){
        return !canPlay;
    }
}
