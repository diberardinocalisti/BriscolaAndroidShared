package multiplayer;

import java.io.IOException;

import gameEngine.Giocatore;

public class GiocatoreMP extends Giocatore {
    public String ruolo;

    public GiocatoreMP(String player, int i) {
        super(player, "null", i);
    }

    public boolean isHost(){
        return this.ruolo.equals("host");
    }

    public String getRuolo(){
        return ruolo;
    }

    public void setRuolo(String ruolo){
        this.ruolo = ruolo;
    }
}
