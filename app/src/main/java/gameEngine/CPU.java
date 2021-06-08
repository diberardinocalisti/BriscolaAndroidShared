package gameEngine;

import android.os.Build;
import android.os.Handler;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;

import static gameEngine.Engine.*;

public class CPU extends Giocatore {
    protected CPU(String nome, Integer index) {
        super(nome, index, true);
        Game.CPU = this;
    }

    public void prendi(Integer indice, Carta daAggiungere){
        super.prendi(indice, daAggiungere);
        this.carte[indice].nascondi();
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
    public void toccaA(){
        Game.canPlay = false;

        // Piccolo delay tra una giocata e l'altra (Range: 1000ms - 2000ms)
        new Handler().postDelayed(() -> scegli().getButton().performClick(), (long) (Math.random() * 1000) + 1000);
    }

    // METODO CHE RESTITUISCE LA MIGLIOR CARTA DA GIOCARE;
    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta scegli(){
        Game.canPlay = true;

        Carta[] sulTavolo = Engine.getCarteGiocate();
        Carta[] mieCarte = this.carte;

        // ARRAY CONTENENTE LE CARTE DEL MAZZO CHE SUPERANO QUELLA AVVERSARIA;
        Carta[] superano = getSuperano(sulTavolo);

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

        if(cartaSulTavolo.isLiscio()){
            /**
            Se la carta più bassa che supera quella avversaria è una briscola e non è un carico
            la si gioca solo nel caso in cui non si abbia un non carico (non briscola)
            da poter lanciare, altrimenti si gioca il non carico;
            */
            if(minSupera.isBriscola() && !minSupera.isCarico()){
                return minBriscEsc.isCarico() ? minSupera : minBriscEsc;
            }else if(!maxSupera.isBriscola()){
                return maxSupera;
            }else{
                return minSupera;
            }
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
}
