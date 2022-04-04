package Home;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.jetbrains.annotations.TestOnly;

import java.util.ArrayList;

import Login.loginClass;
import UI.BottomDialog;
import game.danielesimone.briscola.R;
import gameEngine.Utility;

import static Login.loginClass.getFullName;
import static Login.loginClass.getId;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.setImgProfile;

public class Friends extends Dialog{
    private AppCompatActivity context;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public Friends(AppCompatActivity context){
        super(context);

        this.context = context;
        this.setContentView(R.layout.friends_dialog);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utility.ridimensionamento(context, this.findViewById(R.id.friend_parent));

        inizializza();

        this.create();
        this.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void inizializza(){
        TextView nomeProfiloTextView = this.findViewById(R.id.friend_nome);
        ImageView immagineProfilo = this.findViewById(R.id.friend_profilePicture);

        String nomeProfiloString = getFullName();
        nomeProfiloTextView.setText(nomeProfiloString);

        loginClass.setImgProfile(context, getId(), immagineProfilo);

        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
        aggiungiUtente(loginClass.getFullName(), getId());
    }

    // Non sono riuscito a capire bene il funzionamento delle RycyclerView via codice, per ora sto utilizzando il solito metodo
    // che trovo molto più semplice ma se vuoi puoi sostituirlo con il tuo funzionante;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void aggiungiUtente(String playerName, String playerId){
        LayoutInflater inflater = LayoutInflater.from(context);

        final LinearLayout gallery = this.findViewById(R.id.friend_linearLayout);
        int layoutToAdd = R.layout.singlefriend;

        View view = inflater.inflate(layoutToAdd, gallery, false);

        ViewGroup parentView = view.findViewById(R.id.singlefriend_parent);
        TextView nameView = view.findViewById(R.id.singlefriend_playerName);
        TextView vittorieView = view.findViewById(R.id.singlefriend_nVittorie);
        ImageView playerImage = view.findViewById(R.id.singlefriend_playerIcon);
        ImageView moreInfo = view.findViewById(R.id.singlefriend_playerInfo);

        nameView.setText(playerName);
        setImgProfile(context, playerId, playerImage);

/*      @Todo:
          Bisogna mostrare il numero di vittorie, probabilmente l'hai già
          fatto nel tuo codice quindi basta semplicemente incollarlo qui;
          */

        moreInfo.setOnClickListener(v -> {
            ArrayList<Button> buttonsToAdd = new ArrayList<>();
            Button button;

            button = new Button(context);
            button.setText(context.getString(R.string.visitprofile));
            button.setOnClickListener(v1 -> {
                // Ad ora non è possibile ricavare l'URL dell'account Facebook perché l'app non è stata verificata,
                // Per ora cerco semplicemente l'username su Facebook e mostro i risultati;
                final String link = "https://www.facebook.com/search/top/?q=%s";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(link, playerName)));
                context.startActivity(browserIntent);
            });
            buttonsToAdd.add(button);

            button = new Button(context);
            button.setText(context.getString(R.string.goback));
            button.setOnClickListener(null);
            buttonsToAdd.add(button);

            new BottomDialog(playerName, buttonsToAdd, context);
        });

        Utility.ridimensionamento(context, parentView);
        gallery.addView(view);
    }
}
