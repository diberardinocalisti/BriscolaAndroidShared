package gameEngine;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import game.danielesimone.briscola.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Login.loginClass;

import static gameEngine.Engine.*;
import static gameEngine.Engine.isLastManche;
import static gameEngine.Game.*;

public class Giocatore {
    protected Button[] bottoni;

    // Array contenente le carte che il giocatore ha in mano;
    protected Carta[] carte;

    // ArrayList contenente le carte prese;
    protected ArrayList<Carta> prese;

    // Nome del giocatore;
    protected String nome;

    // Se il giocatore è controllato dalla CPU o no;
    protected boolean CPU;

    protected Integer punteggioCarte = 0;

    protected Integer index;

    protected View prendi;

    protected ImageView userIcon;

    protected String id;

    protected View mazzo;

    protected boolean pescato = false;
    
    public Giocatore(String nome, String userId, Integer index) {
        this(nome, index, userId, false);
        Game.user = this;
    }

    protected Giocatore(String nome, Integer index, String userId, boolean CPU) {
        this.nome = nome;
        this.CPU = CPU;
        this.index = index;
        this.carte = new Carta[nCarte];
        this.prese = new ArrayList<>();
        this.bottoni = new Button[(int) nCarte];

        for(int i = this.index * nCarte, j = 0; j < nCarte; j++, i++)
            this.bottoni[j] = carteBottoni[i];

        String idName = "mazzo" + this.index;
        int id = activity.getResources().getIdentifier(idName, "id", activity.getPackageName());
        this.mazzo = activity.findViewById(id);

        idName = "prendi" + this.index;
        id = activity.getResources().getIdentifier(idName, "id", activity.getPackageName());
        this.prendi = activity.findViewById(id);

        idName = "friend_profile_picture_" + this.index;
        id = activity.getResources().getIdentifier(idName, "id", activity.getPackageName());
        this.userIcon = activity.findViewById(id);

        this.id = userId;

        this.updateIcon();
    }

    public Button[] getBottoni() {
        return bottoni;
    }

    public void setBottoni(Button[] bottoni) {
        this.bottoni = bottoni;
    }

    public Carta[] getCarte() {
        return carte;
    }

    public void setCarte(Carta[] carte) {
        this.carte = carte;
    }

    public ArrayList<Carta> getPrese() {
        return prese;
    }

    public void setPrese(ArrayList<Carta> prese) {
        this.prese = prese;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public boolean isCPU() {
        return CPU;
    }

    public void setCPU(boolean CPU) {
        this.CPU = CPU;
    }

    public void setPunteggioCarte(Integer punteggioCarte) {
        this.punteggioCarte = punteggioCarte;
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public View getPrendi() {
        return prendi;
    }

    public void setPrendi(View prendi) {
        this.prendi = prendi;
    }

    public ImageView getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(ImageView userIcon) {
        this.userIcon = userIcon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public View getMazzo() {
        return mazzo;
    }

    public void setMazzo(View mazzo) {
        this.mazzo = mazzo;
    }

    public boolean isPescato() {
        return pescato;
    }

    public void setPescato(boolean pescato) {
        this.pescato = pescato;
    }

    public Bitmap getIcon(){
        BitmapDrawable drawable = (BitmapDrawable) this.userIcon.getDrawable();
        return drawable.getBitmap();
    }

    public void updateIcon() {
        loginClass.setImgProfile(activity, this.id, this.userIcon);
    }

    public void svuotaMazzo(){
        Arrays.fill(carte, null);

        this.punteggioCarte = 0;
        this.aggiornaIconaCarte();
        this.prese.clear();
    }

    public void aggiornaIconaCarte(){
        this.mazzo.setVisibility(prese.size() == 0 ? View.INVISIBLE : View.VISIBLE);
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

        String title = "+" + valoreCarte + " " + activity.getString(R.string.points) + "!\n";
        String description = isTerminata() ? activity.getString(R.string.matchended) : getMessaggioTurno(this);
        String messageToShow = title + description;

        showMessage(messageToShow);
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
    public void pesca(){
        this.pescato = false;
        
        if(!isLastManche())
            this.prendi(Game.mazzo.get(0), () -> {
                if(isLastManche() && lastManche == 0)
                    Engine.lastManche();
            });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void prendi(Carta daAggiungere, Runnable callback){
        for(int i = 0; i < carte.length; i++) {
            if (this.carte[i] == null) {
                prendi(i, daAggiungere, callback);
                return;
            }else if (this.carte[i].getButton() == null) {
                prendi(i, daAggiungere, callback);
                return;
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void prendi(Integer indice, Carta daAggiungere, Runnable callback){
        Object event = new Object();

        Giocatore.this.carte[indice] = daAggiungere;
        Giocatore.this.carte[indice].setPortatore(Giocatore.this);
        Giocatore.this.carte[indice].setButton(Giocatore.this.bottoni[indice]);

        if(isPenultimaManche())
            pulisciMazzo();

        Game.mazzo.remove(Giocatore.this.carte[indice]);
        Engine.aggiornaNCarte();

        if(isLastManche())
            this.prendi = Game.carte[I_BRISCOLA];

        muoviCarta(this.prendi, this.bottoni[indice], daAggiungere, false, true, true, event);

        new Thread(() -> {
            synchronized (event){
                try {
                    event.wait();
                    activity.runOnUiThread(() -> {
                        Giocatore.this.carte[indice].abilita();

                        if(!this.isCPU()){
                            if(Game.user == this)
                                Giocatore.this.carte[indice].mostra();
                            else
                                Giocatore.this.carte[indice].nascondi();
                        }else{
                            boolean scoperte = SharedPref.getCarteScoperte();

                            if(!scoperte)
                                this.carte[indice].nascondi();
                        }

                        if(!this.isCPU()){
                            synchronized (this) {
                                this.notifyAll();
                            }
                        }

                        this.pescato = true;
                        callback.run();
                    });

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public int getIndexFromCarta(Carta cartaDaTrovare){
        for(int i = 0; i < this.bottoni.length; i++)
            if(this.bottoni[i].getId() == cartaDaTrovare.getButton().getId())
                return i;

        return -1;
    }

    public void toccaA() throws InterruptedException {
        canPlay = true;
        giocante = this;
    }
}
