package Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import Login.LoginActivity;
import Login.loginClass;
import firebase.FirebaseClass;
import game.danielesimone.briscola.R;

import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.FbUser;
import multiplayer.User;

import static Login.LoginActivity.fbUID;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.isLoggedIn;
import static Login.loginClass.isUsernameLoggedIn;

public class LoadingScreen extends AppCompatActivity {
    public static boolean gameRunning = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final int durataCaricamento = 2000;

        super.onCreate(savedInstanceState);

        SharedPref.setContext(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.loading_screen);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        Class destination = SharedPref.getTipoCarte().equals("null") ? Initconfig.class : MainActivity.class;
        MobileAds.initialize(this, initializationStatus -> {});

        if(isLoggedIn(this)){
            if(isFacebookLoggedIn()) {
                AccessToken accessToken = AccessToken.getCurrentAccessToken();
                fbUID = accessToken.getUserId();
            }else if(isUsernameLoggedIn()){
                fbUID = SharedPref.getUsername();
                loginClass.updateEmail();
                checkIfAccountExists();
            }
        }


        if(!gameRunning){
            new Handler().postDelayed(() -> {
                findViewById(R.id.parent).setOnClickListener(v -> {
                    LoadingScreen.this.startActivity(new Intent(LoadingScreen.this, destination));
                    LoadingScreen.this.finish();
                });

                ProgressBar progressBar = findViewById(R.id.progressBar);
                progressBar.setVisibility(View.INVISIBLE);

                TextView centerTextView = findViewById(R.id.textView);
                centerTextView.setText(getString(R.string.touchtostart));
            }, durataCaricamento);
        }else{
            LoadingScreen.this.startActivity(new Intent(LoadingScreen.this, destination));
        }
    }

    protected void checkIfAccountExists(){
        if (!Utility.isNetworkAvailable(this)) {
            LoginActivity.doLogout(this);
            return;
        }

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if (!task.isSuccessful() || task.isCanceled() || task.getResult().getValue() == null) {
                LoginActivity.doLogout(this);
            }
        });
    }
}
