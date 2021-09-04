package Home;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscola.R;

import java.util.Random;

import gameEngine.Carta;
import gameEngine.Game;
import gameEngine.SharedPref;
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

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.actionbar));
        Utility.ridimensionamento(this, findViewById(R.id.init_parent));

        LinearLayout gallery = this.findViewById(R.id.init_cardGallery);
        LayoutInflater inflater = LayoutInflater.from(this);

        String[] tipoArray = this.getResources().getStringArray(R.array.tipoCarte);
        String randomSeme = Game.semi[new Random().nextInt(Game.semi.length)];
        String randomTipo = tipoArray[new Random().nextInt(tipoArray.length)];
        int numCarta = 1;

        for (String tipoCarte : tipoArray) {
            Carta c = new Carta(numCarta, randomSeme, tipoCarte);

            View view = inflater.inflate(R.layout.singlecard, gallery, false);
            ViewGroup parentView = view.findViewById(R.id.singlecard_parent);
            TextView cardTitle = view.findViewById(R.id.singlecard_cardTitle);
            View cardImage = view.findViewById(R.id.singlecard_cardImage);

            cardTitle.setText(c.getTipo());
            cardImage.setBackground(c.getImage());
            cardImage.setOnClickListener(v -> {
                SharedPref.setTipoCarte(c.getTipo());
                this.startActivity(new Intent(this, MainActivity.class));
            });

            Utility.ridimensionamento(this, parentView);
            gallery.addView(view);
        }

        HorizontalScrollView cardScrollView = findViewById(R.id.init_cardScrollView);
        View randomBtn = findViewById(R.id.init_random);
        View scrollLeftBtn = findViewById(R.id.init_scrollLeft);
        View scrollRightBtn = findViewById(R.id.init_scrollRight);

        randomBtn.setOnClickListener(v -> {
            SharedPref.setTipoCarte(randomTipo);
            this.startActivity(new Intent(this, MainActivity.class));
        });

        scrollLeftBtn.setOnClickListener(v -> {
            cardScrollView.post(() -> cardScrollView.fullScroll(View.FOCUS_LEFT));
        });

        scrollRightBtn.setOnClickListener(v -> {
            cardScrollView.post(() -> cardScrollView.fullScroll(View.FOCUS_RIGHT));
        });
    }
}
