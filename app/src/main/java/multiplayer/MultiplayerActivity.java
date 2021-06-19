package multiplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

public class MultiplayerActivity extends AppCompatActivity {

    Button button[] = new Button[4];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        getSupportActionBar().hide();

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
                        Utility.createDialog(MultiplayerActivity.this,"Come sfidare i tuoi amici","Giocare con i tuoi amici non è mai stato così semplice!\nCrea una tua stanza privata e fornisci al tuo amico il codice che ti viene mostrato oppure entra in una stanza utilizzando il codice che ti ha fornito il tuo amico!");
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
