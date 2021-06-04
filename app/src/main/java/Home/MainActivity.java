package Home;

import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

public class MainActivity extends AppCompatActivity {

    Button button[] = new Button[4];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*
        *   Collego i bottoni del file XML agli elementi dell'array
        **/
        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);

            button[index].setOnClickListener(v -> new MainMenu().startGame(button[index], this));
        }
    }
}