package Home;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import Login.LoginActivity;
import Login.loginClass;
import UI.UiColor;
import firebase.FirebaseClass;
import game.danielesimone.briscola.GameActivity;
import game.danielesimone.briscola.R;
import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.EmailUser;
import multiplayer.User;
import okhttp3.internal.Util;

import static Login.LoginActivity.fbUID;
import static Login.loginClass.isUser;
import static Login.loginClass.setImgProfile;
import static gameEngine.Game.activity;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import multiplayer.User;

public class Shop extends GameActivity{
    BillingClient billingClient;

    List<SkuDetails> info;

    Integer coinShopped = 0;

    /*
        INFO
    *   0 --> 150 monete, 0,99
    *   1 --> 500 monete, 1,99
    *   2 --> remove_ads
    * */
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.shop);

        coinShopped = 0;
        billingClient = BillingClient.newBuilder(this)
                            .enablePendingPurchases()
                            .setListener((billingResult, list) -> {
                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null)
                                {
                                    for(Purchase p: list)
                                    {
                                        if(p.getPurchaseState() == Purchase.PurchaseState.PURCHASED)
                                        {
                                            //verifyPurchase(p);
                                            if(coinShopped != -1)
                                            {
                                                int moneteOttenute = SharedPref.getCoin() + coinShopped;
                                                System.out.println("Monete attuali: " + SharedPref.getCoin() +
                                                                    "\nMonete da aggiungere: " + coinShopped +
                                                                    "\nMonete totali " + moneteOttenute);
                                                loginClass.setCoin(moneteOttenute);
                                            }else {
                                                setRemoveAd();
                                            }


                                            Utility.goTo(Shop.this, MainActivity.class);
                                        }
                                    }
                                }
                            })
                            .build();

        connectToGooglePlayBilling();

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);
        mostraProdotti();
    }

    @Override
    protected void onResume() {
        super.onResume();

        coinShopped = 0;
    }

    private void connectToGooglePlayBilling()
    {
        billingClient.startConnection(
                new BillingClientStateListener() {
                    @Override
                    public void onBillingServiceDisconnected() {
                        connectToGooglePlayBilling();
                    }

                    @Override
                    public void onBillingSetupFinished(@NonNull BillingResult billingResult) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                            getProductDetails();
                    }
                }
        );
    }

    //
    private void verifyPurchase(Purchase p)
    {
        String reqUrl = "https://us-central1-briscola-75019.cloudfunctions.net/verifyPurchase?" +
                "purchaseToken=" + p.getPurchaseToken() +" &" +
                "purchaseTime=" + p.getPurchaseTime()+ "&" +
                "orderId=" + p.getOrderId();

        StringRequest stringRequest = new StringRequest(
                Request.Method.POST,
                reqUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject purchaseInfoFromServer = new JSONObject(response);
                            if(purchaseInfoFromServer.getBoolean("isValid"))
                            {
                                ConsumeParams consumeParams = ConsumeParams.newBuilder().setPurchaseToken(p.getPurchaseToken()).build();
                                billingClient.consumeAsync(
                                        consumeParams,
                                        new ConsumeResponseListener() {
                                            @Override
                                            public void onConsumeResponse(@NonNull BillingResult billingResult, @NonNull String s) {
                                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK)
                                                {
                                                    //Utility.oneLineDialog(getApplicationContext(), String.valueOf(R.string.verifiedConsume), null);
                                                }
                                            }
                                        }
                                );

                                AcknowledgePurchaseParams acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder().setPurchaseToken(p.getPurchaseToken()).build();
                                billingClient.acknowledgePurchase(
                                        acknowledgePurchaseParams,
                                        new AcknowledgePurchaseResponseListener() {
                                            @Override
                                            public void onAcknowledgePurchaseResponse(@NonNull BillingResult billingResult) {
                                                if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                                                    Toast.makeText(activity, "Consumed!", Toast.LENGTH_LONG).show();
                                                }
                                            }
                                        }
                                );
                            }


                        }catch (Exception e)
                        {

                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        );

        Volley.newRequestQueue(this).add(stringRequest);
    }

    private void getProductDetails()
    {
        List<String> productIds = new ArrayList<>();
        productIds.add("remove_ads");
        productIds.add("monete_1");
        productIds.add("monete_2");
        productIds.add("monete_3");

        SkuDetailsParams getProoductDetailsQuery = SkuDetailsParams
                            .newBuilder()
                            .setSkusList(productIds)
                            .setType(BillingClient.SkuType.INAPP)
                            .build();

        billingClient.querySkuDetailsAsync(
                getProoductDetailsQuery,
                new SkuDetailsResponseListener() {
                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void onSkuDetailsResponse(@NonNull BillingResult billingResult, @Nullable List<SkuDetails> list) {
                        if(billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK && list != null)
                        {
                            info = list;
                        }
                    }
                }

        );
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void mostraProdotti(){
        mostraAvatar("Maestro", "avatar_21", 150);
        mostraAvatar("John Bick", "avatar_22", 150);
        mostraAvatar("Lio Nessi", "avatar_23", 150);
        mostraAvatar("Donald Brump", "avatar_24", 150);
        mostraAvatar("Doge", "avatar_25", 150);

        mostraMonete(150, 0.99);
        mostraMonete(500, 1.99);
        mostraMonete(1000, 3.99);
        mostraAdRemover(0.99);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraAvatar(String avatarName, String avatarId, int avatarPrice){
        int currCoins = SharedPref.getCoin();

        Runnable onClick = () -> {
            Utility.oneLineDialog(this, this.getString(R.string.buyconfirm), () -> {
                if(SharedPref.getCoin() >= avatarPrice){
                    FirebaseClass.getFbRef().child(fbUID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot){
                            EmailUser currUser = snapshot.getValue(EmailUser.class);
                            Class<?> userClass = currUser.getClass();

                            try{
                                Field selectedAvatarField = userClass.getDeclaredField(avatarId);
                                selectedAvatarField.set(currUser, true);

                                loginClass.setCoin(SharedPref.getCoin() - avatarPrice);

                                Utility.oneLineDialog(Shop.this, Shop.this.getString(R.string.boughtsuccess), null);
                            }
                            catch(NoSuchFieldException | IllegalAccessException e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull @NotNull DatabaseError error){}
                    });
                }else{
                    int neededCoins = avatarPrice - currCoins;
                    String message = this.getString(R.string.needmorecoins).replace("{coins}", String.valueOf(neededCoins));
                    Utility.oneLineDialog(this, message, null);
                }
            });
        };

        Drawable avatarDrawable = getResources().getDrawable(getResources().getIdentifier(avatarId, "drawable", getPackageName()));
        mostraProdotto(avatarName,
                R.id.avatarshop_Name,
                avatarDrawable,
                R.id.avatarshop_Image,
                avatarPrice,
                R.id.avatarshop_Price,
                R.id.avatarshop_Parent,
                R.id.shop_avatarGallery,
                R.layout.singleavatarshop,
                onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraMonete(int nMonete, double price){
        Runnable onClick = () -> {
//            SkuDetails itemInfo = (nMonete == 150 ? info.get(0) : nMonete == 500 ? info.get(1) : nMonete == 1000 ? info.get(2) : null);
//
//            coinShopped = nMonete;
//
//            billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build());
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

    private void mostraAdRemover(double price){
        Runnable onClick = () -> {
            // Elaborare il click del box delle monete;
            //Utility.oneLineDialog(this, "E' stato selezionato l'ad remover", null);

            SkuDetails itemInfo = info.get(3);

            billingClient.launchBillingFlow(this, BillingFlowParams.newBuilder().setSkuDetails(itemInfo).build());

            coinShopped = -1;
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

        parentView.setOnClickListener(v -> callback.run());

        Utility.ridimensionamento(this, parentView);
        gallery.addView(view);
    }

    private void setRemoveAd()
    {
        FirebaseClass.editFieldFirebase(fbUID,"removeAd","true");
    }
}
