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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.MobileAds;

import game.danielesimone.briscola.R;

import gameEngine.Utility;

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
}
