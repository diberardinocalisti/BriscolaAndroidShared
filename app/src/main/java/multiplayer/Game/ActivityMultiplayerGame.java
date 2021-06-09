package multiplayer.Game;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Utility;

import static multiplayer.engineMultiplayer.codiceStanza;
import static multiplayer.engineMultiplayer.role;

public class ActivityMultiplayerGame extends AppCompatActivity {

    private String onStopUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        Toast.makeText(ActivityMultiplayerGame.this,codiceStanza,Toast.LENGTH_LONG).show();

        //@TODO nella stanza di gioco controllare se la stanza viene eliminata
        //Se la stanza viene eliminata
        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                //La stanza non esiste più
                if(dataSnapshot.getChildrenCount() == 0)
                {
                    if(onStopUser != role)
                    {
                        Toast.makeText(getApplicationContext(),"L'avversario ha abbandonato la partita! Hai vinto a tavolino!",Toast.LENGTH_LONG).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @org.jetbrains.annotations.NotNull DatabaseError databaseError) {

            }


        });
    }


    //Un utente è uscito dal campo da gioco
    @Override
    protected void onStop() {
        super.onStop();

        //Elimino la stanza dal db

        FirebaseClass.deleteFieldFirebase(null,codiceStanza);
        onStopUser = role;
    }
}
