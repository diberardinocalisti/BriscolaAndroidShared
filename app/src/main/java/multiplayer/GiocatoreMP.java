package multiplayer;

import gameEngine.Carta;
import gameEngine.Giocatore;

public class GiocatoreMP extends Giocatore {
    public GiocatoreMP(String player, int i) {
        super(player, i);
    }

    @Override
    public void pesca(Carta carta){
        //TODO: Andremo a ridefinire i metodi della classe Giocatore che andranno cambiati (alcuni rimarranno tali e uguali)
    }
}
