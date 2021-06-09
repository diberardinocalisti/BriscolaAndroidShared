package gameEngine;

import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;
import com.facebook.login.Login;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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

    static void creaMazzo() {
        lastManche = false;
        mazzo.clear();

        Carta.nascondi(carte[I_MAZZO]);

        for(String seme : semi)
            for(Integer i = 1; i <= 10; i++)
                mazzo.add(new Carta(i, seme));

        Collections.shuffle(mazzo);
        mazzoIniziale = mazzo.toArray(new Carta[0]);
    }

    static void reset(){
        for(Giocatore p : giocatori){
            p.svuotaMazzo();
            p.azzeraPunteggio();
        }
    }

    static void creaGiocatori(){
        for(int i = 0; i < giocatori.length; i++){
            boolean CPU = i == 0;
            if(!CPU){
                String username = "Ospite";

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

    static void prossimoTurno(Giocatore p){
        if(p == null)
            p = getRandomPlayer();

        giocante = p;
        p.toccaA();
    }

    static void terminaManche(Giocatore vincitore) {
        Game.canPlay = false;

        vincitore.mancheVinta();
        pulisciPianoGioco();

        for(Giocatore p : giocatori)
            if(Game.mazzo.size() > 0 || !lastManche)
                p.pesca();

        Game.canPlay = true;

        if(isTerminata()){
            termina();
        }else{
            prossimoTurno(ultimoVincitore);
        }
    }

    static void termina(){
        Giocatore vincitore = trovaVincitore();

        Game.canPlay = false;
        terminata = true;

        for(Giocatore p : giocatori)
            p.mostraMazzo();

        if(vincitore != null){
            vincitore.aggiornaPunteggio();

            if(vincitore.getScore() == Game.scoreLimit){
                terminaPartita(vincitore);
            }else{
                terminaRound(vincitore);
            }
        }else{
            terminaRound(null);
        }
    }

    static void terminaPartita(Giocatore vincitore){
        String titolo = vincitore.getNome() + " ha vinto! ("+vincitore.getPunteggioCarte()+")";
        String sottotitolo = "Premi OK per un'altra partita!";
        Utility.confirmDialog(activity, titolo, sottotitolo, (dialog, which) -> iniziaPartita(), dialog -> iniziaPartita());
    }

    static void terminaRound(Giocatore vincitore){
        String titolo = vincitore == null ? "Pareggio!" : vincitore.getNome() + " ha vinto il round! (" + vincitore.getPunteggioCarte() + ")";
        String sottotitolo = "Premi OK per un altro round!";
        Utility.confirmDialog(activity, titolo, sottotitolo, (dialog, which) -> iniziaRound(), dialog -> iniziaRound());
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
        for(Integer i : I_CAMPO_GIOCO){
            carte[i].setBackground(null);
        }
    }

    static void pulisciPianoLaterale(){
        Game.carte[I_BRISCOLA].setBackground(null);
        Game.carte[I_MAZZO].setBackground(null);
    }

    static Giocatore getRandomPlayer(){
        return giocatori[(int) (Math.random() * giocatori.length)];
    }

    static Giocatore getOtherPlayer(Giocatore current){
        for(Giocatore p : giocatori)
            if(p != current)
                return p;

        return null;
    }

    static Carta getCartaFromButton(View button){
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

    static Carta[] getCarteGiocatori(){
        ArrayList<Carta> carte = new ArrayList<>();

        for(Giocatore p : giocatori)
            for(Carta c : p.carte)
                if(c != null)
                    carte.add(c);

        return carte.toArray(carte.toArray(new Carta[0]));
    }

    static Carta getOtherCarta(Carta current){
        for(Integer i : I_CAMPO_GIOCO) {
            Carta c = getCartaFromButton(carte[i]);

            if(c == null)
                continue;

            if(isLibero(Game.carte[i]))
                continue;

            if(c.getNome() != current.getNome())
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

    static Giocatore doLogic(Carta last, Carta first) {
        if(last != null && first == null)
            return null;

        Carta[] carte = {first, last};
        Carta comanda = first;

        for(Carta carta : carte){
            if(carta.isBriscola())
                comanda = carta;
        }

        Carta c_vincente = first.getSeme() == last.getSeme() ? getMax(carte) : comanda;

        if(c_vincente != null){
            Carta c_perdente = first == c_vincente ? last : first;
            c_perdente.getButton().setAlpha(0.5f);
        }
        
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
        Integer n_carte = Game.mazzo.size() + 1;
        TextView icona = activity.findViewById(R.id.n_carte);

        if(n_carte - 1 == 0) {
            icona.setVisibility(View.INVISIBLE);
        }else{
            icona.setText(n_carte.toString());
            icona.setVisibility(View.VISIBLE);
        }
    }
}
