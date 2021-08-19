package gameEngine;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import game.danielesimone.briscola.R;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;

public class CPU extends Giocatore {
    public static String[] difficulties;
    public final static int EASY = 0, HARD = 1;
    private int skill;

    protected CPU(String nome, Integer index) {
        super(nome, index, null, true);
        this.skill = SharedPref.getCPUSkill();

        Game.CPU = this;
        Game.opp = this;
    }

    public int getSkill(){
        return this.skill;
    }

    public void setSkill(int skill){
        this.skill = skill;
    }

    public void scopriCarte(){
        for(Carta c : carte)
            if(c != null)
                c.mostra();
    }

    public void copriCarte(){
        for(Carta c : carte)
            if(c != null)
                c.nascondi();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void toccaA() throws InterruptedException {
        Game.canPlay = false;

        super.toccaA();

        new Thread(() -> {
            // SE E' L'ULTIMA MANCHE, ASPETTA CHE L'AVVISO DELL'ULTIMA MANCHE SPARISCA PRIMA DI GIOCARE LA PROPRIA CARTA;
            if(isLastManche() && getCarteGiocatori().length == nGiocatori * nCarte) {
                synchronized (this) {
                    try {
                        this.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            activity.runOnUiThread(() -> new Handler().postDelayed(() -> Engine.onClick(scegli().getButton()), (long) intermezzoCPU));
        }).start();
    }

    // METODO CHE RESTITUISCE LA MIGLIOR CARTA DA GIOCARE;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta scegli(){
        return this.skill == 0 ? algoEasy() : algoHard();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta algoHard(){
        Game.canPlay = true;

        Carta[] sulTavolo = Engine.getCarteGiocate();
        Carta[] mieCarte = this.carte;

        // ARRAY CONTENENTE LE CARTE DEL MAZZO CHE SUPERANO QUELLA AVVERSARIA;
        Carta[] superano = getSuperano(sulTavolo);

        // ARRAY CONTENENTE LE CARTE DEL MAZZO CHE NON SUPERANO QUELLA AVVERSARIA;
        Carta[] nonSuperano = getNonSuperano(sulTavolo);

        // LA CARTA CON PUNTEGGIO PIU' BASSO;
        Carta minPunti = Engine.getMin(mieCarte);

        // LA CARTA CON PUNTEGGIO PIU' BASSO, ESCLUDENDO LE BRISCOLE;
        Carta minBriscEsc = Engine.getMin(nonBriscole());

        // LA BRISCOLA PIU' BASSA NEL MAZZO;
        Carta minBrisc = Engine.getMin(briscole());

        /** Se nessuna delle carte supera quella avversaria o se si sta facendo la prima mossa; */
        if(superano.length == 0){
            /**
             * Se, escludendo le briscole, la carta più bassa del proprio mazzo è un carico, la lancia
             * solo nel caso in cui non abbia una briscola non carico da lanciare;
             **/

            if(minBriscEsc.isCarico()){
                return minBrisc.isCarico() ? minBriscEsc : minBrisc;
            }else{
                return minBriscEsc;
            }
        }

        Carta cartaSulTavolo = sulTavolo[0];

        // LA CARTA PIU' BASSA DEL MAZZO (TRA QUELLE CHE SUPERANO LA CARTA AVVERSARIA), ESCLUDENDO LE BRISCOLE SE PRESENTI;
        Carta minSupera = Engine.getMin(nonBriscole(superano));

        // LA CARTA PIU' ALTA DEL MAZZO (TRA QUELLE CHE SUPERANO LA CARTA AVVERSARIA), ESCLUDENDO LE BRISCOLE SE PRESENTI;
        Carta maxSupera = Engine.getMax(nonBriscole(superano));

        Carta minNonSupera = Engine.getMin(nonSuperano);

        // LA CARTA PIU' ALTA DEL MAZZO, SENZA ESCLUDERE LE BRISCOLE;
        Carta maxSuperaBrisc = Engine.getMax(superano);

        /**
         * Se la somma della carta massima e la carta avveraria più il punteggio attuale
         * garantisce la vittoria al giocatore, gioca la stessa (solo nel caso in cui la carta stessa
         * supera quella avversaria);
         */
        if(maxSuperaBrisc.supera(cartaSulTavolo)){
            Integer sommaCarte = maxSuperaBrisc.getValore() + cartaSulTavolo.getValore();

            if(sommaCarte + this.punteggioCarte > Game.maxPunti / 2)
                return maxSuperaBrisc;
        }

        /**
         * Se la briscola è un carico e siamo all'ultima manche, il bot tirerà la carta più bassa che ha nel mazzo
         * tale che non superi quella avversaria (in modo da assicurarsi la presa della briscola)
         */
        if(Game.mazzo.size() == nGiocatori && Game.briscola.isCarico())
            if(!getMin(nonSuperano).supera(cartaSulTavolo))
                return minNonSupera;

        if(cartaSulTavolo.isLiscio()){
            /**
             * Se si possiedono solo carte briscole che superano quella avversaria;
             **/
            if(minSupera.isBriscola()){
                /** Se la carta più bassa che supera quella avversaria è una briscola e non è un carico
                 * la si gioca solo nel caso in cui non si abbia un non carico (non briscola)
                 * da poter lanciare, altrimenti si gioca il non carico;
                 **/
                if(!minSupera.isCarico()) {
                    return minBriscEsc.isLiscio() ? minBriscEsc : minSupera;
                }else{
                    /**
                     * Se la minima (escludendo le briscole) è un carico, allora gioca la briscola solo nel caso in cui non sia un carico;
                     * Altrimenti gioca il carico;
                     **/
                    if(minBriscEsc.isCarico())
                        return !minPunti.isCarico() ? minPunti : minBriscEsc;
                    else return minBriscEsc;
                }
            }else if(!maxSupera.isBriscola()){
                return maxSupera;
            }else{
                return minSupera;
            }
        }else{
            /**
             * Se la carta più bassa che supera quella avversaria è un carico di briscola, allora la si gioca
             * solo nel caso in cui a terra c'è un carico, altrimenti si gioca la carta più bassa che si ha nel mazzo;
             */
            if(minSupera.isCaricoBriscola()) {
                if (cartaSulTavolo.isCarico())
                    return minSupera;
                else
                    return !minPunti.isCarico() ? minPunti : minBriscEsc;
            }else{
                /**
                 * Nota: maxSupera è il risultato di un metodo a cui viene applicato il metodo "nonBriscole", ciò significa
                 * che se maxSupera è una briscola vuol dire che il mazzo è formato da sole briscole. In altre parole, nonBriscole
                 * restituisce il mazzo originale se il mazzo è formato da sole briscole, per cui se maxSupera è briscola allora
                 * il mazzo è formato da sole briscole e perciò l'avversario giocherà la più bassa tra queste; Se invece maxSupera non
                 * è una briscola, allora l'avversario giocherà quest'ultima in modo da ottenere più punti possibili con una sola presa.
                 **/
                return maxSupera.isBriscola() ? minSupera : maxSupera;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta algoEasy(){
        Game.canPlay = true;

        Carta[] sulTavolo = Engine.getCarteGiocate();
        Carta[] mieCarte = this.carte;

        // ARRAY CONTENENTE LE CARTE DEL MAZZO CHE SUPERANO QUELLA AVVERSARIA;
        Carta[] superano = getSuperano(sulTavolo);

        // LA CARTA CON PUNTEGGIO PIU' BASSO, ESCLUDENDO LE BRISCOLE;
        Carta minBriscEsc = Engine.getMin(nonBriscole());

        // LA BRISCOLA PIU' BASSA NEL MAZZO;
        Carta minBrisc = Engine.getMin(briscole());

        /** Se nessuna delle carte supera quella avversaria o se si sta facendo la prima mossa; */
        if(superano.length == 0){
            /**
             * Se, escludendo le briscole, la carta più bassa del proprio mazzo è un carico, la lancia
             * solo nel caso in cui non abbia una briscola non carico da lanciare;
             **/
            if(minBriscEsc.isCarico()){
                return minBrisc.isCarico() ? minBriscEsc : minBrisc;
            }else{
                return minBriscEsc;
            }
        }

        // LA CARTA PIU' BASSA DEL MAZZO (TRA QUELLE CHE SUPERANO LA CARTA AVVERSARIA), ESCLUDENDO LE BRISCOLE SE PRESENTI;
        Carta minSupera = Engine.getMin(nonBriscole(superano));

        // LA CARTA PIU' ALTA DEL MAZZO (TRA QUELLE CHE SUPERANO LA CARTA AVVERSARIA), ESCLUDENDO LE BRISCOLE SE PRESENTI;
        Carta maxSupera = Engine.getMax(nonBriscole(superano));

        return maxSupera.isBriscola() ? minSupera : maxSupera;
    }

    // SE NON SI PASSA NESSUN PARAMETRO AL METODO, ALLORA SI CONSIDERA IL MAZZO DEL GIOCATORE;
    public Carta[] nonBriscole(){
        return nonBriscole(this.carte);
    }

    // METODO CHE RESTITUISCE LE CARTE NON BRISCOLE PRESENTI NELL'ARRAY PASSATO, SE NON SONO PRESENTI RESTITUISCE L'ARRAY STESSO;
    public Carta[] nonBriscole(Carta[] carte){
        ArrayList<Carta> nonBriscole = new ArrayList<>();

        for(Carta carta : carte)
            if(carta != null)
                if(!carta.isBriscola())
                    nonBriscole.add(carta);

        if(nonBriscole.size() == 0)
            return carte;
        else
            return nonBriscole.toArray(new Carta[0]);
    }

    public Carta[] briscole(){
        return briscole(this.carte);
    }

    public static Carta[] briscole(Carta[] carte){
        ArrayList<Carta> briscole = new ArrayList<>();

        for(Carta carta : carte)
            if(carta != null)
                if(carta.isBriscola())
                    briscole.add(carta);

        if(briscole.size() == 0)
            return carte;
        else
            return briscole.toArray(new Carta[0]);
    }

    public Carta[] getSuperano(Carta[] daSuperare){
        if(daSuperare.length == 0)
            return new Carta[]{};

        return getSuperano(daSuperare[0]);
    }

    public Carta[] getSuperano(Carta daSuperare){
        return getSuperano(daSuperare, this.carte);
    }

    // METODO CHE RESTITUISCE UN ARRAY DI "CARTA" CONTENENTE LE CARTE CHE SONO PIU' ALTE DELLA CARTA PASSATA;
    public static Carta[] getSuperano(Carta daSuperare, Carta[] mazzo){
        ArrayList<Carta> maggiori = new ArrayList<>();

        for(Carta carta : mazzo)
            if(carta != null)
                if(carta.supera(daSuperare))
                    maggiori.add(carta);

        return maggiori.toArray(new Carta[0]);
    }

    public Carta[] getNonSuperano(Carta[] daSuperare){
        if(daSuperare.length == 0)
            return new Carta[]{};

        return getNonSuperano(daSuperare[0]);
    }

    public Carta[] getNonSuperano(Carta daSuperare){
        return getNonSuperano(daSuperare, this.carte);
    }

    // METODO CHE RESTITUISCE UN ARRAY DI "CARTA" CONTENENTE LE CARTE CHE SONO PIU' ALTE DELLA CARTA PASSATA;
    public static Carta[] getNonSuperano(Carta daSuperare, Carta[] mazzo){
        ArrayList<Carta> minori = new ArrayList<>();

        for(Carta carta : mazzo)
            if(carta != null)
                if(!carta.supera(daSuperare))
                    minori.add(carta);

        return minori.toArray(new Carta[0]);
    }
}
