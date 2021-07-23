package multiplayer;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;

import java.util.ArrayList;
import java.util.Random;

import Home.MainActivity;
import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Giocatore;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;
import static multiplayer.Game.ActivityMultiplayerGame.*;


public class engineMultiplayer {
    public static String codiceStanza;
    public static String role;
    public static final String DELIMITER = ";";
    public static Giocatore host, enemy;

    public static void creaStanza(AppCompatActivity c){
        int len = 5;

        String chars = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        codiceStanza = sb.toString();
        accediAllaStanza(c, sb.toString());
    }

    public static void accediAllaStanza(AppCompatActivity c,String gameCode){
        GameRoom g = new GameRoom(gameCode,loginClass.getFBNome(),"null","null","null","null",-1,-1,"host");
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
        c.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void inizializza(ActivityMultiplayerGame c){
        Game.initialize(c);
        creaGiocatori();
        Engine.pulisciTavolo();
        Engine.pulisciPrese();
    }

    public static void checkIfSomeoneLeft(){
        String host = snapshot.getHost();
        String enemy = snapshot.getEnemy();

        if(host.equals("null") && !enemy.equals("null"))
        {
            if(role.equals("HOST")) {
                Toast.makeText(activity, activity.getString(R.string.youleft), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, activity.getString(R.string.enemyleft), Toast.LENGTH_SHORT).show();
                Utility.goTo(activity, MainActivity.class);
            }
            onStop = true;
        }

        if(enemy.equals("null") && !host.equals("null")) {
            if (!role.equals("HOST")){
                Toast.makeText(activity, activity.getString(R.string.youleft), Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(activity, activity.getString(R.string.enemyleft), Toast.LENGTH_SHORT).show();
                Utility.goTo(activity, MainActivity.class);
            }

            onStop = true;
        }
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public static String creaMazzoFirebase(){
        String mazzoFb = new String();
        Engine.creaMazzo();

        for(Carta c : Game.mazzo)
            mazzoFb += c.getNome() + DELIMITER;

        return mazzoFb.substring(0, mazzoFb.length()-1);
   }

    public static void creaGiocatori(){
        String[] players = new String[]{ActivityMultiplayerGame.host, ActivityMultiplayerGame.enemy};

        for(int i = 0; i < Game.nGiocatori; i++)
            Game.giocatori[i] = new GiocatoreMP(players[i], i);

        Game.user = giocatori[1];
        Game.opp = giocatori[0];

        if(role.equals("HOST")) {
            host = Game.user;
            enemy = Game.opp;

            ((GiocatoreMP) Game.user).setRuolo("host");
            ((GiocatoreMP) Game.opp).setRuolo("enemy");
        }else{
            host = Game.opp;
            enemy = Game.user;

            ((GiocatoreMP) Game.user).setRuolo("enemy");
            ((GiocatoreMP) Game.opp).setRuolo("host");
        }
    }

    // @Todo: da gestire i turni (comincia sempre l'host)
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void cartaGiocata(){
        String turno = snapshot.getTurno();
        String app = (turno.equals("enemy") ? snapshot.getGiocataDaHost() : snapshot.getGiocataDaEnemy());

        if(app.equals("null"))
            return;

        final String separator = "#";
        String nome = app.split(separator)[0];
        int indice = Integer.parseInt(app.split(separator)[1]);

        Carta c = Engine.getCartaFromName(nome);

        Game.canPlay = false;
        giocante =  (roleId.equals(turno)) ? Game.user : Game.opp;

        Object event = new Object();

        if(c.getPortatore() == null)
            return;

        // @Todo: Gestire i turni;
        View daMuovere = c.getPortatore().bottoni[indice];

        if(!daMuovere.isEnabled())
            return;

        c.setButton(daMuovere);

        muoviCarta(daMuovere, Game.carte[c.getPortatore().index + I_CAMPO_GIOCO[lastManche][0]], c,false, true, false, event);
        giocaCarta(c, event);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void giocaCarta(Carta c, Object event){
        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();
                    activity.runOnUiThread(() -> {
                        Game.canPlay = true;

                        c.getPortatore().lancia(c);

                        final Giocatore vincente = doLogic(c, getOtherCarta(c));

                        if(vincente == null) {
                            prossimoTurno((GiocatoreMP) getOtherPlayer(c.getPortatore()));
                        }else{
                            new Handler().postDelayed(() -> terminaManche((GiocatoreMP) vincente), intermezzo);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initHost(){
        Giocatore[] app = new Giocatore[nGiocatori];
        app[0] = host;
        app[1] = enemy;

        mazzoOnline = engineMultiplayer.creaMazzoFirebase();
        snapshot.setCarteRimanenti(mazzoOnline);

        FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti", mazzoOnline);

        Engine.estraiBriscola();
        gameEngine.Engine.distribuisciCarte(null, app);

        distribuisci = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initEnemy(){
        Giocatore[] app = new Giocatore[nGiocatori];
        app[0] = host;
        app[1] = enemy;

        mazzoOnline = snapshot.getCarteRimanenti();

        Engine.creaMazzo(mazzoOnline);
        Engine.estraiBriscola();

        gameEngine.Engine.distribuisciCarte(null, app);

        distribuisci = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void prossimoTurno(GiocatoreMP p){
        if(p == null)
            return;

        engineMultiplayer.setTurno(p.getRuolo());
        Engine.prossimoTurno(p);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void terminaManche(GiocatoreMP vincitore){
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost", "null");
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy", "null");

        snapshot.setGiocataDaHost("null");
        snapshot.setGiocataDaEnemy("null");

        Engine.terminaManche(vincitore);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void onClick(){
        for(Button b : Game.user.bottoni){
            b.setOnClickListener(v -> {
                if(!Game.canPlay)
                    return;

                if(!roleId.equals(snapshot.getTurno()))
                    return;

                final Button bottone = (Button) v;
                final Carta carta = Engine.getCartaFromButton(bottone);

                int index = Game.user.getIndexFromCarta(carta);

                Game.canPlay = false;

                if(role.equals("HOST")){
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost",carta.getNome()+"#"+index);

                    // DA RIVEDERE!! Bisogna gestire i turni in base a chi vince la manche, e non sempre in base a chi ha tirato per ultimo.
                    if(getCarteGiocate().length < nGiocatori)
                        setTurno("enemy");
                }else{
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy",carta.getNome()+"#"+index);

                    // DA RIVEDERE!! Bisogna gestire i turni in base a chi vince la manche, e non sempre in base a chi ha tirato per ultimo.
                    if(getCarteGiocate().length < nGiocatori)
                        setTurno("host");
                }
            });
        }
    }

    public static void setTurno(String turno){
        FirebaseClass.editFieldFirebase(codiceStanza,"turno", turno);
        snapshot.setTurno(turno);
    }


    public static void removeCardFromMazzo(String carta){
        mazzoOnline = mazzoOnline.replace(carta, "");

        if(mazzoOnline.startsWith(DELIMITER))
            mazzoOnline = mazzoOnline.substring(1);

        if(mazzoOnline.endsWith(DELIMITER))
            mazzoOnline.substring(0, mazzoOnline.length() - 1);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String arrayListToString(ArrayList<String> tot){
        return String.join(DELIMITER, tot);
    }
}
