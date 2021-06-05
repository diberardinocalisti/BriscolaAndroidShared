package gameEngine;

import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import java.util.Collections;
import java.util.Comparator;

import static gameEngine.Game.I_BRISCOLA;
import static gameEngine.Game.I_CAMPO_GIOCO;
import static gameEngine.Game.briscola;
import static gameEngine.Game.carte;
import static gameEngine.Game.giocante;
import static gameEngine.Game.giocatori;
import static gameEngine.Game.lastManche;
import static gameEngine.Game.mazzo;
import static gameEngine.Game.semi;
import static gameEngine.Game.terminata;
import static gameEngine.Game.ultimoVincitore;

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

        for(String seme : semi) {
            for(Integer i = 1; i <= 10; i++)
                mazzo.add(new Carta(i, seme));
        }

        Collections.shuffle(mazzo);
    }

    static void reset(){
        for(Giocatore p : giocatori){
            p.svuotaMazzo();
            p.azzeraPunteggio();
        }
    }

    static void creaGiocatori(){
        for(int i = 0; i < giocatori.length; i++){
            boolean CPU = i == 0 ? true : false;
            giocatori[i] = CPU == false ? new Giocatore("Giocatore", i) : new CPU("CPU", i);
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

    static void terminaManche(Giocatore vincitore) throws InterruptedException {
        Game.canPlay = false;

        Thread.sleep(1750);

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
        Game.endEvent = 1;
        //pGiocoC.add(new Messaggio(vincitore.getNome() + " ha vinto! ("+vincitore.conta()+")", "Premi INVIO per un'altra partita!"));
    }

    static void terminaRound(Giocatore vincitore){
        Game.endEvent = 0;
        String titolo = vincitore == null ? "Pareggio!" : vincitore.getNome() + " ha vinto il round (" + vincitore.conta() + ")!";
        //pGiocoC.add(new Messaggio(titolo, "Premi INVIO per il prossimo round!"));
    }

    static Giocatore trovaVincitore(){
        Integer score_1 = giocatori[0].conta();
        Integer score_2 = giocatori[1].conta();

        if(score_1 > score_2)
            return giocatori[0];
        else if(score_2 > score_1)
            return giocatori[1];
        else
            return null;
    }

    static void pulisciTavolo(){
        pulisciPianoGioco();
    }

    static void pulisciPianoGioco(){
        for(Integer i : I_CAMPO_GIOCO){
            carte[i].setBackground(null);
        }
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

    static Carta getCartaFromButton(Button button){
        for(Giocatore g : giocatori)
            for(Carta carta : g.carte)
                if(carta.getButton().getId() == button.getId())
                    return carta;
        return null;
    }

    static Carta getOtherCarta(Carta current){
        return null;
        /*for(Component component: pGiocoC.getComponents()){
            Carta carta = (Carta) component;

            if(carta != current)
                return carta;
        }

        return null;*/
    }

    public static Comparator<Carta> ordinaCarte = Comparator.comparingInt(c -> c.getValore());

    static Integer getCarteATerra(){
        return 0;
        //return pGiocoUser.getComponentCount() + pGiocoCPU.getComponentCount();
    }

    static boolean isTerminata(){
        return getCarteATerra() == 0;
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

        //c_vincente.setBorderPainted(true);
        return c_vincente.getPortatore();
    }

    public static Carta getMax(Carta[] array) {
        Carta max = null;

        for(int i = 0; i < array.length; i++){
            if(max == null){
                max = array[i];
                continue;
            }

            if(array[i].getValore() > max.getValore()) {
                max = array[i];
            }else if(array[i].getValore() == max.getValore()){
                if(array[i].getNumero() > max.getNumero()){
                    max = array[i];
                }
            }
        }

        return max;
    }

    public static Carta getMin(Carta[] array) {
        Carta min = null;

        for(int i = 0; i < array.length; i++) {
            if (min == null) {
                min = array[i];
                continue;
            }

            if (array[i].getValore() < min.getValore()){
                min = array[i];
            }else if(array[i].getValore() == min.getValore()){
                if(array[i].getNumero() < min.getNumero()){
                    min = array[i];
                }
            }
        }

        return min;
    }
}
