package gameEngine;

import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;

import static gameEngine.Engine.getCartaFromButton;
import static gameEngine.Engine.isLibero;
import static gameEngine.Engine.pulisciPianoLaterale;
import static gameEngine.Game.I_BRISCOLA;
import static gameEngine.Game.I_CAMPO_GIOCO;
import static gameEngine.Game.activity;
import static gameEngine.Game.briscola;
import static gameEngine.Game.carte;
import static gameEngine.Game.carteBottoni;
import static gameEngine.Game.giocatori;
import static gameEngine.Game.lastManche;
import static gameEngine.Game.mazzo;
import static gameEngine.Game.nCarte;
import static gameEngine.Game.ultimoVincitore;

public class Giocatore {
    protected Button bottoni[];

    // Array contenente le carte che il giocatore ha in mano;
    protected Carta carte[];

    // ArrayList contenente le carte prese;
    protected ArrayList<Carta> prese;

    // Nome del giocatore;
    protected String nome;

    // Round vinti del giocatore;
    protected Integer score = 0;

    // Se il giocatore è controllato dalla CPU o no;
    protected boolean CPU;

    protected Integer punteggioCarte = 0;

    protected Integer index;

    // Icona che mostra il punteggio del giocatore;
    protected Button iconaPunteggio;

    public Giocatore(String nome, Integer index){
        this(nome, index, false);
    }

    protected Giocatore(String nome, Integer index, boolean CPU){
        this.nome = nome;
        this.CPU = CPU;
        this.index = index;
        this.carte = new Carta[3];
        this.prese = new ArrayList<>();
        this.bottoni = new Button[nCarte];

        for(int i = this.index * nCarte, j = 0; j < nCarte; j++, i++){
            this.bottoni[j] = carteBottoni[i];
        }

        String idS = "button" + (this.index + 1 + 10);
        int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
        this.iconaPunteggio = (Button) activity.findViewById(id);
    }

    public String getNome() {
        return nome;
    }

    public Integer getScore() {
        return score;
    }

    public boolean isCPU(){
        return CPU;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mostraMazzo() {
        final Integer daMostrare = 3;

        ArrayList<Carta> carte = prese;
        Collections.sort(carte, Engine.ordinaCarte);
        Collections.reverse(carte);

        //@// TODO: 04/06/2021 Mostrare le 3 carte più alte del mazzo a fine partita;

        for(int i = 0; i < daMostrare && i < carte.size(); i++){
            Carta c = carte.get(i);
            this.carte[i] = c;
            this.carte[i].setButton(this.bottoni[i]);
            this.carte[i].mostra();
        }
    }

    public void svuotaMazzo(){
        for(int i = 0; i < carte.length; i++)
            carte[i] = null;

        this.punteggioCarte = 0;
        this.aggiornaIconaCarte();
        this.prese.clear();
    }

    public void azzeraPunteggio(){
        this.score = 0;
    }

    public void aggiornaPunteggio(){
        this.score++;
    }

    public void aggiornaIconaCarte(){
        this.iconaPunteggio.setText(this.punteggioCarte.toString());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta pesca(){
        System.out.println(mazzo.size());
        if(Game.mazzo.size() == 0){
            if(!lastManche){
                lastManche = true;
                pulisciPianoLaterale();
                return pesca(briscola);
            }
        }else{
            return pesca(Game.mazzo.get(0));
        }

        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta pesca(Carta carta) {
        int index = this.prendi(carta);
        Game.mazzo.remove(this.carte[index]);
        return carta;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mancheVinta() {
        for(Integer i : I_CAMPO_GIOCO){
            if(Game.carte[i] != null){
                Carta c = getCartaFromButton(Game.carte[i]);
                c.setButton(null);
                prese.add(c);
                punteggioCarte += c.getValore();
            }
        }

        this.aggiornaIconaCarte();

        ultimoVincitore = this;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void lancia(Carta carta){
        for(Integer i : I_CAMPO_GIOCO){
            if(isLibero(Game.carte[i])){
                carta.disabilita();
                carta.setButton(Game.carte[i]);
                carta.abilita();
                this.rimuovi(carta);
                return;
            }
        }
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

            if (carte[i].getNome() == daRimuovere.getNome()) {
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
            Button b = c.getButton();
            if(b == null)
                return c;
        }
        return null;
    }

    public void toccaA(){}
}
