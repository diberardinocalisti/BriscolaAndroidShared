package multiplayer.Game;

import android.bluetooth.BluetoothA2dp;
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
    private boolean stopApp = false;
    private String roleId, noteRoleId;
    private String host,enemy;
    private boolean onStop = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        roleId = (role == "HOST" ? "host" : "enemy");
        noteRoleId = (role == "HOST" ? "enemy" : "host");


        //@TODO viene prima stampato che il giocatore null si è unito alla partia
        //Se la stanza viene eliminata
        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals("host"))
                        host = String.valueOf(value);
                    else if(key.equals("enemy"))
                        enemy = String.valueOf(value);

                }

                System.out.println("Host --> " + host+"\nEnemy --> " + enemy);

                //L'host ha abbandonato
                if(host.equals("null") && !enemy.equals("null"))
                {
                    if(roleId.equals("host"))
                    {
                        Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Il tuo avversario ha abbandonato la partita.\nHai vinto a tavolino",Toast.LENGTH_LONG).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }
                }

                if(!host.equals("null") && enemy.equals("null"))
                {
                    if(roleId.equals("enemy"))
                    {
                        Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }else
                    {
                        Toast.makeText(getApplicationContext(),"Il tuo avversario ha abbandonato la partita.\nHai vinto a tavolino",Toast.LENGTH_LONG).show();
                        Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                    }
                }

                if(onStop)
                    FirebaseClass.deleteFieldFirebase(null, codiceStanza);

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

        FirebaseClass.editFieldFirebase(codiceStanza,roleId,"null");

        onStop = true;
    }
}
