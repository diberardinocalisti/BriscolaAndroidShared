package multiplayer;

import com.example.briscolav10.R;

import gameEngine.Game;
import gameEngine.Giocatore;

import static gameEngine.Game.activity;
import static multiplayer.engineMultiplayer.sendChatMessage;

public class GiocatoreMP extends Giocatore {
    public String ruolo;

    public GiocatoreMP(String player, int i) {
        super(player, i);
    }

    public void joinedMessage() {
        if(Game.user != this)
            sendChatMessage(this.getNome(), activity.getString(R.string.joinedgame), true);
    }

    public String getRuolo(){
        return ruolo;
    }

    public void setRuolo(String ruolo){
        this.ruolo = ruolo;
    }
}
