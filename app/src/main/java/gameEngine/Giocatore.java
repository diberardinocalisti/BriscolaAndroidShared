package gameEngine;

import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import java.util.ArrayList;
import java.util.Collections;

import static gameEngine.Engine.getCartaFromButton;
import static gameEngine.Game.I_CAMPO_GIOCO;
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

        for(int i = this.index * 3, j = 0; j < nCarte; j++, i++){
            this.bottoni[j] = carteBottoni[i];
        }
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
    public void mostraMazzo(){
        final Integer daMostrare = 3;

        ArrayList<Carta> carte = prese;
        Collections.sort(carte, Engine.ordinaCarte);
        Collections.reverse(carte);

        //@// TODO: 04/06/2021 Mostrare le 3 carte più alte del mazzo a fine partita;
        //if(carte.size() <= daMostrare)
            //this.pMazzo.remove(this.mazzo);

        //for(int i = 0; i < daMostrare && i < carte.size(); i++)
            //this.pTavolo.add(carte.get(i));
    }

    public void svuotaMazzo(){
        for(int i = 0; i < carte.length; i++)
            carte[i] = null;

        this.punteggioCarte = 0;
        this.prese.clear();
    }

    public void azzeraPunteggio(){
        this.score = 0;
    }

    public void aggiornaPunteggio(){
        this.score++;
    }

    public Carta pesca(){
        if(Game.mazzo.size() == 0){
            if(!lastManche){
                //anteprimaCarte.setVisible(false);
                lastManche = true;
                //pGiocoR.remove(briscola);
                return pesca(briscola);
            }
        }else{
            return pesca(Game.mazzo.get(0));
        }

        return null;
    }

    public Carta pesca(Carta carta) {
        carta.setPortatore(this);
        this.prendi(carta);
        carta.abilita();

        Game.mazzo.remove(carta);

        return carta;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void mancheVinta() {
        for(Integer i : I_CAMPO_GIOCO){
            if(carte[i] != null){
                Carta c = getCartaFromButton(Game.carte[i]);
                prese.add(c);
                punteggioCarte += c.getValore();
            }
        }

        ultimoVincitore = this;
    }

    public void lancia(Carta carta){
        for(Integer i : I_CAMPO_GIOCO){
            if(Game.carte[i].getBackground() == null){
                Game.carte[i].setBackground(carta.getImage());
                carta.disabilita();
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

    public void prendi(Carta daAggiungere){
        for(int i = 0; i < carte.length; i++)
            if(carte[i] == null){
                carte[i] = daAggiungere;
                carte[i].setPortatore(this);
                carte[i].setButton(this.bottoni[i]);

                if(this.isCPU())
                    this.carte[i].nascondi();
                else
                    this.carte[i].mostra();

                return;
            }
    }

    public void toccaA(){}
}
