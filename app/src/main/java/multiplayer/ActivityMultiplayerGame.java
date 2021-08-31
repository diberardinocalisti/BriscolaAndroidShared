package multiplayer;

import android.content.Context;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscola.R;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Utility;

import static game.danielesimone.briscola.ActivityGame.leftGame;
import static gameEngine.Game.terminata;
import static multiplayer.engineMultiplayer.checkIfSomeoneLeft;
import static multiplayer.engineMultiplayer.codiceStanza;
import static multiplayer.engineMultiplayer.initEnemy;
import static multiplayer.engineMultiplayer.initHost;
import static multiplayer.engineMultiplayer.role;

public class ActivityMultiplayerGame extends AppCompatActivity {
    public static String roleId;
    public static boolean onStop = false;
    public static String mazzoOnline = new String();
    public static GameRoom snapshot;
    public static boolean initPartita = false;
    public static ValueEventListener valueEventListener;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        onStop = initPartita = false;

        roleId = (role.equals("HOST") ? "host" : "enemy");
        
        Game.initialize(this);

        valueEventListener = new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot.getValue(GameRoom.class);

                if(!onStop){
                    try{
                        checkIfSomeoneLeft();
                    }catch(Exception e){
                        return;
                    }
                }

                if(!onStop){
                    if(!initPartita){
                        if(roleId.equals("host")) {
                            initHost();
                        }else if(!snapshot.getMazzo().equals("null")) {
                            initEnemy();
                        }
                    }else{
                        engineMultiplayer.updateChat();
                        engineMultiplayer.checkIfCartaGiocata();
                    }
                }
            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
        };

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(valueEventListener);
    }

    @Override
    protected void onStop() {
        super.onStop();

        leftGame = true;
        onStop = true;
        
        FirebaseClass.editFieldFirebase(codiceStanza, roleId, "null");
        FirebaseClass.deleteFieldFirebase(null, codiceStanza);

        if(!terminata)
            Utility.mainMenu(this);
    }

    @Override
    protected void onDestroy(){
        super.onStop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Utility.oneLineDialog(this, this.getString(R.string.confirmleavegame), () -> {
            leftGame = true;
            Utility.mainMenu(this);
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

    @Override
    protected void onResume() {
        super.onResume();

        if(leftGame)
            Utility.mainMenu(this);
    }
}
