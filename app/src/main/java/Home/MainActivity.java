package Home;

import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

import Login.loginClass;

public class MainActivity extends AppCompatActivity {

    Button button[] = new Button[4];

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


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

    }
}
