package multiplayer;

import com.example.briscolav10.R;

import java.io.IOException;

import gameEngine.Game;
import gameEngine.Giocatore;

import static gameEngine.Game.activity;
import static multiplayer.engineMultiplayer.sendChatMessage;

public class GiocatoreMP extends Giocatore {
    public String ruolo;

    public GiocatoreMP(String player, int i) throws IOException {
        super(player, null, i);
    }

    public String getRuolo(){
        return ruolo;
    }

    public void setRuolo(String ruolo){
        this.ruolo = ruolo;
    }
}
