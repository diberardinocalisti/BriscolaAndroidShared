package gameEngine;

import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import static gameEngine.Engine.*;
import static gameEngine.Engine.isLastManche;
import static gameEngine.Game.*;

public class Giocatore {
    public Button[] bottoni;

    // Array contenente le carte che il giocatore ha in mano;
    public Carta[] carte;

    // ArrayList contenente le carte prese;
    public ArrayList<Carta> prese;

    // Nome del giocatore;
    public String nome;

    // Se il giocatore è controllato dalla CPU o no;
    public boolean CPU;

    public Integer punteggioCarte = 0;

    public Integer index;

    public View prendi;

    // Icona che mostra il punteggio del giocatore;
    //public Button iconaPunteggio;

    public View mazzo;

    public Giocatore(String nome, Integer index){
        this(nome, index, false);
        Game.user = this;
    }

    public Giocatore(String nome, Integer index, boolean CPU){
        this.nome = nome;
        this.CPU = CPU;
        this.index = index;
        this.carte = new Carta[nCarte];
        this.prese = new ArrayList<>();
        this.bottoni = new Button[(int) nCarte];

        for(int i = this.index * nCarte, j = 0; j < nCarte; j++, i++)
            this.bottoni[j] = carteBottoni[i];

        String idS = "button" + (this.index + 1 + 10);
        int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
        //this.iconaPunteggio = activity.findViewById(id);

        idS = "mazzo" + this.index;
        id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
        this.mazzo = activity.findViewById(id);

        idS = "prendi" + this.index;
        id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
        this.prendi = activity.findViewById(id);
    }

    public String getNome() {
        return nome;
    }

    public boolean isCPU(){
        return CPU;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] mostraMazzo() {
        final int daMostrare = nCarte;

        ArrayList<Carta> carte = prese;
        Collections.sort(carte, Engine.ordinaCarte);
        Collections.reverse(carte);
        ArrayList<String> mostrate = new ArrayList<>();

        for(int i = 0; i < daMostrare && i < carte.size(); i++)
            mostrate.add(carte.get(i).getNome());

        return mostrate.toArray(new String[0]);
    }

    public void svuotaMazzo(){
        Arrays.fill(carte, null);

        this.punteggioCarte = 0;
        this.aggiornaIconaCarte();
        this.prese.clear();
    }

    public void aggiornaIconaCarte(){
        int visibility = prese.size() == 0 ? View.INVISIBLE : View.VISIBLE;
        this.mazzo.setVisibility(visibility);
        //this.iconaPunteggio.setText(this.punteggioCarte.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pesca(){
        if(!isLastManche())
            this.pesca(Game.mazzo.get(0));

        if(isLastManche() && lastManche == 0)
            Engine.lastManche();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pesca(Carta carta) {
        this.prendi(carta);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mancheVinta(Runnable callback) {
        Object event = new Object();

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();

                    activity.runOnUiThread(() -> {
                        Giocatore.this.aggiornaIconaCarte();
                        ultimoVincitore = Giocatore.this;
                        callback.run();
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }).start();

        int valoreCarte = 0;

        for(Integer i : I_CAMPO_GIOCO[lastManche]){
            if(Game.carte[i] != null){
                muoviCarta(Game.carte[i], this.mazzo, true, true, true, event);
                Carta c = getCartaFromButton(Game.carte[i]);
                c.setButton(null);
                prese.add(c);
                punteggioCarte += c.getValore();
                valoreCarte += c.getValore();
            }
        }

        this.msgMancheVinta(valoreCarte);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void msgMancheVinta(int valoreCarte){
        // Se i giocatori stanno per pescare le proprie ultime carte non visualizzerà il messaggio dei punti
        // ma visualizzerà invece il messaggio "ULTIMA MANCHE"
        if(isPenultimaManche())
            return;

        String points = "+" + valoreCarte + " " + activity.getString(R.string.points) + "!";

        String titolo = new String();

        if(!isTerminata()){
            int stringId = Game.user == this ? R.string.tuoturno : R.string.turno;
            titolo = activity.getString(stringId).replace("%user", this.getNome());;
        }else{
            titolo = activity.getString(R.string.matchended);
        }

        String msg = points + "\n" + titolo;

        Utility.textAnimation(msg, centerText, () -> clearText(centerText));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void lancia(Carta carta){
        int indice = this.index + I_CAMPO_GIOCO[lastManche][0];

        if(carta == null)
            return;

        if(!isLibero(Game.carte[indice]))
            return;

        carta.disabilita();
        carta.setButton(Game.carte[indice]);
        carta.abilita();

        this.rimuovi(carta);
    }

    public Integer getPunteggioCarte(){
        return punteggioCarte;
    }

    public Integer n_carte(){
        Integer size = 0;

        for(Carta carta : carte)
            if(carta != null)
                size++;

        return size;
    }

    public void rimuovi(Carta daRimuovere){
        for(int i = 0; i < carte.length; i++) {
            if(carte[i] == null)
                continue;

            if(carte[i].getNome().equals(daRimuovere.getNome())) {
                carte[i] = null;
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public int prendi(Carta daAggiungere){
        for(int i = 0; i < carte.length; i++) {
            if (this.carte[i] == null) {
                prendi(i, daAggiungere, null);
                return i;
            } else if (this.carte[i].getButton() == null) {
                prendi(i, daAggiungere, null);
                return i;
            }
            /*else if (isLibero(this.carte[i].getButton())) {
                prendi(i, daAggiungere, null);
                return i;
            }*/
        }
        return -1;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void prendi(Integer indice, Carta daAggiungere, Runnable callback){
        Object event = new Object();

        Giocatore.this.carte[indice] = daAggiungere;
        Giocatore.this.carte[indice].setPortatore(Giocatore.this);
        Giocatore.this.carte[indice].setButton(Giocatore.this.bottoni[indice]);
        Game.mazzo.remove(Giocatore.this.carte[indice]);
        Engine.aggiornaNCarte();

        muoviCarta(this.prendi, this.bottoni[indice], daAggiungere, false, true, true, event);

        new Thread(() -> {
            synchronized (event){
                try {
                    event.wait();
                    activity.runOnUiThread(() -> {
                        Giocatore.this.carte[indice].abilita();

                        if(Game.CPU != this){
                            if(Game.user == this)
                                Giocatore.this.carte[indice].mostra();
                            else
                                Giocatore.this.carte[indice].nascondi();
                        }
                    });
                    if(callback != null)
                        callback.run();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public Carta getAvailableCarta(){
        for(Carta c : this.carte){
            View b = c.getButton();
            if(b == null)
                return c;
        }
        return null;
    }

    public int getIndexFromCarta(Carta cartaDaTrovare){
        for(int i = 0; i < this.bottoni.length; i++)
            if(this.bottoni[i].getId() == cartaDaTrovare.getButton().getId())
                return i;

        return -1;
    }

    public void toccaA(){
        giocante = this;
    }
}
