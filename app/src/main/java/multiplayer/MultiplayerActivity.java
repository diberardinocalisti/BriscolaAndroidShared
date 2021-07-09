package multiplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

public class MultiplayerActivity extends AppCompatActivity {

    Button button[] = new Button[4];

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_multiplayer);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        ActivityMultiplayerGame.onStop = false;
        ActivityMultiplayerGame.start = false;
        ActivityMultiplayerGame.initialManche = false;

        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);
            button[index].setOnClickListener(v -> {
                switch ((index+1)){
                    case 1:
                        engineMultiplayer.creaStanza(MultiplayerActivity.this);
                        engineMultiplayer.role = "HOST";
                        break;
                    case 2:
                        Utility.createInputDialogMultiplayer(MultiplayerActivity.this);
                        break;
                    case 3:
                        MultiplayerActivity.super.onBackPressed();
                        break;
                    case 4:
                        Utility.createDialog(MultiplayerActivity.this, button[index].getText().toString(),this.getString(R.string.howtoplaymptut));
                        break;
                }
            });
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

    /*@Override
    protected void onStop() {
        super.onStop();

        Toast.makeText(getApplicationContext(),"CHIUSA",Toast.LENGTH_LONG).show();
    }*/

}
