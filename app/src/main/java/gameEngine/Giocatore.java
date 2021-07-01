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
        this.carte = new Carta[3];
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
    }

    public String getNome() {
        return nome;
    }

    public boolean isCPU(){
        return CPU;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public String[] mostraMazzo() {
        final int daMostrare = 3;

        ArrayList<Carta> carte = prese;
        Collections.sort(carte, Engine.ordinaCarte);
        Collections.reverse(carte);
        ArrayList<String> mostrate = new ArrayList<>();

        for(int i = 0; i < daMostrare && i < carte.size(); i++){
            mostrate.add(carte.get(i).getNome());
        }

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
        if(Game.mazzo.size() == 0){
            if(!lastManche){
                lastManche = true;
                pulisciPianoLaterale();
                this.pesca(briscola);
            }
        }else{
            this.pesca(Game.mazzo.get(0));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void pesca(Carta carta) {
        int index = this.prendi(carta);
        Game.mazzo.remove(this.carte[index]);
        Engine.aggiornaNCarte();
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

        for(Integer i : I_CAMPO_GIOCO){
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

    public void msgMancheVinta(int valoreCarte){
        String points = "+" + valoreCarte + " " + activity.getString(R.string.points) + "!";

        int stringId = Game.user == this ? R.string.tuoturno : R.string.turno;
        String tocca = activity.getString(stringId).replace("%user", this.getNome());;

        String msg = points + "\n" + tocca;
        Utility.textAnimation(msg, centerText, Engine::clearCenterText);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void lancia(Carta carta){
        int indice = this.index + I_CAMPO_GIOCO[0];

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
                prendi(i, daAggiungere);
                return i;
            } else if (this.carte[i].getButton() == null) {
                prendi(i, daAggiungere);
                return i;
            } else if (isLibero(this.carte[i].getButton())) {
                prendi(i, daAggiungere);
                return i;
            }
        }
        return -1;
    }

    public void prendi(Integer indice, Carta daAggiungere){
        this.carte[indice] = daAggiungere;
        this.carte[indice].setPortatore(this);
        this.carte[indice].setButton(this.bottoni[indice]);
        this.carte[indice].abilita();
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
