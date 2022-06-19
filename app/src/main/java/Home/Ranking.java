package Home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;

import UI.UiColor;
import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.Utility;
import multiplayer.FbUser;
import multiplayer.User;

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

        Utility.ridimensionamento(appCompatActivity, this.findViewById(R.id.ranking_parent));

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
        ScrollView scrollView = findViewById(R.id.ranking_scrollView);

        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            View view = scrollView.getChildAt(scrollView.getChildCount()-1);
            int difference = (view.getBottom()-(scrollView.getHeight()+scrollView.getScrollY()));
            boolean isAtBottom = difference == 0;

            if(isAtBottom)
                mostraUtenti();
        });
    }

    private void mostraUtentePrincipale(){
        if(!isLoggedIn(appCompatActivity))
            return;

        utentePrincipale.setPlacement(getPlacementById(utentePrincipale.getID()));
        mostraUtente(utentePrincipale, R.id.ranking_scrollViewLayoutPersonal, appCompatActivity);
    }

    private void ottieniUtenti(Runnable callback){
        ProgressBar progressBar = this.findViewById(R.id.ranking_loadingBar);
        progressBar.setVisibility(View.VISIBLE);

        if(!Utility.isNetworkAvailable(appCompatActivity))
            return;

        FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
            Iterable<DataSnapshot> result = task.getResult().getChildren();

            for(DataSnapshot d: result){
                if(isUser(d)){
                    String nomeUtente;
                    String idUtente = d.getKey();
                    boolean isSelf = false;

                    int vinte = d.getValue(User.class).getVinte();

                    if(isEmailUser(d)){
                        nomeUtente = d.getKey();
                        if(isUsernameLoggedIn(appCompatActivity))
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

                    if(nomeUtente.length() <= 1)
                        nomeUtente = appCompatActivity.getString(R.string.invalidusername);

                    RankingUtente utenteDaAggiungere =  new RankingUtente(nomeUtente, idUtente, vinte);
                    if(isSelf)
                        utentePrincipale = utenteDaAggiungere;

                    utentiTotali.add(utenteDaAggiungere);
                }
            }

            Collections.sort(utentiTotali, (user1, user2) -> user2.getVinte() - user1.getVinte());

            if(callback != null)
                callback.run();

            progressBar.setVisibility(View.INVISIBLE);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

            mostraUtente(utentiTotali.get(i), R.id.ranking_scrollViewLayout, appCompatActivity);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraUtente(RankingUtente utente, int parentLayout, AppCompatActivity appCompatActivity){
        LayoutInflater inflater = LayoutInflater.from(appCompatActivity);

        final LinearLayout gallery = this.findViewById(parentLayout);
        int layoutToAdd = R.layout.singlerankingplayer;

        View view = inflater.inflate(layoutToAdd, gallery, false);

        ViewGroup parentView = view.findViewById(R.id.singleranking_parent);
        ImageView playerIcon = view.findViewById(R.id.singleranking_playerIcon);
        TextView playerName = view.findViewById(R.id.singleranking_playerName);
        Button playerPlacement = view.findViewById(R.id.singleranking_playerPlacement);
        TextView nVittorie = view.findViewById(R.id.singleranking_nVittorie);

        playerName.setText(utente.getName());
        nVittorie.setText(String.valueOf(utente.getVinte()));
        playerPlacement.setText(String.valueOf(utente.getPlacement()));
        setImgProfile(appCompatActivity, utente.getID(), playerIcon);

        if(utente == utentePrincipale)
            playerName.setTextColor(UiColor.GREEN);

        Utility.ridimensionamento(appCompatActivity, parentView);
        gallery.addView(view);

        if(parentLayout == R.id.ranking_scrollViewLayout)
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

        private RankingUtente(String name, String ID, int vinte){
            this.name = name;
            this.ID = ID;
            this.vinte = vinte;
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
    }
}
