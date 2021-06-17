package Home;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.briscolav10.R;

import Login.loginClass;
import multiplayer.Game.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;

public class MainActivity extends AppCompatActivity {

    View button[] = new View[6];
    ImageButton rank;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        rank = findViewById(R.id.rank);

        if(loginClass.isFacebookLoggedIn())
            rank.setVisibility(View.VISIBLE);
        else
            rank.setVisibility(View.INVISIBLE);

        ActivityMultiplayerGame.onStop = false;
        /*
        *   Collego i bottoni del file XML agli elementi dell'array
        *   prova
        *   */
        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);
            button[index].setOnClickListener(v -> new MainMenu().startGame(idS, this));
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }
}
