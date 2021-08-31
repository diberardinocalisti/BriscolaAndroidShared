package multiplayer;

public class FbUser extends User {
    private String nome;
    private String cognome;

    public FbUser(){}

    public FbUser(int vinte, int perse, String nome, String cognome) {
        super(vinte, perse);
        this.nome = nome.trim();
        this.cognome = cognome.trim();
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


