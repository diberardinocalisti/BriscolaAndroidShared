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

import static gameEngine.Game.*;
import static multiplayer.ActivityMultiplayerGame.*;


public class engineMultiplayer extends Engine{
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
        GameRoom g = new GameRoom(gameCode, loginClass.getFBNome(),"null","null","null","null");
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
        c.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void inizializza(){
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
        String[] players = new String[]{"giocatore1", "giocatore2"};

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

        host.setNome(snapshot.getHost());
        enemy.setNome(snapshot.getEnemy());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void cartaGiocata(){
        System.out.println("carta enemy: " + snapshot.getGiocataDaEnemy());
        System.out.println("carta host: " + snapshot.getGiocataDaHost());

        if(giocante == null)
            giocante = host;

        String turno = ((GiocatoreMP) giocante).getRuolo();
        String app = (turno.equals("enemy") ? snapshot.getGiocataDaEnemy() : snapshot.getGiocataDaHost());

        if(app.equals("null"))
            return;

        final String separator = "#";
        String nome = app.split(separator)[0];
        int indice = Integer.parseInt(app.split(separator)[1]);

        Carta c = Engine.getCartaFromName(nome);

        Game.canPlay = false;

        Object event = new Object();

        if(c.getPortatore() == null)
            return;

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
        inizializza();

        Giocatore[] app = new Giocatore[]{host, enemy};

        mazzoOnline = engineMultiplayer.creaMazzoFirebase();
        snapshot.setCarteRimanenti(mazzoOnline);

        FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti", mazzoOnline);

        gameEngine.Engine.distribuisciCarte(() -> Engine.estraiBriscola(null), app);

        distribuisci = true;

        engineMultiplayer.onClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initEnemy(){
        inizializza();

        Giocatore[] app = new Giocatore[]{host, enemy};

        mazzoOnline = snapshot.getCarteRimanenti();

        Engine.creaMazzo(mazzoOnline);

        gameEngine.Engine.distribuisciCarte(() -> Engine.estraiBriscola(null), app);

        distribuisci = true;

        engineMultiplayer.onClick();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void prossimoTurno(GiocatoreMP p){
        if(p == null)
            return;

        Engine.prossimoTurno(p);
        giocante = p;

        System.out.println(host.getNome() + " è l'host");
        System.out.println(enemy.getNome() + " è l'enemy");
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void terminaManche(GiocatoreMP vincitore){
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost", "null");
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy", "null");

        if(snapshot != null){
            snapshot.setGiocataDaHost("null");
            snapshot.setGiocataDaEnemy("null");
        }

        Engine.terminaManche(vincitore);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void onClick(){
        for(Button b : carteBottoni)
            b.setOnClickListener(null);

        for(Button b : Game.user.bottoni){
            b.setOnClickListener(v -> {
                if(!Game.canPlay)
                    return;

                if(Game.user != giocante)
                    return;

                final Button bottone = (Button) v;
                final Carta carta = Engine.getCartaFromButton(bottone);

                int index = Game.user.getIndexFromCarta(carta);

                Game.canPlay = false;

                if(role.equals("HOST")){
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost",carta.getNome()+"#"+index);
                }else{
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy",carta.getNome()+"#"+index);
                }
            });
        }
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
