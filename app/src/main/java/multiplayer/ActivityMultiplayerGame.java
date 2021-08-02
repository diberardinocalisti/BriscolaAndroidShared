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

import com.example.briscolav10.R;
import com.google.android.gms.ads.AdView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import firebase.FirebaseClass;
import gameEngine.Game;
import gameEngine.Utility;

import static gameEngine.Game.terminata;
import static multiplayer.engineMultiplayer.checkIfSomeoneLeft;
import static multiplayer.engineMultiplayer.codiceStanza;
import static multiplayer.engineMultiplayer.initEnemy;
import static multiplayer.engineMultiplayer.initHost;
import static multiplayer.engineMultiplayer.role;

public class ActivityMultiplayerGame extends AppCompatActivity {
    public static String roleId;
    public static boolean onStop = false;
    public static String mazzoOnline = "";
    public static GameRoom snapshot;
    public static boolean distribuisci = false;
    private AdView mAdView;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.campo_da_gioco);

        /*MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);*/

        onStop = distribuisci = false;

        roleId = (role.equals("HOST") ? "host" : "enemy");

        Game.canPlay = roleId.equals("host");

        Game.initialize(this);

        FirebaseClass.getFbRefSpeicific(codiceStanza).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                snapshot = dataSnapshot.getValue(GameRoom.class);

                try{
                    checkIfSomeoneLeft();
                }catch(Exception e){
                    return;
                }

                if(!onStop){
                    if(!distribuisci){
                        if(roleId.equals("host"))
                            initHost();
                        else if(!snapshot.getMazzo().equals("null"))
                            initEnemy();
                    }else{
                        engineMultiplayer.updateChat();
                        engineMultiplayer.checkIfCartaGiocata();
                    }
                }

                if(onStop) {
                    FirebaseClass.deleteFieldFirebase(null, codiceStanza);
                    ActivityMultiplayerGame.this.finish();
                }
            }

            @Override public void onCancelled(@NonNull @NotNull DatabaseError databaseError){}
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

        if(terminata) {
            FirebaseClass.deleteFieldFirebase(null, codiceStanza);
        }else{
            FirebaseClass.editFieldFirebase(codiceStanza, roleId, "null");
            onStop = true;
        }
    }

    @Override
    protected void onDestroy(){
        super.onStop();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Utility.oneLineDialog(this, this.getString(R.string.confirmleavegame), ActivityMultiplayerGame.super::onBackPressed);
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
