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

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Login.loginClass;
import firebase.FirebaseClass;
import gameEngine.Utility;
import multiplayer.Game.ActivityMultiplayerGame;

public class MultiplayerActivity extends AppCompatActivity {
    Button[] button = new Button[4];

    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_multiplayer);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

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
                        createInputDialog();
                        break;
                    case 3:
                        MultiplayerActivity.super.onBackPressed();
                        break;
                    case 4:
                        Utility.createDialog(MultiplayerActivity.this, button[index].getText().toString(),this.getString(R.string.howtoplaymptut));
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
                            engineMultiplayer.codiceStanza = input;
                            engineMultiplayer.role = "NOTHOST";
                            ActivityGame.multiplayer = true;
                            FirebaseClass.editFieldFirebase(input,"enemy", loginClass.getFBNome());
                            Utility.goTo(MultiplayerActivity.this, ActivityMultiplayerGame.class);
                        }
                    }else{
                        Toast.makeText(MultiplayerActivity.this, MultiplayerActivity.this.getText(R.string.roomnotexisting), Toast.LENGTH_LONG).show();
                    }

                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                }

            });
        });
    }
}
