package multiplayer;

import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import java.util.Random;

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Engine;


public class engineMultiplayer extends AppCompatActivity {

    public static String codiceStanza;
    public static String role;

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

   public static void startMultiplayerGame()
   {
       //Devo inserire le carte rimanenti, quindi tutto il mazzo
       FirebaseClass.editFieldFirebase(codiceStanza,"carteRimanenti",creaMazzoFirebase());

   }

   public static String creaMazzoFirebase()
   {
       String[] numeri = {"1","2","3","4","5","6","7","8","9","10"};
       String[] semi = {"denara","spade","coppe","bastoni"};
        String mazzo = "";

        for(String s : semi)
        {
            for(String n : numeri)
            {
                mazzo += n+"_"+s+";";
            }
        }

        mazzo = mazzo.substring(0, mazzo.length()-1);
        return mazzo;
   }

}
