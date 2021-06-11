package multiplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Random;

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import multiplayer.Game.ActivityMultiplayerGame;

import static multiplayer.Game.ActivityMultiplayerGame.mazzoOnline;


public class engineMultiplayer extends AppCompatActivity {

    public static String codiceStanza;
    public static String role;
    private static final int CARTE_INIZIALI = 3;

    public static void creaStanza(Context c)
    {
        int len = 5;

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        codiceStanza = sb.toString();
        accediAllaStanza(c, sb.toString());
    }

    public static void accediAllaStanza(Context c,String gameCode)
    {
        GameRoom g = new GameRoom(gameCode,loginClass.getFBNome(),"null","null","null","null",-1,-1);
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
    }

   @RequiresApi(api = Build.VERSION_CODES.N)
   public static void startMultiplayerGame(AppCompatActivity c)
   {
       Game.initialize(c);
       //Devo inserire le carte rimanenti, quindi tutto il mazzo
       FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",creaMazzoFirebase());

   }

   @RequiresApi(api = Build.VERSION_CODES.N)
   public static String creaMazzoFirebase()
   {
       String mazzoFb = "";
       Engine.creaMazzo();

       for(Carta c : Game.mazzo)
       {
           mazzoFb += c.getNome()+";";
       }

       mazzoFb = mazzoFb.substring(0, mazzoFb.length()-1);

        return mazzoFb;
   }

   public static String[] getInitialCards(){
       String[] daDare = new String[CARTE_INIZIALI];

       /*ValueEventListener postListener = new ValueEventListener() {
           @Override
           public void onDataChange(DataSnapshot dataSnapshot) {
               // Get Post object and use the values to update the UI
               System.out.println("Changed!!!");
               GameRoom g = dataSnapshot.getValue(GameRoom.class);

               String rimanenti = g.getCarteRimanenti();
               String[] singole = rimanenti.split(";");

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

       String[] singole = mazzoOnline.split(";");
       for(int i = 0; i< CARTE_INIZIALI ;i++)
       {
           daDare[i] = singole[i];
       }

       return daDare;

    }

    public static ArrayList<String> stringToArray()
    {
        ArrayList<String> appo = new ArrayList<String>();

        for( String s : mazzoOnline.split(";") )
        {
            appo.add(s);
        }

        return appo;
    }


    public static ArrayList<String> removeCardsFromArray(String carteUscite)
    {
        String[] carte = carteUscite.split(";");    //3_coppe | 2_coppe | 3_bastoni
        ArrayList<String> tot = stringToArray();

        for(int i=0;i<CARTE_INIZIALI;i++)
        {
            tot.remove(carte[i]);
        }

        return tot;
    }

    public static String arrayListToString(ArrayList<String> tot)
    {
        String appo = "";

        for(int i=0;i<tot.size();i++)
        {
            appo += tot.get(i) + ";";
        }

        appo = appo.substring(0,appo.length()-1);

        return appo;
    }

}
