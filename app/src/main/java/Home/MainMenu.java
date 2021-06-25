package Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;
import com.facebook.login.Login;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Settings;
import gameEngine.Utility;
import multiplayer.MultiplayerActivity;
import okhttp3.internal.Util;


public class MainMenu extends AppCompatActivity {
    public static boolean carteScoperte;

    @RequiresApi(api = Build.VERSION_CODES.N)
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
                    Intent i = new Intent(main, LoginActivity.class);
                    main.startActivity(i);
                }
                break;

            // Come si gioca?
            case "button4":
                String title = main.getString(R.string.intro);
                String msg = main.getString(R.string.howtoplay);
                Utility.createDialog(main, title, msg);
                break;

            // Uscire dal gioco;
            case "button3":
                String titolo = main.getString(R.string.closegame);
                String messaggio = main.getString(R.string.confirmleave);

                Utility.confirmDenyDialog(main, titolo, messaggio, (dialog, which) -> android.os.Process.killProcess(android.os.Process.myPid()), null);

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