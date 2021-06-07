package multiplayer;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import gameEngine.Utility;

public class MultiplayerActivity extends AppCompatActivity {

    Button button[] = new Button[4];

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer);

        for(int i = 0; i < button.length; i++){
            int index = i;

            String idS = "button" + (index + 1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            button[index] = findViewById(id);
            button[index].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch ((index+1))
                    {
                        case 1:
                            engineMultiplayer.creaStanza(MultiplayerActivity.this);
                            break;
                        case 2:
                            //Unisciti ad una stanza
                            break;
                        case 3:
                            MultiplayerActivity.super.onBackPressed();
                            break;
                        case 4:
                            Utility.createDialog(MultiplayerActivity.this,"Come puoi giocare con i tuoi amici?","Giocare con i tuoi amici non è mai stato così semplice!\nCrea una tua stanza privata e fornisci al tuo amico il codice che ti viene mostrato, oppure entra in una stanza utilizzando il codice che ti ha fornito il tuo amico!");
                            break;
                    }

                }
            });
        }

    }

}
