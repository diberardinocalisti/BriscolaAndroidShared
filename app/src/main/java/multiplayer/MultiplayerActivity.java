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

import Login.LoginActivity;
import game.danielesimone.briscola.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import firebase.FirebaseClass;
import gameEngine.Utility;

import static Login.LoginActivity.fbUID;

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

    public void createInputDialog(){
        Utility.inputDialog(this, this.getString(R.string.insertcode), (Object par) -> {
            String input = ((String) par).toUpperCase();

            joinRoomByCode(this, input, null,
                    () -> Utility.oneLineDialog(this, (String) this.getText(R.string.roomnotexisting), null),
                    () -> Utility.oneLineDialog(this, (String) this.getText(R.string.roomfull), null));
        });
    }

    public static void joinRoomByCode(AppCompatActivity context, String gameCode, Runnable onRoomAvailableCallback, Runnable onRoomNotExistingCallback, Runnable onRoomFullCallback){
        if(gameCode.isEmpty())
            return;

        FirebaseClass.getFbRefSpeicific(gameCode).addListenerForSingleValueEvent(new ValueEventListener() {
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
                        if(onRoomFullCallback != null)
                            onRoomFullCallback.run();
                    }else{
                        if(onRoomAvailableCallback != null)
                            onRoomAvailableCallback.run();

                        engineMultiplayer.accediGuest(context, gameCode);
                    }
                }else{
                    if(onRoomNotExistingCallback != null)
                        onRoomNotExistingCallback.run();
                }
            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
        });
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
