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

import java.util.Random;

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import multiplayer.Game.ActivityMultiplayerGame;


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

   public static String[] getInitialCards() throws InterruptedException {
       String[] daDare = new String[CARTE_INIZIALI];
       Object event = new Object();

       new Thread(() -> {
           ValueEventListener postListener = new ValueEventListener() {
               @Override
               public void onDataChange(DataSnapshot dataSnapshot) {
                   // Get Post object and use the values to update the UI
                   System.out.println("Changed!!");
                   GameRoom g = dataSnapshot.getValue(GameRoom.class);

                   String rimanenti = g.getCarteRimanenti();
                   String[] singole = rimanenti.split(";");

                   for(int i = 0; i< CARTE_INIZIALI ;i++)
                   {
                       daDare[i] = singole[i];
                   }

                   event.notifyAll();
               }

               @Override
               public void onCancelled(DatabaseError databaseError) {
               }
           };
           FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(postListener);
       }).start();

       synchronized (event){
           event.wait();
           return daDare;
       }
    }

}
