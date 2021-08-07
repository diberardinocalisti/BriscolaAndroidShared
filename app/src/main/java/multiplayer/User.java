package multiplayer;

public class User {
    private int vinte;
    private int perse;
    private String nome;
    private String cognome;

    public User(int vinte, int perse, String nome, String cognome) {
        this.vinte = vinte;
        this.perse = perse;
        this.nome = nome;
        this.cognome = cognome;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCognome() {
        return cognome;
    }

    public void setCognome(String cognome) {
        this.cognome = cognome;
    }
}


