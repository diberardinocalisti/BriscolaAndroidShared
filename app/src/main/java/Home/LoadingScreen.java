package Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;

import Login.LoginActivity;
import multiplayer.MultiplayerActivity;


public class LoadingScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        getSupportActionBar().hide();

        setContentView(R.layout.loading_screen);

        new Handler().postDelayed(() -> {
            LoadingScreen.this.startActivity(new Intent(this, MainActivity.class));
            this.finish();
       }, 1750);
    }
}