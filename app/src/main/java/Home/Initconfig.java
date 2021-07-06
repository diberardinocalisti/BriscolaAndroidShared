package Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

import java.util.Random;

import gameEngine.Carta;
import gameEngine.Game;
import gameEngine.Utility;

public class Initconfig extends AppCompatActivity {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Game.activity = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.choicecard);

        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.enableTopBar(this);

        LinearLayout gallery = this.findViewById(R.id.gallery);
        LayoutInflater inflater = LayoutInflater.from(this);

        String[] tipoArray = this.getResources().getStringArray(R.array.tipoCarte);
        String randomSeme = Game.semi[new Random().nextInt(Game.semi.length)];
        String randomTipo = tipoArray[new Random().nextInt(tipoArray.length)];
        int numCarta = 1;

        for (String tipoCarte : tipoArray) {
            Carta c = new Carta(numCarta, randomSeme, tipoCarte);

            View view = inflater.inflate(R.layout.singlecard, gallery, false);
            TextView cardTitle = view.findViewById(R.id.cardTitle);

            View cardImage = view.findViewById(R.id.cardImage);

            cardTitle.setText(c.getTipo());
            cardImage.setBackground(c.getImage());
            cardImage.setOnClickListener(v -> {
                SharedPref.setTipoCarte(c.getTipo());
                this.startActivity(new Intent(this, MainActivity.class));
            });

            gallery.addView(view);
        }

        findViewById(R.id.random).setOnClickListener(v -> {
            SharedPref.setTipoCarte(randomTipo);
            this.startActivity(new Intent(this, MainActivity.class));
        });
    }
}
