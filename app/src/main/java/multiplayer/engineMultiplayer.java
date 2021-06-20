package multiplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

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

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

import static multiplayer.Game.ActivityMultiplayerGame.mazzoOnline;


public class engineMultiplayer extends AppCompatActivity {

    public static String codiceStanza;
    public static String role;
    public static final String DELIMITER = ";";
    private static final int CARTE_INIZIALI = 3;

    public static void creaStanza(AppCompatActivity c)
    {
        int len = 5;

        String chars = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        codiceStanza = sb.toString();
        accediAllaStanza(c, sb.toString());
    }

    public static void accediAllaStanza(AppCompatActivity c,String gameCode)
    {
        GameRoom g = new GameRoom(gameCode,loginClass.getFBNome(),"null","null","null","null",-1,-1,"no");
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
        c.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void inizializza(ActivityMultiplayerGame c){
        Game.initialize(c);
        creaGiocatori(c);
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public static void startMultiplayerGame(ActivityMultiplayerGame c)
   {
       inizializza(c);
       //Devo inserire le carte rimanenti, quindi tutto il mazzo
       FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",creaMazzoFirebase());
   }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public static String creaMazzoFirebase(){
        String mazzoFb = new String();
        Engine.creaMazzo();

        for(Carta c : Game.mazzo)
            mazzoFb += c.getNome() + DELIMITER;

        return mazzoFb.substring(0, mazzoFb.length()-1);
   }

    public static void creaGiocatori(ActivityMultiplayerGame c){
        String[] players;

        if(c.roleId.equals("host"))
            players = new String[]{c.enemy, c.host};
        else
            players = new String[]{c.host, c.enemy};

        for(int i = 0; i < Game.nGiocatori; i++)
            Game.giocatori[i] = new GiocatoreMP(players[i], i);

        Game.user = Game.giocatori[1];
    }

    public static void aggiornaNCarte(Integer n_Carte){

    }

    public static ArrayList<String> stringMazzoToArray()
    {
        ArrayList<String> mazzoArrlst = new ArrayList<>();
        String[] mazzoArr = mazzoOnline.split(DELIMITER);

        Collections.addAll(mazzoArrlst, mazzoArr);

        return mazzoArrlst;

        /*
            ArrayList<String> appo = new ArrayList<String>();

            for( String s : mazzoOnline.split(DELIMITER) )
                appo.add(s);

            return appo;
         */
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
    public static String arrayListToString(ArrayList<String> tot)
    {
        return String.join(DELIMITER, tot);
        /*String appo = "";

        for(int i=0;i<tot.size();i++)
        {
            appo += tot.get(i) + DELIMITER;
        }

        appo = appo.substring(0,appo.length()-1);

        return appo;*/
    }

}
