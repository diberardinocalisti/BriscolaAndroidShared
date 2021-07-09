package Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import gameEngine.Utility;


public class LoadingScreen extends AppCompatActivity {

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPref.setContext(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.loading_screen);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        new Handler().postDelayed(() -> {
            if(SharedPref.getTipoCarte().equals("null"))
                this.startActivity(new Intent(this, Initconfig.class));
            else
                this.startActivity(new Intent(this, MainActivity.class));

            this.finish();
       }, 2000);
    }
}
