package Home;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import Login.Avatar;
import Login.loginClass;
import firebase.FirebaseClass;
import game.danielesimone.briscola.GameActivity;
import game.danielesimone.briscola.R;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.EmailUser;

import static Login.loginClass.isUsernameLoggedIn;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;

public class Shop extends GameActivity{
    Integer coinShopped = 0;

    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.shop);

        getSupportActionBar().hide();
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        mostraProdotti();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void mostraProdotti(){
        final LinearLayout premiumAvatarsSW = this.findViewById(R.id.shop_avatarGallery);
        final LinearLayout coinSW = this.findViewById(R.id.shop_moneteGallery);
        final LinearLayout adsSW = this.findViewById(R.id.shop_adsGallery);

        premiumAvatarsSW.removeAllViews();
        coinSW.removeAllViews();
        adsSW.removeAllViews();

        setProgressBarVisibility(View.VISIBLE);

        new Thread(() -> {
            for(Avatar.PremiumAvatar avatar : Avatar.PREMIUM_AVATARS)
                mostraAvatar(avatar);

            runOnUiThread(() -> {
                mostraMonete(150, 0.99);
                mostraMonete(500, 1.99);
                mostraMonete(1000, 3.99);
                mostraAdRemover(0.99);
                setProgressBarVisibility(View.INVISIBLE);
            });
        }).start();

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraAvatar(Avatar.PremiumAvatar avatar){
        String avatarName = avatar.getNomeAvatar();
        String avatarId = avatar.getIdAvatar();
        int avatarPrice = avatar.getCostoAvatar();

        int currCoins = SharedPref.getCoin();

        Runnable onClick = () -> {
            if(!isUsernameLoggedIn(this)){
                String message = this.getString(R.string.logintounlock);
                Utility.oneLineDialog(this, message, null);
                return;
            }

            FirebaseClass.getFbRef().child(loginClass.getId()).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot snapshot){
                    EmailUser currUser = snapshot.getValue(EmailUser.class);
                    Class<?> userClass = currUser.getClass();

                    try{
                        Field selectedAvatarField = userClass.getDeclaredField(avatarId);

                        boolean isAvatarAlreadyUnlocked = selectedAvatarField.getBoolean(currUser);
                        if(isAvatarAlreadyUnlocked){
                            Utility.oneLineDialog(Shop.this, Shop.this.getString(R.string.itemalreadyunlocked), null);
                        }else{
                            Utility.oneLineDialog(Shop.this, Shop.this.getString(R.string.buyconfirm), () -> {
                                if(SharedPref.getCoin() >= avatarPrice){
                                    try{
                                        selectedAvatarField.set(currUser, true);
                                        FirebaseClass.editFieldFirebase(loginClass.getId(), avatarId,true);
                                    }catch(IllegalAccessException e){
                                        e.printStackTrace();
                                    }

                                    loginClass.setCoin(SharedPref.getCoin() - avatarPrice, Shop.this);
                                    mostraProdotti();
                                    Utility.oneLineDialog(Shop.this, Shop.this.getString(R.string.boughtsuccess), null);
                                }else{
                                    int neededCoins = avatarPrice - currCoins;
                                    String message = Shop.this.getString(R.string.needmorecoins).replace("{coins}", String.valueOf(neededCoins));
                                    Utility.oneLineDialog(Shop.this, message, null);
                                }
                            });
                        }

                    }catch(IllegalAccessException | NoSuchFieldException e){
                        e.printStackTrace();
                    }
                }

                @Override public void onCancelled(@NonNull @NotNull DatabaseError error){}
            });
        };

        Object locker = new Object();

        FirebaseClass.getFbRef().child(loginClass.getId()).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot snapshot){
                EmailUser currUser = snapshot.getValue(EmailUser.class);
                Class<?> userClass = currUser.getClass();

                try{
                    Field selectedAvatarField = userClass.getDeclaredField(avatarId);
                    boolean hasUnlockedAvatar = selectedAvatarField.getBoolean(currUser);

                    Drawable avatarDrawable = getResources().getDrawable(getResources().getIdentifier(avatarId, "drawable", getPackageName()));
                    mostraProdotto(avatarName,
                            R.id.avatarshop_Name,
                            avatarDrawable,
                            R.id.avatarshop_Image,
                            avatarPrice,
                            hasUnlockedAvatar,
                            R.id.avatarshop_Price,
                            R.id.avatarshop_Parent,
                            R.id.shop_avatarGallery,
                            R.layout.singleavatarshop,
                            R.id.avatarshop_locked,
                            onClick);

                    synchronized (locker){
                        locker.notifyAll();
                    }
                }
                catch(NoSuchFieldException | IllegalAccessException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error){}
        });

        try{
            synchronized (locker){
                locker.wait();
            }
        }
        catch(InterruptedException e){
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraMonete(int nMonete, double price){
        Runnable onClick = () -> {
        /*if(!isUsernameLoggedIn()){
                String message = this.getString(R.string.logintounlock);
                Utility.oneLineDialog(this, message, null);
                return;
        }
        SkuDetails itemInfo = (nMonete == 150 ? info.get(0) : nMonete == 500 ? info.get(1) : nMonete == 1000 ? info.get(2) : null);

        coinShopped = nMonete;

        billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build*/
            Utility.oneLineDialog(this, this.getString(R.string.featurenotavailable), null);
        };

        mostraProdotto(String.valueOf(nMonete),
                R.id.coinAmount,
                this.getDrawable(R.drawable.coinbag),
                R.id.coinImage,
                price,
                R.id.coinPrice,
                R.id.coinParent,
                R.id.shop_moneteGallery,
                R.layout.singlecoin,
                onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraAdRemover(double price){
        Runnable onClick = () -> {
/*            SkuDetails itemInfo = info.get(3);

            billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build());

            coinShopped = -1;*/
            Utility.oneLineDialog(this, this.getString(R.string.featurenotavailable), null);
        };

        mostraProdotto(String.valueOf(price),
                R.id.noadsshop_Price,
                this.getDrawable(R.drawable.no_ads),
                0,
                price,
                R.id.noadsshop_Price,
                R.id.noadsshop_Parent,
                R.id.shop_adsGallery,
                R.layout.rimuoviads,
                onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraProdotto(String productDescriptionStr,
                                int productDescriptionView,
                                Drawable productIconDrawable,
                                int productIconId,
                                double productPriceValue,
                                int productPriceId,
                                int parentViewId,
                                int parentLayoutId,
                                int layoutToAddId,
                                Runnable callback){
        mostraProdotto(productDescriptionStr,
                productDescriptionView,
                productIconDrawable,
                productIconId,
                productPriceValue,
                false,
                productPriceId,
                parentViewId,
                parentLayoutId,
                layoutToAddId,
                0,
                callback);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraProdotto(String productDescriptionStr,
                                int productDescriptionView,
                                Drawable productIconDrawable,
                                int productIconId,
                                double productPriceValue,
                                boolean isItemUnlocked,
                                int productPriceId,
                                int parentViewId,
                                int parentLayoutId,
                                int layoutToAddId,
                                int itemLockedId,
                                Runnable callback){

        runOnUiThread(() -> {
            LayoutInflater inflater = LayoutInflater.from(this);

            final LinearLayout gallery = this.findViewById(parentLayoutId);
            int layoutToAdd = layoutToAddId;

            View view = inflater.inflate(layoutToAdd, gallery, false);

            ViewGroup parentView = view.findViewById(parentViewId);

            TextView productDescription = view.findViewById(productDescriptionView);
            productDescription.setText(productDescriptionStr);

            if(productIconId != 0){
                ImageView productIcon = view.findViewById(productIconId);
                productIcon.setImageDrawable(productIconDrawable);
            }

            TextView productPrice = view.findViewById(productPriceId);

            String priceApp = String.valueOf(productPriceValue);
            // Se il prezzo termina per ".0", rimuove gli zeri ridondanti;
            String priceString = priceApp.endsWith(".0") ? priceApp.replace(".0", "") : priceApp;

            productPrice.setText(String.format("%s: %s", this.getString(R.string.price), priceString));

            ImageView itemLockedIcon;
            if(itemLockedId != 0){
                itemLockedIcon = view.findViewById(itemLockedId);
                itemLockedIcon.setVisibility(isItemUnlocked ? View.INVISIBLE : View.VISIBLE);
            }

            parentView.setOnClickListener(v -> {
                if(!isItemUnlocked){
                    callback.run();
                }else{
                    Utility.oneLineDialog(this, this.getString(R.string.itemalreadyunlocked), null);
                }
            });

            Utility.ridimensionamento(this, parentView);
            gallery.addView(view);
        });
    }

    private void setProgressBarVisibility(int visibility){
        ProgressBar progressBar = findViewById(R.id.shop_loadingbar);
        progressBar.setVisibility(visibility);

        ViewGroup container = findViewById(R.id.shop_subparent);
        container.setVisibility(visibility == View.VISIBLE ? View.INVISIBLE : View.VISIBLE);
    }


    @Override
    protected void onResume() {
        super.onResume();

        coinShopped = 0;
    }
}
