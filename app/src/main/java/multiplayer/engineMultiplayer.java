package multiplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import java.util.Random;

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Utility;


public class engineMultiplayer extends AppCompatActivity {

    public static String codiceStanza;

    public static void creaStanza(Context c)
    {
        int len = 5;

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        codiceStanza = sb.toString();

        DialogInterface.OnClickListener action = (dialog, which) -> accediAllaStanza(c, sb.toString());

        Utility.confirmDialog(c,"Il codice della tua stanza",sb.toString(),action,null);
    }

    public static void accediAllaStanza(Context c,String gameCode)
    {
        GameRoom g = new GameRoom(loginClass.getFBNome(),"null", gameCode);
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
    }


}
