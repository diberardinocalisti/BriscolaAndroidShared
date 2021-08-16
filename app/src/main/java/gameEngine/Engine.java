package gameEngine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import firebase.FirebaseClass;
import game.danielesimone.briscola.ActivityGame;
import game.danielesimone.briscola.R;
import game.danielesimone.briscola.postPartita;
import multiplayer.ActivityMultiplayerGame;
import multiplayer.GiocatoreMP;
import multiplayer.engineMultiplayer;

import static Login.loginClass.getFBUserId;
import static Login.loginClass.getFullFBName;
import static Login.loginClass.isFacebookLoggedIn;
import static gameEngine.Game.I_BRISCOLA;
import static gameEngine.Game.I_CAMPO_GIOCO;
import static gameEngine.Game.I_MAZZO;
import static gameEngine.Game.accelMultip;
import static gameEngine.Game.activity;
import static gameEngine.Game.briscola;
import static gameEngine.Game.canPlay;
import static gameEngine.Game.cartaGiocata;
import static gameEngine.Game.carte;
import static gameEngine.Game.centerText;
import static gameEngine.Game.dimensioneMazzo;
import static gameEngine.Game.giocante;
import static gameEngine.Game.giocatori;
import static gameEngine.Game.intermezzo;
import static gameEngine.Game.intermezzoManche;
import static gameEngine.Game.lastManche;
import static gameEngine.Game.mazzo;
import static gameEngine.Game.mazzoIniziale;
import static gameEngine.Game.nCarte;
import static gameEngine.Game.nGiocatori;
import static gameEngine.Game.semi;
import static gameEngine.Game.terminata;
import static gameEngine.Game.ultimoVincitore;
import static gameEngine.Game.user;
import static gameEngine.Game.viewAnimDuration;
import static multiplayer.engineMultiplayer.codiceStanza;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Engine{
    public static void inizializza() {
        creaGiocatori(Engine::iniziaRound);
    }

    static void iniziaRound() {
        inizia();
        pulisciTavolo();
        creaMazzo();

        Giocatore[] randomTurno = getRandomOrdine();
        distribuisciCarte(randomTurno);
    }

    static void inizia(){
        canPlay = false;
        terminata = false;
    }

    public static void creaMazzo() {
        lastManche = 0;
        mazzo.clear();

        Carta.nascondi(carte[I_MAZZO]);

        for(String seme : semi)
            for(int i = 1; i <= dimensioneMazzo/semi.length; i++)
                mazzo.add(new Carta(i, seme));

        Collections.shuffle(mazzo);
        mazzoIniziale = mazzo.toArray(new Carta[0]);

        aggiornaNCarte(mazzo.size());
    }

    public static void creaMazzo(String carte){
        final String delCarte = ";";
        final String delTipo = "_";

        String[] carteSplit = carte.split(delCarte);
        Carta.nascondi(Game.carte[I_MAZZO]);

        for(String c : carteSplit){
            String[] strTok = c.split(delTipo);

            String tipo = strTok[0];
            int i = Integer.parseInt(strTok[1]);
            String seme = strTok[2];

            mazzo.add(new Carta(i, seme, tipo));
        }

        mazzoIniziale = mazzo.toArray(new Carta[0]);
    }

    public static void creaGiocatori(Runnable callback) {
        for(int i = 0; i < giocatori.length; i++){
            boolean CPU = i == 0;
            if(!CPU){
                String username = activity.getString(R.string.guest);

                if(isFacebookLoggedIn())
                    username = getFullFBName();

                giocatori[i] = new Giocatore(username, getFBUserId(), i);
            }else{
                giocatori[i] = new CPU("CPU", i);
            }
        }

        Engine.setOnCLickListener();

        // Mostrerà la dialog di sceltà della difficoltà solo la prima partita;
        if(!Game.difficoltàScelta){
            Game.difficoltàScelta = true;

            Utility.oneLineDialog(activity,
                    activity.getString(R.string.skillselect),
                    activity.getString(R.string.easy),
                    activity.getString(R.string.hard),

                    // First option;
                    () -> {
                        SharedPref.setCPUSkill(gameEngine.CPU.EASY);
                        callback.run();
                    },

                    // Second option;
                    () -> {
                        SharedPref.setCPUSkill(gameEngine.CPU.HARD);
                        callback.run();
                    },

                    // Dismiss option;
                    callback);
        }else{
            callback.run();
        }
    }

    public static void estraiBriscola(Runnable callback){
        Object event = new Object();

        if(Engine.getCarteGiocatori().length < nGiocatori * nCarte)
            return;

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();

                    activity.runOnUiThread(() -> {
                        briscola.setButton(carte[I_BRISCOLA]);
                        briscola.mostra();
                        if(callback != null)
                            callback.run();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        briscola = mazzo.get(0);
        briscola.nascondi();
        mazzo.remove(briscola);
        mazzo.add(briscola);
        briscola.setButton(carte[I_MAZZO]);
        muoviCarta(user.prendi, carte[I_BRISCOLA], briscola, false, true, true, event);
    }

    public static void distribuisciCarte(Giocatore[] turno){
        Giocatore giocante = turno[0];
        distribuisciCarte(() -> estraiBriscola(() -> {
            Runnable callback = null;

            if(giocante == Game.CPU)
                callback = () -> prossimoTurno(giocante);
            else
                prossimoTurno(giocante);

            if(!cartaGiocata)
                showMessage(getMessaggioTurno(giocante), callback);
        }), turno);
    }

    public static void distribuisciCarte(Runnable callback, Giocatore[] players) {
        Object event = new Object();

        new Thread(() -> {
            for(Giocatore p : players)
                p.mazzo.setVisibility(View.INVISIBLE);

            for(Giocatore p : players){
                activity.runOnUiThread(p::svuotaMazzo);

                while(p.n_carte() < nCarte) {
                    try {
                        Thread.sleep(intermezzo/2);
                        activity.runOnUiThread(p::pesca);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            synchronized (event){
                event.notifyAll();
            }
        }).start();

        new Thread(() -> {
            synchronized (event){
                try {
                    event.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                activity.runOnUiThread(callback);
            }
        }).start();
    }

    public static String getMessaggioTurno(Giocatore giocatore){
        int stringId = Game.user == giocatore ? R.string.tuoturno : R.string.turno;
        return activity.getString(stringId).replace("%user", giocatore.getNome());
    }

    public static void setOnCLickListener(){
        View.OnClickListener onClickListener = Engine::onClick;

        for(Button b : Game.user.bottoni)
            b.setOnClickListener(onClickListener);
    }

    public static void onClick(View v){
        final Button bottone = (Button) v;
        final Carta carta = Engine.getCartaFromButton(bottone);

        if(carta == null)
            return;

        if(carta.getPortatore() != giocante)
            return;

        if(!Game.canPlay || terminata)
            return;

        Game.canPlay = false;

        Object event = new Object();

        final View destButton = carte[carta.getPortatore().index + I_CAMPO_GIOCO[lastManche][0]];

        muoviCarta(bottone, destButton, false, true, false, event);
        clearText(centerText);

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();
                    activity.runOnUiThread(() -> {
                        if(giocante == null)
                            return;

                        Game.canPlay = true;

                        giocante.lancia(carta);
                        final Giocatore vincente = doLogic(carta, getOtherCarta(carta));

                        if(vincente == null) {
                            prossimoTurno(getOtherPlayer(giocante));
                        }else{
                            giocante = null;
                            new Handler().postDelayed(() -> terminaManche(vincente), intermezzo);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    public static void prossimoTurno(Giocatore p) {
        if(p == null)
            p = getRandomPlayer();

        try{
            p.toccaA();
        }catch(InterruptedException ignored){}
    }

    public static void terminaManche(Giocatore vincitore) {
        vincitore.mancheVinta(() -> new Handler().postDelayed(() -> {
            pulisciPianoGioco();

            Giocatore[] giocatori = getVincitorePerdente(vincitore);

            for(Giocatore p : giocatori)
                if(mazzo.size() > 0 || lastManche == 0)
                    p.pesca();

            if(isTerminata()) {
                termina();
            }else{
                prossimoTurno(vincitore);
            }
        }, intermezzoManche));
    }

    public static void lastManche(){
        pulisciPianoLaterale();
        centerText = activity.findViewById(R.id.avvisocentro);
        lastManche = 1;

        int stringId = Game.user == ultimoVincitore ? R.string.tuoturno : R.string.turno;
        String tocca = activity.getString(stringId).replace("%user", ultimoVincitore.getNome());;

        String msg = activity.getString(R.string.lastmanche) + "\n" + tocca;
        Utility.textAnimation(msg, centerText, () -> {
            clearText(centerText);

            if(Game.CPU != null){
                synchronized (Game.CPU){
                    Game.CPU.notifyAll();
                }
            }
        });
    }

    static void termina(){
        canPlay = false;
        terminata = true;

        terminaPartita();
    }

    static void terminaPartita(){
        Intent i = new Intent(activity, postPartita.class);

        i.putExtra("punteggio", user.getPunteggioCarte());
        i.putExtra("carte", user.mostraMazzo());

        if(ActivityGame.multiplayer){
            FirebaseClass.getFbRefSpeicific(codiceStanza).removeEventListener(ActivityMultiplayerGame.valueEventListener);
            FirebaseClass.deleteFieldFirebase(null, engineMultiplayer.codiceStanza);
            i.putExtra("ruolo", ((GiocatoreMP) user).getRuolo());
        }

        activity.startActivity(i);
    }

    public static void pulisciTavolo(){
        pulisciPianoGioco();
        pulisciPianoLaterale();
    }

    static void pulisciPianoGioco(){
        for(Integer i : I_CAMPO_GIOCO[lastManche])
            carte[i].setBackground(null);
    }

    static void pulisciPianoLaterale(){
        pulisciBriscola();
        pulisciMazzo();
        pulisciPrese();
    }

    public static void pulisciMazzo(){
        carte[I_MAZZO].setBackground(null);
    }

    public static void pulisciBriscola(){
        carte[I_BRISCOLA].setBackground(null);
    }

    public static void pulisciPrese(){
        for(Giocatore p : giocatori)
            if(p != null)
                p.prendi.setBackground(null);
    }

    static Giocatore[] getRandomOrdine(){
        Giocatore[] ordine = new Giocatore[nGiocatori];
        ordine[0] = getRandomPlayer();
        ordine[1] = ordine[0] == giocatori[0] ? giocatori[1] : giocatori[0];
        return ordine;
    }

    static Giocatore getRandomPlayer(){
        return giocatori[(int) (Math.random() * giocatori.length)];
    }

    public static Giocatore getPlayerById(String id){
        for(Giocatore p : giocatori)
            if(p.getId().equals(id))
                return p;

        return null;
    }

    public static Giocatore getOtherPlayer(Giocatore current){
        for(Giocatore p : giocatori)
            if(p != current)
                return p;

        return null;
    }

    public static Carta getCartaFromButton(View button){
        for(Carta carta : mazzoIniziale){
            if(carta == null)
                continue;

            if(carta.getButton() == null)
                continue;

            if(carta.getButton().getId() == button.getId())
                return carta;
        }
        return null;
    }

    public static Carta getCartaFromName(String nome){
        for(Carta carta : mazzoIniziale){
            if(carta == null)
                continue;

            if(carta.getNome().equals(nome))
                return carta;
        }

        return null;
    }

    static Carta[] getCarteGiocatori(){
        ArrayList<Carta> carte = new ArrayList<>();

        for(Giocatore p : giocatori)
            for(Carta c : p.carte)
                if(c != null)
                    carte.add(c);

        return carte.toArray(carte.toArray(new Carta[0]));
    }

    public static Carta getOtherCarta(Carta current){
        for(Integer i : I_CAMPO_GIOCO[lastManche]) {
            Carta c = getCartaFromButton(carte[i]);

            if(c == null)
                continue;

            if(isLibero(carte[i]))
                continue;

            if(!c.getNome().equals(current.getNome()))
                return c;
        }

        return null;
    }

    public static Comparator<Carta> ordinaCarte = Comparator.comparingInt(Carta::getValore);

    static boolean isTerminata(){
        return getCarteGiocatori().length == 0;
    }

    static boolean isLastManche(){
        return Game.mazzo.size() == 0;
    }

    static boolean isPenultimaManche(){
        return Game.mazzo.size() == nGiocatori;
    }

    static boolean isLibero(View b){
        return b.getBackground() == null || b.getVisibility() == View.INVISIBLE;
    }

    static boolean isCenterTextShown(){
        return !centerText.getText().toString().equals("");
    }

    public static Giocatore doLogic(Carta last, Carta first) {
        if(last != null && first == null)
            return null;

        Carta[] carte = {first, last};
        Carta comanda = first;

        for(Carta carta : carte)
            if(carta.isBriscola())
                comanda = carta;

        Carta c_vincente = first.getSeme().equals(last.getSeme()) ? getMax(carte) : comanda;
        return c_vincente.getPortatore();
    }

    public static Carta getMax(Carta[] array) {
        Carta max = null;

        for (Carta carta : array) {
            if (carta == null)
                continue;

            if (max == null) {
                max = carta;
                continue;
            }

            if (carta.getValore() > max.getValore()) {
                max = carta;
            } else if (carta.getValore() == max.getValore()) {
                if (carta.getNumero() > max.getNumero()) {
                    max = carta;
                }
            }
        }

        return max;
    }

    public static Carta getMin(Carta[] array) {
        Carta min = null;

        for (Carta carta : array) {
            if (carta == null)
                continue;

            if (min == null) {
                min = carta;
                continue;
            }

            if (carta.getValore() < min.getValore()) {
                min = carta;
            } else if (carta.getValore() == min.getValore()) {
                if (carta.getNumero() < min.getNumero()) {
                    min = carta;
                }
            }
        }

        return min;
    }

    public static Carta[] getCarteGiocate(){
        ArrayList<Carta> carteGiocate = new ArrayList<>();

        for(Integer i : I_CAMPO_GIOCO[lastManche]){
            Carta c = getCartaFromButton(carte[i]);
            if(c != null)
                carteGiocate.add(c);
        }

        return carteGiocate.toArray(new Carta[0]);
    }

    public static void aggiornaNCarte(){
        aggiornaNCarte(mazzo.size());
    }

    public static void aggiornaNCarte(Integer n_Carte){
        TextView icona = activity.findViewById(R.id.n_carte);

        if(n_Carte == 0) {
            icona.setVisibility(View.INVISIBLE);
        }else{
            icona.setText(n_Carte.toString());
            icona.setVisibility(View.VISIBLE);
        }
    }

    public static void aggiornaTipoCarte(String tipoCarte){
        tipoCarte = tipoCarte.toLowerCase();
        SharedPref.setTipoCarte(tipoCarte);

        if(ActivityGame.multiplayer)
            return;

        Game.tipoCarte = tipoCarte;

        if(terminata)
            return;

        for(Carta c : mazzoIniziale)
            c.aggiornaTipo();

        for(Carta c : mazzo)
            c.aggiornaTipo();

        for(View c : carte) {
            Carta carta = getCartaFromButton(c);
            if(carta != null)
                carta.aggiornaTipo();
        }
    }

    public static Giocatore[] getVincitorePerdente(Giocatore vincente){
        Giocatore[] arr = new Giocatore[giocatori.length];

        arr[0] = vincente;
        arr[1] = vincente == giocatori[0] ? giocatori[1] : giocatori[0];

        return arr;
    }

    public static void showMessage(String text){
        showMessage(text, null);
    }

    public static void showMessage(String text, Runnable callback){
        Utility.textAnimation(text, centerText, () -> {
            if(callback != null)
                callback.run();

            clearText(centerText);
        });
    }

    public static void clearText(TextView textView){
        textView.setText("");
    }

    public static void muoviCarta(View startView, View destView, boolean fade, boolean flip, boolean reverseFlip, Object event){
        muoviCarta(startView, destView, null, fade, flip, reverseFlip, event);
    }

    public static void muoviCarta(View startView, View destView, Carta objectCarta, boolean fade, boolean flip, boolean reverseFlip, Object event){
        AnimationSet animationSet = new AnimationSet(false);

        animationSet.addAnimation(moveAnim(startView, destView, event));

        if(fade)
            animationSet.addAnimation(fadeAnim());

        startView.startAnimation(animationSet);

        if(flip || reverseFlip){
            Drawable background = null;

            if(objectCarta == null) {
                objectCarta = getCartaFromButton(startView);
                background = destView.getBackground();
            }else{
                if(objectCarta.getPortatore() == Game.user || (SharedPref.getCarteScoperte() && !ActivityGame.multiplayer))
                    background = objectCarta.getImage();
            }

            if(!objectCarta.isCoperta() && !reverseFlip)
                return;

            if(background == null && reverseFlip)
                background = Carta.getVuoto();

            flipAnim(startView, background, true, event);
        }
    }

    public static Animation moveAnim(View startView, View destView, Object event){
        final float diffX = destView.getX() - startView.getX();
        final float diffY = destView.getY() - startView.getY();

        final Animation animation = new TranslateAnimation(0, diffX,0, diffY);
        animation.setDuration(viewAnimDuration);

        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateInterpolator(accelMultip));

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation) {
                synchronized (event){
                    event.notifyAll();
                    startView.setVisibility(View.INVISIBLE);
                }
            }
        });

        return animation;
    }

    public static Animation fadeAnim(){
        Animation fadeAnim = new AlphaAnimation(1f, 0f);
        fadeAnim.setInterpolator(new AccelerateInterpolator(accelMultip));
        fadeAnim.setDuration(viewAnimDuration);
        fadeAnim.setFillAfter(false);

        return fadeAnim;
    }

    public static void flipAnim(View startView, Drawable destBackground, boolean reset, Object event){
        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(startView, "scaleX", 1f, 0f).setDuration(viewAnimDuration/2);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(startView, "scaleX", 0f, 1f).setDuration(viewAnimDuration/2);

        oa1.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation);
                startView.setBackground(destBackground);
                oa2.start();
            }
        });

        oa1.start();

        oa2.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

                if(event == null)
                    return;

                synchronized(event){
                    if(reset)
                        startView.setBackground(Carta.getVuoto());

                    event.notifyAll();
                }
            }
        });
    }
}
