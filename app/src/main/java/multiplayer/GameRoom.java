package multiplayer;

public class GameRoom {
    private String gameCode;

    private String host, enemy;
    
    private String carteRimanenti;

    private String giocataDaHost;
    private String giocataDaEnemy;
    private String turno;

    public GameRoom(){};

    public GameRoom(String gameCode, String host, String enemy, String carteRimanenti, String giocataDaHost, String giocataDaEnemy, String turno) {
        this.gameCode = gameCode;
        this.host = host;
        this.enemy = enemy;
        this.carteRimanenti = carteRimanenti;
        this.giocataDaHost = giocataDaHost;
        this.giocataDaEnemy = giocataDaEnemy;
        this.turno = turno;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
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

    public String getCarteRimanenti() {
        return carteRimanenti;
    }

    public void setCarteRimanenti(String carteRimanenti) {
        this.carteRimanenti = carteRimanenti;
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

    public String getTurno() {
        return turno;
    }

    public void setTurno(String turno) {
        this.turno = turno;
    }

    @Override
    public String toString() {
        return "GameRoom{" +
                "gameCode='" + gameCode + '\'' +
                ", host='" + host + '\'' +
                ", enemy='" + enemy + '\'' +
                ", carteRimanenti='" + carteRimanenti + '\'' +
                ", giocataDaHost='" + giocataDaHost + '\'' +
                ", giocataDaEnemy='" + giocataDaEnemy + '\'' +
                ", turno='" + turno + '\'' +
                '}';
    }
}
