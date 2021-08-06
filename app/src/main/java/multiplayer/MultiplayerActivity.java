package multiplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import firebase.FirebaseClass;
import gameEngine.Utility;

public class MultiplayerActivity extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_multiplayer);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        ActivityMultiplayerGame.onStop = false;

        Button createRoom = findViewById(R.id.createroom);
        Button joinRoom = findViewById(R.id.accessviacode);
        Button roomList = findViewById(R.id.roomlist);
        Button goBack = findViewById(R.id.goback);

        createRoom.setOnClickListener(v -> engineMultiplayer.creaStanza(MultiplayerActivity.this));
        joinRoom.setOnClickListener(v -> createInputDialog());
        roomList.setOnClickListener(v -> Utility.goTo(this, RoomList.class));
        goBack.setOnClickListener(v -> MultiplayerActivity.super.onBackPressed());
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    public void createInputDialog(){
        Utility.inputDialog(this, this.getString(R.string.insertcode), (Object par) -> {
            String input = ((String) par).toUpperCase();

            FirebaseClass.getFbRefSpeicific(input).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                    String host = "null";
                    String enemy = "null";

                    if(dataSnapshot.exists()){
                        for(DataSnapshot d : dataSnapshot.getChildren()){
                            String key = d.getKey();
                            Object value = d.getValue();

                            if(key.equals("host"))
                                host = String.valueOf(value);
                            if(key.equals("enemy"))
                                enemy = String.valueOf(value);
                        }

                        if(!host.equals("null") && !enemy.equals("null")){
                            Toast.makeText(MultiplayerActivity.this.getApplicationContext(), MultiplayerActivity.this.getText(R.string.roomfull), Toast.LENGTH_LONG).show();
                        }else{
                            engineMultiplayer.accediGuest(MultiplayerActivity.this, input);
                        }
                    }else{
                        Toast.makeText(MultiplayerActivity.this, MultiplayerActivity.this.getText(R.string.roomnotexisting), Toast.LENGTH_LONG).show();
                    }
                }

                @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
            });
        });
    }
}
