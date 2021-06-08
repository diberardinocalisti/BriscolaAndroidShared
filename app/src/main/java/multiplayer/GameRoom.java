package multiplayer;

public class GameRoom {

    //Codice stanza
    private String gameCode;

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    //Nome giocatore che ha avvbiato la partita
    private String host;
    //Nome giocatore che partecipa alla partita
    private String enemy;

    public GameRoom(){};

    public GameRoom(String host, String enemy, String gameCode) {
        this.host = host;
        this.enemy = enemy;
        this.gameCode = gameCode;
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
}
