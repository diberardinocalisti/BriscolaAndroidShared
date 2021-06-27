package gameEngine;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterViewAnimator;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;
import com.facebook.login.Login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.Delayed;

import Home.SharedPref;
import multiplayer.Game.ActivityMultiplayerGame;

import static gameEngine.Game.*;
import static Login.loginClass.*;

@RequiresApi(api = Build.VERSION_CODES.N)
public class Engine{
    public static void inizializza() {
        creaGiocatori();
        iniziaPartita();
    }

    public static void iniziaPartita() {
        reset();
        iniziaRound();
    }

    static void iniziaRound() {
        Giocatore toccaA = trovaVincitore();

        if(toccaA == null)
            toccaA = getRandomPlayer();

        inizia();
        pulisciTavolo();
        creaMazzo();
        estraiBriscola();
        distribuisciCarte();
        prossimoTurno(toccaA);
    }

    static void inizia(){
        Game.canPlay = true;
        terminata = false;
    }

    public static void creaMazzo() {
        lastManche = false;
        mazzo.clear();

        Carta.nascondi(carte[I_MAZZO]);

        for(String seme : semi)
            for(Integer i = 1; i <= 10; i++)
                mazzo.add(new Carta(i, seme));

        Collections.shuffle(mazzo);
        mazzoIniziale = mazzo.toArray(new Carta[0]);
    }

    public static void creaMazzo(String carte){
        final String delCarte = ";";
        final String delTipo = "_";

        String[] carteSplit = carte.split(delCarte);

        for(String c : carteSplit){

            String[] strTok = c.split(delTipo);

            int i = Integer.parseInt(strTok[1]);
            String seme = strTok[2];

            mazzo.add(new Carta(i, seme));
        }

        mazzoIniziale = mazzo.toArray(new Carta[0]);
    }

    static void reset(){
        for(Giocatore p : giocatori){
            p.svuotaMazzo();
        }
    }

    static void creaGiocatori(){
        for(int i = 0; i < giocatori.length; i++){
            boolean CPU = i == 0;
            if(!CPU){
                String username = "Guest";

                if(isFacebookLoggedIn())
                    username = getFBNome();

                giocatori[i] = new Giocatore(username, i);
            }else{
                giocatori[i] = new CPU("CPU", i);
            }
        }
    }

    static void estraiBriscola(){
        briscola = mazzo.get(0);
        mazzo.remove(briscola);
        briscola.setButton(carte[I_BRISCOLA]);
        briscola.mostra();
    }

    static void distribuisciCarte(){
        for(Giocatore p : giocatori){
            p.svuotaMazzo();

            while(p.n_carte() < Game.nCarte)
                p.pesca();
        }
    }

    public static void prossimoTurno(Giocatore p){
        if(p == null)
            p = getRandomPlayer();

        giocante = p;
        p.toccaA();
    }

    public static void terminaManche(Giocatore vincitore) {
        Game.canPlay = false;

        vincitore.mancheVinta(new Runnable() {
            @Override
            public void run() {
                pulisciPianoGioco();

                Giocatore[] giocatori = getVincitorePerdente(vincitore);

                for(Giocatore p : giocatori)
                    if(Game.mazzo.size() > 0 || !lastManche)
                        p.pesca();

                Game.canPlay = true;

                if(isTerminata()){
                    termina();
                }else{
                    prossimoTurno(vincitore);
                }
            }
        });

    }

    static void termina(){
        Game.canPlay = false;
        Game.terminata = true;

        terminaPartita();
    }

    static void terminaPartita(){
        Intent i = new Intent(activity,com.example.briscolav10.postPartita.class);

        i.putExtra("punteggio", user.getPunteggioCarte());
        i.putExtra("carte", user.mostraMazzo());

        activity.startActivity(i);
    }

    static Giocatore trovaVincitore(){
        Integer score_1 = giocatori[0].getPunteggioCarte();
        Integer score_2 = giocatori[1].getPunteggioCarte();

        if(score_1 > score_2)
            return giocatori[0];
        else if(score_2 > score_1)
            return giocatori[1];
        else
            return null;
    }

