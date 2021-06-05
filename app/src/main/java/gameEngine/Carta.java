package gameEngine;

import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import static gameEngine.Game.activity;

public class Carta {
    protected Button b;
    protected Integer valore, numero;
    protected String seme;
    protected boolean enabled = true;
    protected Giocatore portatore;
    protected String nomeBackground;

    @RequiresApi(api = Build.VERSION_CODES.N)
    public Carta(Integer numero, String seme){
        this.numero = numero;
        this.seme = seme;
        this.valore = calcolaValore(numero);
        this.nomeBackground = seme + "_" + numero;
    }

    public void abilita(){
        enabled = true;
        this.b.setBackground(this.getImage());
        this.b.setEnabled(true);
    }

    public void disabilita(){
        enabled = false;
        this.b.setBackground(null);
        this.b.setEnabled(false);
    }

    public boolean isEnabled(){
        return enabled;
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

    public void setButton(Button b){
        this.b = b;
    }

    public Button getButton(){
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
        if(this.getSeme() == daSuperare.getSeme()){
            if(this.getValore() > daSuperare.getValore()){
                return true;
            }else if(this.getValore() == daSuperare.getValore()){
                if(this.getNumero() > daSuperare.getNumero()){
                    return true;
                }
            }
        }else if(this.isBriscola()){
            return true;
        }

        return false;
    }

    public boolean isBriscola(){
        return this.getSeme() == Game.briscola.getSeme();
    }

    public void mostra(){
        this.b.setBackground(this.getImage());
    }

    public void nascondi() {
        if(Game.carteScoperte && this.getPortatore() != null)
            return;

        int resID = activity.getResources().getIdentifier("vuoto", "drawable", activity.getPackageName());
        Drawable image = activity.getResources().getDrawable(resID);

        this.b.setBackground(image);
    }

    public Drawable getImage(){
        int resID = activity.getResources().getIdentifier(this.getNome(), "drawable", activity.getPackageName());
        return activity.getResources().getDrawable(resID);
    }
}
