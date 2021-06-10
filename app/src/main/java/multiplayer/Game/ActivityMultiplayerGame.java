package multiplayer.Game;

import android.bluetooth.BluetoothA2dp;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Utility;
import gameEngine.onClick;

import static multiplayer.engineMultiplayer.*;

public class ActivityMultiplayerGame extends AppCompatActivity {

    public static boolean start = false;
    private String onStopUser;
    private boolean stopApp = false;
    private String roleId, noteRoleId;
    private String host,enemy;
    public static boolean onStop = false;

    //I primi 3 bottoni sono dell'avversario
    private Button carte[] = new Button[6];


    //button9 carta dell'avversario button10 mia carta

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campo_da_gioco);


        for(int i = 0; i < carte.length; i++){
            String idS = "button" + (i+1);
            int id = getResources().getIdentifier(idS, "id", getPackageName());

            carte[i] = findViewById(id);

            int finalI = i;
            carte[i].setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.N)
                @Override
                public void onClick(View v) {
                    if(finalI < 3)
                    {
                        //Ha premuto l'avversario";
                        Carta premuta = Engine.getCartaFromButton((Button) v);
                        Toast.makeText(ActivityMultiplayerGame.this,"carta --> " + premuta.getNome(),Toast.LENGTH_SHORT).show();
                    }else
                    {
                        //Ho premuto io
                    }

                }
            });
        }

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

        if(role == "HOST" && !start)
        {
            startMultiplayerGame(ActivityMultiplayerGame.this);
            ActivityMultiplayerGame.start = true;
            //Toast.makeText(getApplicationContext(),"Ora si dovrebbe creare il mazzo",Toast.LENGTH_LONG).show();
        }



        /*String[] daDare = new String[3];
        try {
            daDare = getInitialCards();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        for(String s: daDare)
            System.out.println("s --> " + s);*/


    }


    //Un utente è uscito dal campo da gioco
    @Override
    protected void onStop() {
        super.onStop();

        FirebaseClass.editFieldFirebase(codiceStanza,roleId,"null");

        onStop = true;
    }
}
