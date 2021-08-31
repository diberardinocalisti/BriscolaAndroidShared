package multiplayer;

public class User {
    protected int vinte;
    protected int perse;

    public User(){}

    public User(int vinte, int perse){
        this.vinte = vinte;
        this.perse = perse;
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
}
