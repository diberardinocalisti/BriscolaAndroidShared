package Home;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Utility;
import multiplayer.MultiplayerActivity;


public class MainMenu extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startGame(Button b, MainActivity main)
    {
        String tmp = b.getText().toString().toLowerCase();

        switch(tmp){
            case "singleplayer":
                Intent intent = new Intent(main, ActivityGame.class);
                intent.putExtra("multiplayer",false);
                main.startActivity(intent);
                break;

            case "multiplayer":
                if(loginClass.isFacebookLoggedIn())
                {
                    Intent i = new Intent(main, MultiplayerActivity.class);
                    main.startActivity(i);
                }else
                {
                    Toast.makeText(main,"Accedi premendo il bottone \"Il mio profilo\" per iniziare a giocare con i tuoi amici!",Toast.LENGTH_LONG).show();
                }
                break;

            case "come si gioca?":
                String title = "Introduzione a Briscola";
                String msg = "Per giocare a Briscola si utilizza un mazzo di 40 carte diviso in 4 semi, ciascuno di 10 carte.\nNel mazzo vi sono un totale di 120 punti, pertanto vincerà il round chi ne realizza almeno 61. \nSe i punti sono 60 il round è considerato pari.\n\nDi seguito i punteggi:\nAsso: 11 punti;\nTre: 10 punti;\nRe: 4 punti;\nCavallo: 3 punti;\nFante: 2 punti;\n\nBUON DIVERTIMENTO!";

                Utility.createDialog(main,title, msg);
                break;

            case "esci dal gioco":
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;

            case "il mio profilo":
                Intent in = new Intent(main, LoginActivity.class);
                main.startActivity(in);
                //Toast.makeText(main,"prova",Toast.LENGTH_SHORT).show();
                break;
        }
    }
}