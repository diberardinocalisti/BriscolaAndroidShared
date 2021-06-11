package Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Settings;
import gameEngine.Utility;
import multiplayer.MultiplayerActivity;
import okhttp3.internal.Util;


public class MainMenu extends AppCompatActivity {
    public static boolean carteScoperte;
    public static String tipoCarte;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startGame(String nomeB, MainActivity main){
        switch(nomeB){
            // Singleplayer;
            case "button1":
                Intent intent = new Intent(main, ActivityGame.class);
                intent.putExtra("multiplayer",false);
                main.startActivity(intent);
                break;

            // Multiplayer;
            case "button5":
                if(loginClass.isFacebookLoggedIn()){
                    Intent i = new Intent(main, MultiplayerActivity.class);
                    main.startActivity(i);
                }else{
                    Toast.makeText(main,"Accedi premendo il bottone \"Il mio profilo\" per iniziare a giocare con i tuoi amici!",Toast.LENGTH_LONG).show();
                }
                break;

            // Come si gioca?
            case "button4":
                String title = "Introduzione a Briscola";
                String msg = "Per giocare a Briscola si utilizza un mazzo di 40 carte diviso in 4 semi, ciascuno di 10 carte.\nNel mazzo vi sono un totale di 120 punti, pertanto vincerà il round chi ne realizza almeno 61. \nSe i punti sono 60 il round è considerato pari.\n\nDi seguito i punteggi:\nAsso: 11 punti\nTre: 10 punti\nRe: 4 punti\nCavallo: 3 punti\nFante: 2 punti\n\nBUON DIVERTIMENTO!";

                Utility.createDialog(main, title, msg);
                break;

            // Uscire dal gioco;
            case "button3":
                String titolo = "Uscire dal gioco";
                String messaggio = "Sei sicuro di voler uscire dal gioco?";

                Utility.confirmDenyDialog(main, titolo, messaggio, (dialog, which) -> {
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
                }, null);

                break;

            // Il mio profilo;
            case "button2":
                Intent in = new Intent(main, LoginActivity.class);
                main.startActivity(in);
                break;

            case "button6":
                new Settings().createSettingsMenu(main);
            break;
        }
    }
}