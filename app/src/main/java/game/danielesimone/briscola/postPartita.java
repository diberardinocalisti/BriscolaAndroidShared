package game.danielesimone.briscola;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import Home.Storico;
import Login.loginClass;

import com.google.firebase.database.DataSnapshot;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Utility;
import multiplayer.GiocatoreMP;
import multiplayer.MultiplayerActivity;
import multiplayer.engineMultiplayer;

import static Login.LoginActivity.fbUID;
import static gameEngine.Engine.ordinaMazzo;
import static gameEngine.Game.activity;
import static gameEngine.Game.maxPunti;
import static gameEngine.Game.nGiocatori;
import static gameEngine.Game.timer;
import static java.lang.String.*;
import static multiplayer.engineMultiplayer.codiceStanza;

public class postPartita extends AppCompatActivity {
    public String background, stato;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getSupportActionBar().hide();

        setContentView(R.layout.postpartita);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        esitoPartita();
        durataPartita();
        mostraMazzo();
        setButtons();
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void esitoPartita(){
        int punteggio = Game.user.getPunteggioCarte();

        // Aggiunge la partita allo storico;
        Storico.addPartita(this, new Storico.Partita(punteggio, Game.opp.getNome(), Game.opp.getId(), Utility.getTimeString()));

        boolean isPersa = punteggio < maxPunti/nGiocatori;
        boolean isVittoria = punteggio > maxPunti/nGiocatori;
        boolean isPareggio = punteggio == maxPunti/nGiocatori;

        if(isPersa){
            partitaPersa();
        }else if(isVittoria){
            partitaVinta();
        }else if(isPareggio){
            pareggio();
        }

        View postpartita = findViewById(R.id.parent);
        TextView esito = findViewById(R.id.esito);
        TextView punti = findViewById(R.id.nPunti);

        // Aggiorna lo sfondo in base all'esito della partita (blu/rosso/grigio);
        int resID = this.getResources().getIdentifier(background, "drawable", this.getPackageName());
        postpartita.setBackground(ContextCompat.getDrawable(this, resID));

        // Scrive lo stato della partita (vittoria/sconfitta/pareggio);
        esito.setText(stato);

        // Scrive i punti ottenuti;
        punti.setText(format("+%d", punteggio));
    }

    private void durataPartita(){
        TextView durata = findViewById(R.id.matchTime);

        // Stoppa il timer e scrive la durata della partita;
        timer.stop();
        final String matchTimeString = getString(R.string.matchtime);
        durata.setText(matchTimeString.replace("{time}", timer.getElapsedTimeString()));
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void mostraMazzo(){
        ordinaMazzo(Game.user.getPrese());
        Carta[] cartePrese = Game.user.getPrese().toArray(new Carta[]{});

        LinearLayout gallery = this.findViewById(R.id.cardsCollected);
        LayoutInflater inflater = LayoutInflater.from(this);

        for(Carta carta : cartePrese){
            View view = inflater.inflate(R.layout.singlecard, gallery, false);
            TextView cardTitle = view.findViewById(R.id.cardTitle);
            View cardImage = view.findViewById(R.id.cardImage);

            Integer valoreCarta = carta.getValore();
            String cardDescription = activity.getString(R.string.pointswvalue).replace("{points}", valoreCarta.toString());
            cardTitle.setText(cardDescription);
            cardImage.setBackground(Carta.getVuoto());

            new Thread(() -> {
                try {
                    Thread.sleep(Game.viewAnimDuration);
                    this.runOnUiThread(() -> Engine.flipAnim(cardImage, carta.getImage(), false, null));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();

            gallery.addView(view);
        }
    }

    private void setButtons(){
        View.OnClickListener restartAction, closeAction;

        HorizontalScrollView cardsScrollView = findViewById(R.id.cardsCollectedScrollView);
        View scrollLeftBtn = findViewById(R.id.scrollCardLeft);
        View scrollRightBtn = findViewById(R.id.scrollCardRight);
        View restartBtn = findViewById(R.id.restart);
        View exitBtn = findViewById(R.id.exit);

        scrollLeftBtn.setOnClickListener(v -> {
            cardsScrollView.post(() -> cardsScrollView.fullScroll(View.FOCUS_LEFT));
        });

        scrollRightBtn.setOnClickListener(v -> {
            cardsScrollView.post(() -> cardsScrollView.fullScroll(View.FOCUS_RIGHT));
        });

        if(!ActivityGame.multiplayer){
            restartAction = v -> {
                Intent i = new Intent(this, ActivityGame.class);
                i.putExtra("multiplayer", ActivityGame.multiplayer);
                this.startActivity(i);
            };
        }else{
            restartAction = v -> {
                GiocatoreMP giocatore = (GiocatoreMP) Game.user;
                if(giocatore.getRuolo().equals("host")){
                    engineMultiplayer.accediHost(this, codiceStanza);
                }else{
                    MultiplayerActivity.joinRoomByCode(this, engineMultiplayer.codiceStanza,
                            // On complete callback;
                            () -> Utility.goTo(this, MultiplayerActivity.class),
                            // On room not existing callback;
                            () -> Utility.oneLineDialog(this, (String) this.getText(R.string.waithost), null),
                            // On room full callback;
                            () -> Utility.oneLineDialog(this, (String) this.getText(R.string.roomfull), null));
                }
            };
        }

        closeAction = v -> {
            Intent i = new Intent(this, MainActivity.class);
            final int percentageShowDialog = 50;
            Utility.runnablePercentage(percentageShowDialog, () -> i.putExtra("askRateApp", true));
            this.startActivity(i);
        };

        restartBtn.setOnClickListener(restartAction);
        exitBtn.setOnClickListener(closeAction);
    }

    public void partitaPersa(){
        background = "sconfitta";
        stato = this.getString(R.string.lost);

        if(!loginClass.isLoggedIn())
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

    public void partitaVinta(){
        background = "vittoria";
        stato = this.getString(R.string.win);

        if(!loginClass.isLoggedIn())
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

    public void pareggio(){
        background = "pareggio";
        stato = this.getString(R.string.tie);
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
