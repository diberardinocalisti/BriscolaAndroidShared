package multiplayer;

public class User {
    protected int vinte;
    protected int perse;
    protected int monete;

    public User(){}

    public User(int vinte, int perse, int monete) {
        this.vinte = vinte;
        this.perse = perse;
        this.monete = monete;
    }

    public int getVinte() {
        return vinte;
    }

    public void setVinte(int vinte) {
        this.vinte = vinte;
    }

    public int getPerse() {
        return perse;
    }

    public void setPerse(int perse) {
        this.perse = perse;
    }

    public int getMonete() {
        return monete;
    }

    public void setMonete(int monete) {
        this.monete = monete;
    }
}
