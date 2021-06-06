package gameEngine;

public class CPU extends Giocatore {
    protected CPU(String nome, Integer index) {
        super(nome, index, true);
    }

    public void prendi(Integer indice, Carta daAggiungere){
        super.prendi(indice, daAggiungere);
        this.carte[indice].nascondi();
    }
}
