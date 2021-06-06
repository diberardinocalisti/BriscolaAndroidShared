package Home;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import static Login.loginClass.isFacebookLoggedIn;

public class MainActivity extends AppCompatActivity {

    Button button[] = new Button[4];
    ImageButton rank;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        rank = (ImageButton) findViewById(R.id.rank);
        rank.setVisibility(View.INVISIBLE);

        /*
        *   Collego i bottoni del file XML agli elementi dell'array
        * prova
        **/
        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);

            button[index].setOnClickListener(v -> new MainMenu().startGame(button[index], this));
        }

        if(isFacebookLoggedIn())
        {
            rank.setVisibility(View.VISIBLE);
        }else
        {
            rank.setVisibility(View.INVISIBLE);
        }

    }
}
