package Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.login.widget.LoginButton;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAd;
import com.google.android.gms.ads.rewardedinterstitial.RewardedInterstitialAdLoadCallback;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import org.w3c.dom.Text;

import java.util.ArrayList;

import UI.BottomDialog;
import firebase.FirebaseClass;
import game.danielesimone.briscola.GameActivity;
import gameEngine.ActivityGame;
import game.danielesimone.briscola.R;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;
import multiplayer.engineMultiplayer;
import okhttp3.internal.Util;
import pl.droidsonroids.gif.GifImageView;

import static Home.LoadingScreen.gameRunning;
import static Login.LoginActivity.fbUID;
import static Login.loginClass.*;

public class MainActivity extends GameActivity implements OnUserEarnedRewardListener{
    private final static int GIFT_COINS = 10;
    private RewardedInterstitialAd rewardedInterstitialAd;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPref.setContext(this);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        gameRunning = true;

        ActivityMultiplayerGame.onStop = false;
        Game.terminata = true;

        setListeners();
        showUpdateNotes();

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            boolean showRateApp = extras.getBoolean("askRateApp");
            if(showRateApp)
                rateApp();
        }

        if(isLoggedIn(this))
            initializeAd();
    }

    protected void updateCoins(){
        loginClass.fetchAndUpdateCoins(this, () -> {
            // La callback viene eseguita quando le Sharedprefs vengono aggiorante;
            TextView coinView = this.findViewById(R.id.coin);
            coinView.setText(String.valueOf(SharedPref.getCoin()));
        });
    }

    protected void showUpdateNotes(){
        if(!SharedPref.getLatestUpdate().equals(Game.GAME_VERSION)){
            SharedPref.setLatestUpdate(Game.GAME_VERSION);
            Utility.createDialog(this, this.getString(R.string.appupdated), this.getString(R.string.latestupdate));
        }
    }

    protected void rateApp(){
        Utility.oneLineDialog(this, this.getString(R.string.rateapptitle), () -> {
            final String link = "https://play.google.com/store/apps/details?id=%s";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format(link, getPackageName())));
            startActivity(browserIntent);
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void setListeners(){
        Button singleplayer = findViewById(R.id.singleplayer);
        Button multiplayer = findViewById(R.id.multiplayer);
        Button mioProfilo = findViewById(R.id.mioprofilo);
        Button closeGame = findViewById(R.id.closegame);
        Button shop = findViewById(R.id.shop);
        ImageButton info = findViewById(R.id.info);
        ImageButton contact = findViewById(R.id.contact);
        ImageButton history = findViewById(R.id.history);
        ImageButton ranking = findViewById(R.id.ranking);
        ImageView coin = findViewById(R.id.coinIcon);
        GifImageView gift = findViewById(R.id.giftIcon);

        singleplayer.setOnClickListener(v -> {
            Intent intent = new Intent(this, ActivityGame.class);
            intent.putExtra("multiplayer",false);
            this.startActivity(intent);
        });

        multiplayer.setOnClickListener(v -> {
            if(isLoggedIn(this)){
                Intent i = new Intent(this, MultiplayerActivity.class);
                this.startActivity(i);
            }else{
                Intent i = new Intent(this, LoginActivity.class);
                this.startActivity(i);
            }
        });

        info.setOnClickListener(v -> {
            String title = this.getString(R.string.intro);
            String msg = this.getString(R.string.howtoplay);
            Utility.createDialog(this, title, msg);
        });

        mioProfilo.setOnClickListener(v -> {
            Intent in = new Intent(this, LoginActivity.class);
            this.startActivity(in);
        });

        contact.setOnClickListener(v -> {
            String title = this.getString(R.string.contactus);
            String msg = this.getString(R.string.contactustxt);
            Utility.createDialog(this, title, msg);
        });

        ranking.setOnClickListener(v -> new Ranking(this));
        history.setOnClickListener(v -> new Storico(this));

        coin.setOnClickListener(v -> {
            if(!isLoggedIn(this)){
                String message = this.getString(R.string.logintoearncoin);
                Utility.oneLineDialog(this, message, null);
            }else{
                String message = this.getString(R.string.howtousecoin);
                Utility.oneLineDialog(this, message, null);
            }
        });

        shop.setOnClickListener(v -> {
            Utility.goTo(this, Shop.class);
        });

        gift.setOnClickListener(v -> {
            if(!isUsernameLoggedIn()){
                String message = this.getString(R.string.logintounlock);
                Utility.oneLineDialog(this, message, null);
            }else{
                watchAdDialog();
            }
        });

        closeGame.setOnClickListener(v -> this.onBackPressed());
    }

    public void initializeAd() {
        MobileAds.initialize(this, initializationStatus -> {
            RewardedInterstitialAd.load(MainActivity.this, Utility.REWARDED_ID,
                    new AdRequest.Builder().build(), new RewardedInterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(RewardedInterstitialAd ad) {
                            rewardedInterstitialAd = ad;
                            rewardedInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                                @Override public void onAdFailedToShowFullScreenContent(AdError adError) {}
                                @Override public void onAdShowedFullScreenContent(){}
                                @Override public void onAdDismissedFullScreenContent(){}
                            });
                        }
                        @Override
                        public void onAdFailedToLoad(LoadAdError loadAdError) {
                        }
                    });
        });
    }

    @Override
    public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
        rewardedInterstitialAd = null;

        int currentCoin = SharedPref.getCoin();
        loginClass.setCoin(currentCoin + GIFT_COINS);
        updateCoins();

        Utility.oneLineDialog(this, getString(R.string.rewardsuccess), null);

        initializeAd();
    }

    private void watchAdDialog(){
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.adv_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ViewGroup dialogParent = dialog.findViewById(R.id.adv_dialog_parent);
        TextView dialogText = dialog.findViewById(R.id.adv_dialog_description);

        dialogText.setText(getString(R.string.watchads).replace("{coins}", String.valueOf(GIFT_COINS)));

        Button dialogOk = dialog.findViewById(R.id.adv_dialog_okDialog);
        dialogOk.setOnClickListener(v1 -> {
            dialog.dismiss();

            if(rewardedInterstitialAd == null){
                Utility.oneLineDialog(this, this.getString(R.string.adnotready), null);
            }else{
                showPrizeAd();
            }
        });

        ImageView dialogClose = dialog.findViewById(R.id.adv_dialog_closeDialog);
        dialogClose.setOnClickListener(v2 -> dialog.dismiss());

        Utility.ridimensionamento((AppCompatActivity) this, dialogParent);
        dialog.show();
    }

    private void showPrizeAd(){
        rewardedInterstitialAd.show(this, this);
    }

    @Override
    public void onBackPressed() {
        Utility.oneLineDialog(this, this.getString(R.string.confirmleave), this::finishAffinity);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCoins();

        if(isLoggedIn(this))
            initializeAd();

        if(ActivityGame.multiplayer && ActivityMultiplayerGame.onStop)
            ActivityMultiplayerGame.onStop = false;

        if(LoginActivity.login && isFacebookLoggedIn()){
            FirebaseClass.editFieldFirebase(fbUID,"nome", loginClass.getFBNome());
            FirebaseClass.editFieldFirebase(fbUID,"cognome", loginClass.getFBCognome());
            LoginActivity.login = false;
        }

        if(!ActivityGame.multiplayer)
            return;

        if(!ActivityGame.leftGame)
            engineMultiplayer.accediHost(this, engineMultiplayer.codiceStanza);
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
