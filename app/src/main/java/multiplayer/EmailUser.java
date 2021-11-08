package multiplayer;

public class EmailUser extends User{
    private String email;
    private String password;
    private String avatar;
    public boolean avatar_21, avatar_22, avatar_23, avatar_24, avatar_25;

    public EmailUser(){}

    public EmailUser(int vinte, int perse, int monete,String avatar, String email, String password){
        this(vinte, perse, monete, avatar, email, password, false, false, false, false, false);
    }
    
    public EmailUser(int vinte, int perse, int monete,String avatar, String email, String password, boolean avatar_21, boolean avatar_22, boolean avatar_23, boolean avatar_24, boolean avatar_25) {
        super(vinte, perse,monete);
        this.email = email;
        this.password = password;
        this.avatar = avatar;
        this.avatar_21 = avatar_21;
        this.avatar_22 = avatar_22;
        this.avatar_23 = avatar_23;
        this.avatar_24 = avatar_24;
        this.avatar_25 = avatar_25;
    }

    public boolean hasAvatar_21(){
        return avatar_21;
    }

    public void setAvatar_21(boolean avatar_21){
        this.avatar_21 = avatar_21;
    }

    public boolean hasAvatar_22(){
        return avatar_22;
    }

    public void setAvatar_22(boolean avatar_22){
        this.avatar_22 = avatar_22;
    }

    public boolean hasAvatar_23(){
        return avatar_23;
    }

    public void setAvatar_23(boolean avatar_23){
        this.avatar_23 = avatar_23;
    }

    public boolean hasAvatar_24(){
        return avatar_24;
    }

    public void setAvatar_24(boolean avatar_24){
        this.avatar_24 = avatar_24;
    }

    public boolean hasAvatar_25(){
        return avatar_25;
    }

    public void setAvatar_25(boolean avatar_25){
        this.avatar_25 = avatar_25;
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
