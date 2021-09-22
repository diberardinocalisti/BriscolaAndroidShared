package multiplayer;

public class EmailUser extends User{
    private String email;
    private String password;
    private String avatar;

    public EmailUser(){}

    public EmailUser(int vinte, int perse, int monete,String avatar, String email, String password) {
        super(vinte, perse,monete);
        this.email = email;
        this.password = password;
        this.avatar = avatar;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