    static void pulisciTavolo(){
        pulisciPianoGioco();
        pulisciPianoLaterale();
    }

    static void pulisciPianoGioco(){
        for(Integer i : I_CAMPO_GIOCO)
            carte[i].setBackground(null);
    }

    static void pulisciPianoLaterale(){
        Game.carte[I_BRISCOLA].setBackground(null);
        Game.carte[I_MAZZO].setBackground(null);
    }

    static Giocatore getRandomPlayer(){
        return giocatori[(int) (Math.random() * giocatori.length)];
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

            System.out.println("Dimensione " + mazzoIniziale.length);
            System.out.println("DAL MAZZO " + carta.getNome());

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
        for(Integer i : I_CAMPO_GIOCO) {
            Carta c = getCartaFromButton(carte[i]);

            if(c == null)
                continue;

            if(isLibero(Game.carte[i]))
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

    static boolean isLibero(View b){
        return b.getBackground() == null;
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
        ArrayList<Carta> carteGiocate = new ArrayList();

        for(Integer i : I_CAMPO_GIOCO){
            Carta c = getCartaFromButton(Game.carte[i]);
            if(c != null)
                carteGiocate.add(c);
        }

        return carteGiocate.toArray(new Carta[0]);
    }

    public static void aggiornaNCarte(){
        aggiornaNCarte(Game.mazzo.size() + 1);
    }

    public static void aggiornaNCarte(Integer n_Carte){
        TextView icona = activity.findViewById(R.id.n_carte);

        if(n_Carte - 1 == 0) {
            icona.setVisibility(View.INVISIBLE);
        }else{
            icona.setText(n_Carte.toString());
            icona.setVisibility(View.VISIBLE);
        }
    }

    public static void aggiornaTipoCarte(String tipoCarte){
        tipoCarte = tipoCarte.toLowerCase();
        SharedPref.setTipoCarte(tipoCarte);
        Game.tipoCarte = tipoCarte;

        if(Game.terminata)
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

    public static void muoviCarta(View startView, View destView, boolean fade, boolean flip, Object event){
        muoviCarta(startView, destView, null, fade, flip, event);
    }

    public static void muoviCarta(View startView, View destView, Carta objectCarta, boolean fade, boolean flip, Object event){
        final int accelMultip = 2;

        final float diffX = destView.getX() - startView.getX();
        final float diffY = destView.getY() - startView.getY();

        AnimationSet s = new AnimationSet(false);

        final Animation animation = new TranslateAnimation(0, diffX,0, diffY);
        animation.setDuration(animationDuration);

        animation.setFillAfter(false);
        animation.setInterpolator(new AccelerateInterpolator(accelMultip));
        s.addAnimation(animation);

        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation) {
                synchronized (event){
                    event.notifyAll();
                }
            }
        });

        if(fade){
            Animation fadeAnim = new AlphaAnimation(1f, 0f);
            fadeAnim.setInterpolator(new AccelerateInterpolator(accelMultip));
            fadeAnim.setDuration(animationDuration);
            fadeAnim.setFillAfter(false);

            s.addAnimation(fadeAnim);
        }

        startView.startAnimation(s);

        if(!flip)
            return;

        if(objectCarta == null)
            objectCarta = getCartaFromButton(startView);

        assert objectCarta != null;
        if(!objectCarta.isCoperta())
            return;

        final ObjectAnimator oa1 = ObjectAnimator.ofFloat(startView, "scaleX", 1f, 0f).setDuration(animationDuration);
        final ObjectAnimator oa2 = ObjectAnimator.ofFloat(startView, "scaleX", 0f, 1f).setDuration(animationDuration);
        oa2.setInterpolator(new AccelerateDecelerateInterpolator());

        oa1.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation, boolean isReverse) {
                super.onAnimationEnd(animation);
                startView.setBackground(destView.getBackground());
                oa2.start();
            }
        });

        oa1.start();

        oa2.addListener(new AnimatorListenerAdapter() {
            @Override public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                synchronized (event){
                    event.notifyAll();
                }
            }
        });
    }
}
