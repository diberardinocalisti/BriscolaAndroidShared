package Home;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.ProvaLoginFirebase;
import com.example.briscolav10.R;
import com.facebook.login.Login;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Settings;
import gameEngine.Utility;
import multiplayer.MultiplayerActivity;
import okhttp3.internal.Util;


public class MainMenu {
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
                    Intent i = new Intent(main, ProvaLoginFirebase.class);
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
                main.onBackPressed();
                break;

            // Il mio profilo;
            case "button2":
                Intent in = new Intent(main, ProvaLoginFirebase.class);
                main.startActivity(in);
                break;
        }
    }
}