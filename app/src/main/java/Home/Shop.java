package Home;

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

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import UI.UiColor;
import game.danielesimone.briscola.GameActivity;
import game.danielesimone.briscola.R;
import gameEngine.Utility;
import okhttp3.internal.Util;

import static Login.loginClass.setImgProfile;
import static gameEngine.Game.activity;

public class Shop extends GameActivity{
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.shop);

        Utility.enableTopBar(this);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        inizializzaLayout();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void inizializzaLayout(){
        mostraAvatar("John Wick", "avatar_21", 150);
        mostraAvatar("Donald Trump", "avatar_22", 150);

        mostraMonete(150, 0.99);
        mostraMonete(500, 1.99);
        mostraMonete(1000, 3.99);
        // Etc etc;
    }

    private void mostraAvatar(String avatarName, String avatarId, double avatarPrice){
        Drawable avatarDrawable = getResources().getDrawable(getResources().getIdentifier(avatarId, "drawable", getPackageName()));

        Runnable onClick = () -> {
            // Elaborare il click del box degli avatar;
            Utility.oneLineDialog(this, "E' stato selezionato un avatar", null);
        };

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

    private void mostraMonete(int nMonete, double price){
        Runnable onClick = () -> {
            // Elaborare il click del box delle monete;
            Utility.oneLineDialog(this, "Sono state selezionate le monete", null);
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

        ImageView productIcon = view.findViewById(productIconId);
        productIcon.setImageDrawable(productIconDrawable);

        TextView productPrice = view.findViewById(productPriceId);

        String priceApp = String.valueOf(productPriceValue);
        // Se il prezzo termina per ".0", rimuove gli zeri ridondanti;
        String priceString = priceApp.endsWith(".0") ? priceApp.replace(".0", "") : priceApp;

        productPrice.setText(String.format("%s: %s", this.getString(R.string.price), priceString));

        parentView.setOnClickListener(v -> callback.run());

        Utility.ridimensionamento(this, parentView);
        gallery.addView(view);
    }
}
