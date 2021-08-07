package multiplayer;

import com.google.firebase.database.DataSnapshot;

public class GameRoom {
    public static String gameCode;
    private String host, enemy;
    private String mazzo;
    private String giocataDaHost, giocataDaEnemy;
    private String idHost, idEnemy;
    private String chat;

    public GameRoom(){};

    public GameRoom(String gameCode, String host, String enemy, String idHost, String idEnemy, String mazzo, String giocataDaHost, String giocataDaEnemy, String chat) {
        GameRoom.gameCode = gameCode;
        this.host = host;
        this.enemy = enemy;
        this.idHost = idHost;
        this.idEnemy = idEnemy;
        this.mazzo = mazzo;
        this.giocataDaHost = giocataDaHost;
        this.giocataDaEnemy = giocataDaEnemy;
        this.chat = chat;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        GameRoom.gameCode = gameCode;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getEnemy() {
        return enemy;
    }

    public void setEnemy(String enemy) {
        this.enemy = enemy;
    }

    public String getIdHost() {
        return idHost;
    }

    public void setIdHost(String idHost) {
        this.idHost = idHost;
    }

    public String getIdEnemy() {
        return idEnemy;
    }

    public void setIdEnemy(String idEnemy) {
        this.idEnemy = idEnemy;
    }

    public String getMazzo() {
        return mazzo;
    }

    public void setMazzo(String mazzo) {
        this.mazzo = mazzo;
    }

    public String getChat(){
        return chat;
    }

    public void setChat(String chat){
        this.chat = chat;
    }

    public String getGiocataDaHost() {
        return giocataDaHost;
    }

    public void setGiocataDaHost(String giocataDaHost) {
        this.giocataDaHost = giocataDaHost;
    }

    public String getGiocataDaEnemy() {
        return giocataDaEnemy;
    }

    public void setGiocataDaEnemy(String giocataDaEnemy) {
        this.giocataDaEnemy = giocataDaEnemy;
    }


    @Override
    public String toString() {
        return "GameRoom{" +
                "gameCode='" + gameCode + '\'' +
                ", host='" + host + '\'' +
                ", enemy='" + enemy + '\'' +
                ", mazzo='" + mazzo + '\'' +
                ", giocataDaHost='" + giocataDaHost + '\'' +
                ", giocataDaEnemy='" + giocataDaEnemy + '\'' +
                ", chat='" + chat + '\'' +
                '}';
    }

    public static boolean isGameRoom(DataSnapshot d){
        return d.hasChild("gameCode");
    }
}
