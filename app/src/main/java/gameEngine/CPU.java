package gameEngine;

public class CPU extends Giocatore {
    protected CPU(String nome, Integer index) {
        super(nome, index, true);
        Game.CPU = this;
    }

    public void prendi(Integer indice, Carta daAggiungere){
        super.prendi(indice, daAggiungere);
        this.carte[indice].nascondi();
    }

    public void scopriCarte(){
        for(Carta c : carte){
            c.mostra();
        }
    }

    public void copriCarte(){
        for(Carta c : carte){
            c.nascondi();
        }
    }
}
