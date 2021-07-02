package Home;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.briscolav10.R;

import Login.loginClass;
import gameEngine.Game;
import gameEngine.Settings;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;

import static gameEngine.Game.activity;

public class MainActivity extends AppCompatActivity {
    View[] button = new View[5];
    ImageButton rank;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        rank = findViewById(R.id.rank);

        if(loginClass.isFacebookLoggedIn())
            rank.setVisibility(View.VISIBLE);
        else
            rank.setVisibility(View.INVISIBLE);

        ActivityMultiplayerGame.onStop = false;

        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);
            button[index].setOnClickListener(v -> new MainMenu().startGame(idS, this));
        }

        Game.terminata = true;
        SharedPref.setContext(this);
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
