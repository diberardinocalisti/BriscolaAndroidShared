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
    public static void inizializza(AppCompatActivity c){
        Game.initialize(c);
    }

   @RequiresApi(api = Build.VERSION_CODES.O)
   public static void startMultiplayerGame(AppCompatActivity c)
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

   public static String[] getInitialCards(){
        /*TODO: Direi di servirsi del metodo pesca di classe GiocatoreMP per pescare le 3 carte iniziali, nel metodo
           getInitialCards ciclerei CARTE_INIZIALI volte invocando appunto il metodo pesca di classe GiocatoreMP
           ancora da ridefinire per il multiplayer), in questo modo evitiamo ripetizioni e ci semplifichiamo il lavoro;
        */
       /*ValueEventListener postListener = new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // Get Post object and use the values to update the UI
               System.out.println("Changed!!!");
               GameRoom g = dataSnapshot.getValue(GameRoom.class);

               String rimanenti = g.getCarteRimanenti();
               String[] singole = rimanenti.split(DELIMITER);

               for(int i = 0; i< CARTE_INIZIALI ;i++)
               {
                   daDare[i] = singole[i];
               }

           }

           @Override
           public void onCancelled(DatabaseError databaseError) {
           }
       };
       FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(postListener);*/

       String[] mazzo = mazzoOnline.split(DELIMITER);
       String[] daDare = new String[CARTE_INIZIALI];

       System.arraycopy(mazzo, 0, daDare, 0, CARTE_INIZIALI);

       return daDare;
    }

    public static ArrayList<String> stringToArray()
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


    public static ArrayList<String> removeCardsFromArray(String carteUscite)
    {
        String[] carte = carteUscite.split(DELIMITER);    //3_coppe | 2_coppe | 3_bastoni
        ArrayList<String> tot = stringToArray();

        for(int i = 0; i < CARTE_INIZIALI; i++)
            tot.remove(carte[i]);

        return tot;
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
