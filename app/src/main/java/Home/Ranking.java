package Home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.ListIterator;
import java.util.function.Predicate;

import Login.LoginActivity;
import UI.UiColor;
import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.SharedPref;
import multiplayer.FbUser;
import multiplayer.User;

import static Login.LoginActivity.fbUID;
import static Login.loginClass.getFBUserId;
import static Login.loginClass.getUsernameId;
import static Login.loginClass.isEmailUser;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.isLoggedIn;
import static Login.loginClass.isUser;
import static Login.loginClass.isUsernameLoggedIn;
import static Login.loginClass.setImgProfile;
import static java.lang.String.format;

public class Ranking extends Dialog {
    private AppCompatActivity appCompatActivity;
    private static RankingUtente utentePrincipale;
    private ArrayList<RankingUtente> utentiMostrati, utentiTotali;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Ranking(AppCompatActivity appCompatActivity){
        super(appCompatActivity);
        this.setContentView(R.layout.ranking_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        inizializza(appCompatActivity);
        setListeners();
        ottieniUtenti(() -> {
            mostraUtenti();
            mostraUtentePrincipale();
        });

        this.create();
        this.show();
    }

    private void inizializza(AppCompatActivity appCompatActivity){
        this.appCompatActivity = appCompatActivity;
        this.utentiMostrati = new ArrayList<>();
        this.utentiTotali = new ArrayList<>();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setListeners(){
        //Utility.showAd(this);

        ScrollView scrollView = findViewById(R.id.scrollViewRanking);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = (View) scrollView.getChildAt(scrollView.getChildCount()-1);
            int difference = (view.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));
            boolean isAtBottom = difference == 0;

            if(isAtBottom)
                mostraUtenti();
        });
    }

    private void mostraUtentePrincipale(){
        if(isLoggedIn()){
            utentePrincipale.setPlacement(getPlacementById(utentePrincipale.getID()));
            mostraUtente(utentePrincipale, R.id.scrollViewLayoutPersonalRanking, appCompatActivity);
            mostraUtente(utentePrincipale, R.id.scrollViewLayoutRanking, appCompatActivity);
        }
    }

    private void ottieniUtenti(Runnable callback){
        ProgressBar progressBar = this.findViewById(R.id.loadingBarRanking);
        progressBar.setVisibility(View.VISIBLE);

        FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
            Iterable<DataSnapshot> result = task.getResult().getChildren();

            for(DataSnapshot d: result){
                if(isUser(d)){
                    String nomeUtente;
                    String idUtente = d.getKey();
                    boolean isSelf = false;

                    if(isEmailUser(d)){
                        nomeUtente = d.getKey();
                        if(isUsernameLoggedIn())
                            if(getUsernameId().equals(idUtente))
                                isSelf = true;
                    }else{
                        String nome = d.getValue(FbUser.class).getNome();
                        String cognome = d.getValue(FbUser.class).getCognome();
                        nomeUtente = String.format("%s %s", nome, cognome);

                        if(isFacebookLoggedIn())
                            if(getFBUserId().equals(idUtente))
                                isSelf = true;
                    }

                    int vinte = d.getValue(User.class).getVinte();

                    if(nomeUtente.length() <= 1)
                        nomeUtente = appCompatActivity.getString(R.string.invalidusername);

                    utentiTotali.add(new RankingUtente(nomeUtente, idUtente, vinte, isSelf));
                }
            }

            Collections.sort(utentiTotali, (user1, user2) -> user2.getVinte() - user1.getVinte());

            if(callback != null)
                callback.run();

            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    private void mostraUtenti(){
        final int N_UTENTI = 5, N_MAX_UTENTI = 50;

        if(utentiMostrati.size() == utentiTotali.size())
            return;

        int startingIndex = utentiMostrati.size();

        for(int i = startingIndex; i < startingIndex + N_UTENTI; i++){
            if(i == utentiTotali.size() || i == N_MAX_UTENTI)
                return;

            RankingUtente utenteDaMostrare = utentiTotali.get(i);
            utenteDaMostrare.setPlacement(i);

            mostraUtente(utentiTotali.get(i), R.id.scrollViewLayoutRanking, appCompatActivity);
        }
    }

    private void mostraUtente(RankingUtente utente, int parentLayout, AppCompatActivity appCompatActivity){
        LayoutInflater inflater = LayoutInflater.from(appCompatActivity);

        final LinearLayout gallery = this.findViewById(parentLayout);
        int layoutToAdd = R.layout.singlerankingplayer;

        View view = inflater.inflate(layoutToAdd, gallery, false);

        ImageView playerIcon = view.findViewById(R.id.ranking_playerIcon);
        TextView playerName = view.findViewById(R.id.ranking_playerName);
        Button playerPlacement = view.findViewById(R.id.ranking_playerPlacement);
        TextView nVittorie = view.findViewById(R.id.ranking_nVittorie);

        playerName.setText(utente.getName());
        nVittorie.setText(String.valueOf(utente.getVinte()));
        playerPlacement.setText(String.valueOf(utente.getPlacement()));
        setImgProfile(appCompatActivity, utente.getID(), playerIcon);

        if(utente.isSelf()) {
            playerName.setTextColor(UiColor.GREEN);
        }

        gallery.addView(view);

        utentiMostrati.add(utente);
    }

    private int getPlacementById(String ID){
        for(int i = 0; i < utentiTotali.size(); i++)
            if(utentiTotali.get(i).getID().equals(ID))
                return i;

        return -1;
    }

    private static class RankingUtente{
        private final String name;
        private final String ID;
        private final int vinte;
        private int placement;
        private boolean isSelf;

        private RankingUtente(String name, String ID, int vinte, boolean isSelf){
            this.name = name;
            this.ID = ID;
            this.vinte = vinte;
            this.isSelf = isSelf;

            if(isSelf)
                Ranking.utentePrincipale = this;
        }

        public String getName() {
            return name;
        }

        public String getID() {
            return ID;
        }

        public int getVinte() {
            return vinte;
        }

        public void setPlacement(int placement){
            this.placement = placement + 1;
        }

        public int getPlacement(){
            return placement;
        }

        public boolean isSelf(){
            return isSelf;
        }
    }
}
