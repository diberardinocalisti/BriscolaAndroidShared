package multiplayer;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import java.util.Random;

import gameEngine.Utility;


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

        DialogInterface.OnClickListener action = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                accediAllaStanza(c);
            }
        };

        Utility.confirmDialog(c,"Il codice della tua stanza",sb.toString(),action,null);
    }

    public static void accediAllaStanza(Context c)
    {
        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
    }


}
