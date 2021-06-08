package multiplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Random;

import Login.loginClass;
import gameEngine.Utility;

import firebase.FirebaseClass;


public class engineMultiplayer extends AppCompatActivity {

    public static void creaStanza(Context c)
    {
        String codice = null;
        int len = 5;

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        DialogInterface.OnClickListener action = (dialog, which) -> accediAllaStanza(c, sb.toString());

        Utility.confirmDialog(c,"Il codice della tua stanza",sb.toString(),action,null);
    }

    public static void accediAllaStanza(Context c,String gameCode)
    {
        /*Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);*/
        GameRoom g = new GameRoom(loginClass.getFBNome(),"null", gameCode);
        FirebaseClass.addToFirebase(g);

        Toast.makeText(c,"Stanza creata con successo!",Toast.LENGTH_LONG);
    }


}
