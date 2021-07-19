package gameEngine;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;

import Home.MainMenu;
import Home.SharedPref;

import static gameEngine.Game.activity;

public class Carta {
    protected View b;
    protected Integer valore, numero;
    protected String seme;
    protected Giocatore portatore;
    protected String nomeBackground;
    protected String tipo;
    protected boolean coperta;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta(Integer numero, String seme){
        this.numero = numero;
        this.seme = seme;
        this.valore = calcolaValore(numero);
        this.tipo = Game.tipoCarte;
        this.nomeBackground = this.tipo + "_" + this.numero + "_" + this.seme;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta(Integer numero, String seme, String tipo){
        this.numero = numero;
        this.seme = seme;
        this.valore = calcolaValore(numero);
        this.tipo = tipo.toLowerCase();
        this.nomeBackground = this.tipo + "_" + this.numero + "_" + this.seme;
    }

    public void abilita(){
        this.b.setEnabled(true);
        this.b.setVisibility(View.VISIBLE);

        boolean scoperte = SharedPref.getCarteScoperte();

        if(ActivityGame.multiplayer) {
            if (Game.user == this.portatore)
                this.mostra();
            else
                this.nascondi();
        }else {
            if (Game.CPU == this.portatore) {
                if (scoperte)
                    this.mostra();
                else
                    this.nascondi();
            } else {
                this.mostra();
            }
        }
    }

    public void disabilita(){
        this.b.setBackground(null);
        this.b.setEnabled(false);
        this.b.setVisibility(View.INVISIBLE);
    }

    public boolean isEnabled(){
        return this.b.isEnabled();
    }

    public String getNome() {
        return nomeBackground;
    }

    public Integer getNumero() {
        return numero;
    }

    public Integer getValore() {
        return valore;
    }

    public String getSeme() {
        return seme;
    }

    public Giocatore getPortatore(){
        return portatore;
    }

    public void setPortatore(Giocatore portatore){
        this.portatore = portatore;
    }

    public void setButton(View b){
        this.b = b;

        if(this.b != null)
            this.b.setAlpha(1);
    }

    public View getButton(){
        return b;
    }

    private Integer calcolaValore(Integer numero){
        switch(numero){
            case 1: return 11;
            case 3: return 10;
            case 10: return 4;
            case 9: return 3;
            case 8: return 2;
            default: return 0;
        }
    }

    public boolean supera(Carta daSuperare){
        if(this.getSeme().equals(daSuperare.getSeme())){
            if(this.getValore() > daSuperare.getValore()){
                return true;
            }else if(this.getValore().equals(daSuperare.getValore())){
                return this.getNumero() > daSuperare.getNumero();
            }
        }else return this.isBriscola();

        return false;
    }

    public boolean isBriscola(){
        return this.getSeme().equals(Game.briscola.getSeme());
    }

    public boolean isCarico(){
        return this.valore >= 10;
    }

    public boolean isCaricoBriscola(){
        return this.isBriscola() && this.isCarico();
    }

    public boolean isLiscio(){
        return this.valore == 0;
    }

    public boolean isPunti(){
        return !isLiscio() && !isCarico();
    }

    public void mostra(){
        this.coperta = false;

        if(this.b != null) {
            this.b.setBackground(this.getImage());
            this.b.setVisibility(View.VISIBLE);
        }
    }

    public void aggiornaTipo(){
        if(!this.tipo.equals(Game.tipoCarte)){
            this.tipo = Game.tipoCarte;
            this.nomeBackground = this.tipo + "_" + this.numero + "_" + this.seme;

            if(!this.isCoperta())
                this.mostra();
        }
    }

    public String getTipo(){
        return this.tipo;
    }

    public void nascondi() {
        if(this.b != null && Game.user != this.portatore) {
            this.coperta = true;
            nascondi(this.b);
        }
    }

    public boolean isCoperta(){
        return this.coperta;
    }

    public static void nascondi(View b){
        b.setBackground(getVuoto());
        b.setVisibility(View.VISIBLE);
    }

    public Drawable getImage(){
        try{
            int resID = getImage(0);
            return activity.getResources().getDrawable(resID);
        }catch(Exception e){
            System.out.println("Errore a " + this.getNome());
            return getVuoto();
        }
    }

    public int getImage(int type){
        try{
            return activity.getResources().getIdentifier(this.getNome(), "drawable", activity.getPackageName());
        }catch(Exception e){
            return getVuoto(0);
        }
    }

    public static Drawable getVuoto(){
        int resID = getVuoto(0);
        return activity.getResources().getDrawable(resID);
    }

    public static int getVuoto(int type){
        return activity.getResources().getIdentifier("vuoto", "drawable", activity.getPackageName());
    }
}
