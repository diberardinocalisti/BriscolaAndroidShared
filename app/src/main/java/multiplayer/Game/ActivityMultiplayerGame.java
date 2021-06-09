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
    private String roleId;
    private boolean tavolino = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);

        roleId = (role == "HOST" ? "host" : "enemy");


        //@TODO viene prima stampato che il giocatore null si è unito alla partia
        //Se la stanza viene eliminata
        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals(roleId))
                    {
                        if(value == "null")
                        {
                            Toast.makeText(getApplicationContext(),"Hai abbandonato la partita!",Toast.LENGTH_SHORT).show();
                            Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                        }else
                        {
                            Toast.makeText(getApplicationContext(),"Il tuo avversario ha abbandonato la partita.\nHai vinto a tavolino",Toast.LENGTH_LONG).show();
                            Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                        }
                    }


                }
                /*for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    String key = d.getKey();
                    Object value = d.getValue();

                    if(key.equals("enemy"))
                    {
                        //è entrato l'avverrsario nella stanza
                        if(value == "null")
                        {
                            if(roleId != "enemy")
                            {
                                Toast.makeText(getApplicationContext(),"L'avversario ha abbandonato la partita!\n Hai vinto a tavolino!",Toast.LENGTH_LONG).show();
                                Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                                tavolino = true;
                            }
                        }
                    }

                    if(key.equals("host"))
                    {
                        //è entrato l'avverrsario nella stanza
                        if(value == "null")
                        {
                            if(roleId != "host")
                            {
                                Toast.makeText(getApplicationContext(),"L'avversario ha abbandonato la partita!\n Hai vinto a tavolino!",Toast.LENGTH_LONG).show();
                                Utility.goTo(ActivityMultiplayerGame.this,MainActivity.class);
                                tavolino = true;
                            }
                        }
                    }
                }*/
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

        /*if(!tavolino)
        {


            FirebaseClass.editFieldFirebase(codiceStanza,roleId,"null");
        }*/

        FirebaseClass.editFieldFirebase(codiceStanza,roleId,"null");

        //FirebaseClass.deleteFieldFirebase(codiceStanza,roleId);
    }
}
