package multiplayer;

import android.content.Context;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.common.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Stack;

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


public class engineMultiplayer extends AppCompatActivity {

    public static String codiceStanza;
    public static String role;
    public static final String DELIMITER = ";";

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
        String[] players;

        if(roleId.equals("host"))
            players = new String[]{enemy, host};
        else
            players = new String[]{host, enemy};

        for(int i = 0; i < Game.nGiocatori; i++)
            Game.giocatori[i] = new GiocatoreMP(players[i], i);

        Game.user = Game.giocatori[1];
    }

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

        // Todo: il controllo del turno va fatto nell'onClick
        Game.canPlay = (roleId.equals(turno));

        //setButton(Game.canPlay);

        giocante = Game.canPlay ? giocatori[0] : Game.user;

        Object event = new Object();

        // @Todo: Gestire i turni;
        if(!roleId.equals(turno)){
            Engine.muoviCarta(c.getButton(), Game.carte[c.getPortatore().index + I_CAMPO_GIOCO[0]], c,false,true,event);
            giocaCarta(c, event);
        }else{
            View daMuovere = Game.carteBottoni[indice];

            assert c != null;
            c.setButton(daMuovere);

            muoviCarta(c.getButton(), Game.carte[I_CAMPO_GIOCO[0]], c,false, true, event);
            giocaCarta(c, event);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void giocaCarta(Carta c, Object event){
        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();
                    activity.runOnUiThread(() -> {
                        Game.canPlay = true;

                        giocante.lancia(c);
                        final Giocatore vincente = doLogic(c, getOtherCarta(c));

                        if(vincente == null) {
                            prossimoTurno(getOtherPlayer(giocante));
                        }else{
                            // Todo: ridefinire il metodo terminaManche per il multiplayer;
                            new Handler().postDelayed(() -> terminaManche(vincente), 1750);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void distribuisciCarte(){
        if(role.equals("HOST")){
            mazzoOnline = engineMultiplayer.creaMazzoFirebase();
            snapshot.setCarteRimanenti(mazzoOnline);

            FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",mazzoOnline);

            Game.user.svuotaMazzo();

            for(int i = 0; i < Game.nCarte; i++)
                Game.user.pesca();
        }else{
            new Handler().postDelayed(() -> {
                mazzoOnline = snapshot.getCarteRimanenti();
                Engine.creaMazzo(mazzoOnline);
                creaMazzoIniziale();

                Game.user.svuotaMazzo();

                for(int i = 0; i < Game.nCarte; i++)
                    Game.user.pesca();
            }, 1750);
        }

        distribuisci = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void creaMazzoIniziale(){
        mazzoIniziale = new Carta[Game.dimensioneMazzo];
        ArrayList<Carta> mazzoApp = new ArrayList<>();

        for(String seme : semi)
            for(Integer i = 1; i <= 10; i++)
                mazzoApp.add(new Carta(i, seme));

        mazzoIniziale = mazzoApp.toArray(new Carta[0]);
    }
    
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void onClick(){
        for(Button b : Game.user.bottoni){
            b.setOnClickListener(v -> {
                Game.canPlay = roleId.equals(snapshot.getTurno());

                if(!Game.canPlay)
                    return;

                Carta c = Engine.getCartaFromButton(v);
                int index = Game.user.getIndexFromCarta(c);

                Game.canPlay = false;

                if(role.equals("HOST")){
                    //Modifico giocataDaHost
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost",c.getNome()+"#"+index);
                    FirebaseClass.editFieldFirebase(codiceStanza,"turno","enemy");
                }else{
                    //Modifico giocataDaEnemy
                    FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy",c.getNome()+"#"+index);
                    FirebaseClass.editFieldFirebase(codiceStanza,"turno","host");
                }
            });
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void aggiornaNCarte(){
        String carteDisponibili = snapshot.getCarteRimanenti();
        String[] strTok = carteDisponibili.split(DELIMITER);

        Engine.aggiornaNCarte(strTok.length);
    }

    public static ArrayList<String> stringMazzoToArray(){
        ArrayList<String> mazzoArrlst = new ArrayList<>();
        String[] mazzoArr = mazzoOnline.split(DELIMITER);

        Collections.addAll(mazzoArrlst, mazzoArr);

        return mazzoArrlst;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static ArrayList<Carta> stringToArrayCarta(){
        String[] mazzoStringa = stringMazzoToArray().toArray(new String[0]);
        ArrayList<Carta> mazzoCarta = new ArrayList<>();

        for(String c : mazzoStringa){
            mazzoCarta.add(Engine.getCartaFromName(c));
        }

        return mazzoCarta;
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
