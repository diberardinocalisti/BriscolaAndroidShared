package Home;

import android.content.Intent;
import android.os.Build;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;

import gameEngine.Utility;


public class MainMenu extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startGame(Button b, MainActivity main)
    {
        String tmp = b.getText().toString();

        switch(tmp){
            case "1 VS CPU":
                Intent intent = new Intent(main, ActivityGame.class);
                main.startActivity(intent);
                break;

            case "Come si gioca?":
                String title = "Alcune regole prima di iniziare";

                String msg = "Per giocare a Briscola si utilizza un mazzo di 40 carte diviso in 4 semi, ciascuno di 10 carte. Nel mazzo vi sono un totale di 120 punti, pertanto vincerà il round chi ne realizza almeno 61; se i punti sono 60 il round è considerato pari.Di seguito i punteggi:\nAsso --> 11 punti\nTre --> 10 punti\nRe --> 4 punti\nCavallo --> 3 punti\nFante --> 2 punti\n\nBUON DIVERTIMENTO!";

                Utility.createDialog(main,title, msg);
                break;

            case "Esci dal gioco":
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
                break;
        }
    }
}