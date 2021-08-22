package multiplayer;

public class EmailUser {
    private int vinte;
    private int perse;
    private String email;
    private String password;

    public EmailUser(int vinte, int perse, String email, String password) {
        this.vinte = vinte;
        this.perse = perse;
        this.email = email;
        this.password = password;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
