package game.danielesimone.briscolav10;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import Login.LoginActivity;
import Login.loginClass;
import game.danielesimone.briscolav10.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Utility;

import static Login.LoginActivity.fbUID;
import static gameEngine.Game.activity;
import static gameEngine.Game.maxPunti;
import static gameEngine.Game.nGiocatori;

public class postPartita extends AppCompatActivity {

    public String background, stato;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.postpartita);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        Bundle extras = getIntent().getExtras();

        View postpartita = findViewById(R.id.parent);
        TextView esito = findViewById(R.id.esito);
        TextView punti = findViewById(R.id.nPunti);
        Button restart = findViewById(R.id.restart);
        Button exit = findViewById(R.id.exit);

        int punteggio = extras.getInt("punteggio");
        String[] daMostrare = extras.getStringArray("carte");


        if(punteggio < maxPunti/nGiocatori) {
            partitaPersa();
        }else if(punteggio == maxPunti/nGiocatori) {
            pareggio();
        }else{
            partitaVinta();
        }

        // Aggiorna lo sfondo in base all'esito della partita (blu/rosso/grigio);
        int resID = activity.getResources().getIdentifier(background, "drawable", activity.getPackageName());

        final int sdk = android.os.Build.VERSION.SDK_INT;
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            postpartita.setBackgroundDrawable(ContextCompat.getDrawable(activity, resID) );
        }else{
            postpartita.setBackground(ContextCompat.getDrawable(activity, resID));
        }

        // Scrive lo stato della partita (vittoria/sconfitta/pareggio);
        esito.setText(stato);

        // Scrive i punti ottenuti;
        punti.setText("+" + punteggio);

        for(int i = 0; i < daMostrare.length; i++){
            Carta carta = Engine.getCartaFromName(daMostrare[i]);
            String idS = "carta" + (i+1);

            int id = activity.getResources().getIdentifier(idS, "id", activity.getPackageName());
            ImageView bottone = findViewById(id);

            bottone.setBackground(Carta.getVuoto());
            new Thread(() -> {
                try {
                    Thread.sleep(Game.viewAnimDuration);
                    activity.runOnUiThread(() -> Engine.flipAnim(bottone, carta.getImage(), false, null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }

        restart.setOnClickListener(v -> {
            Intent i = new Intent(this, ActivityGame.class);
            i.putExtra("multiplayer", ActivityGame.multiplayer);
            this.startActivity(i);
        });

        exit.setOnClickListener(v -> Utility.goTo(this, MainActivity.class));

        if(ActivityGame.multiplayer)
            restart.setVisibility(View.INVISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }


    public void partitaPersa()
    {
        background = "sconfitta";
        stato = this.getResources().getString(R.string.lost);

        if(!loginClass.isFacebookLoggedIn())
            return;

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                long perse;
                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("perse"))
                    {
                        perse = (long) d.getValue();
                        FirebaseClass.editFieldFirebase(fbUID,"perse",perse+1);
                        break;
                    }
                }
            }
        });
    }

    public void pareggio()
    {
        background = "pareggio";
        stato = this.getResources().getString(R.string.tie);
    }

    public void partitaVinta()
    {
        background = "vittoria";
        stato = this.getResources().getString(R.string.win);

        if(!loginClass.isFacebookLoggedIn())
            return;

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                long vinte;

                for(DataSnapshot d: task.getResult().getChildren()){
                    if(d.getKey().equals("vinte")){
                        vinte = (long) d.getValue();
                        FirebaseClass.editFieldFirebase(fbUID,"vinte",vinte+1);
                        break;
                    }
                }
            }
        });


    }
}