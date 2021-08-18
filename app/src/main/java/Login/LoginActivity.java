package Login;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscola.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.database.DataSnapshot;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Utility;
import multiplayer.User;


public class LoginActivity extends AppCompatActivity {
    public static AccessTokenTracker logoutTraker;
    public static String fbUID;
    public CallbackManager callbackManager;
    public static boolean login = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utility.enableTopBar(this);

        if(!loginClass.isFacebookLoggedIn()){
            loginPage();
        }else{
            accountPage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void loginPage(){
        setContentView(R.layout.login_page);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        callbackManager = CallbackManager.Factory.create();

        LoginButton l = findViewById(R.id.login_button);
        //l.setPermissions("user_friends");

        Button why = findViewById(R.id.login_why);
        why.setOnClickListener(v -> Utility.createDialog(this, why.getText().toString(), this.getString(R.string.whylogintext)));

        Button back = findViewById(R.id.login_back);
        back.setOnClickListener(v -> super.onBackPressed());

        l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                fbUID = token.getUserId();

                FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean esiste = false;
                        for(DataSnapshot d: task.getResult().getChildren()){
                            if(d.getKey().equals(fbUID)){
                                esiste = true;
                                break;
                            }
                        }

                        if(!esiste) {
                            login = true;
                            User user = new User(0,0,"","");
                            FirebaseClass.addUserToFirebase(user, fbUID);
                        }
                    }
                });
                accountPage();
            }
            @Override
            public void onCancel() {
                loginMsg(LoginActivity.this.getString(R.string.unknownerror));
            }

            @Override
            public void onError(FacebookException e) {
                loginMsg(LoginActivity.this.getString(R.string.unknownerror));
            }
        });
    }


    protected void loginMsg(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    protected void accountPage(){
        setContentView(R.layout.fb_profile);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        final int refreshRate = 100;

        // Dato che il listener onSuccess viene eseguito nonostante le informazioni del profilo non siano ancora state ancora
        // lette, aspetto finché getCurrentProfile restituisce il profilo dell'utente;
        new Thread(() -> {
            while(Profile.getCurrentProfile() == null) {
                try {
                    Thread.sleep(refreshRate);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            LoginActivity.this.runOnUiThread(() -> {
                setStatistiche();
                setListeners();
                handleLogout();
            });
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    protected void setStatistiche(){
        TextView nome = findViewById(R.id.nome);
        nome.setText(loginClass.getFullFBName());

        TextView accountId = findViewById(R.id.idValore);
        accountId.setText(loginClass.getFBUserId());

        ImageView imgProfile = findViewById(R.id.friendProfilePicture);
        loginClass.setImgProfile(this, loginClass.getFBUserId(), imgProfile);

        TextView nVittorie = findViewById(R.id.vittorieValore);
        TextView nSconfitte = findViewById(R.id.sconfitteValore);
        TextView nRateo = findViewById(R.id.rateoValore);

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String vinte, perse;
                float rateo, vinteF = 0.0f, perseF = 0.0f;

                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("perse")){
                        perse = String.valueOf(d.getValue());
                        perseF = Float.parseFloat(perse);
                        nSconfitte.setText(String.valueOf(d.getValue()));
                    }

                    if(d.getKey().equals("vinte")) {
                        vinte = String.valueOf(d.getValue());
                        vinteF = Float.parseFloat(vinte);
                        nVittorie.setText(String.valueOf(d.getValue()));
                    }
                }


                rateo = (vinteF != 0.0 && perseF != 0.0 ? vinteF/perseF : 0);

                if(perseF == 0.0)
                    rateo = vinteF;

                rateo = (float) (Math.round(rateo*100.0)/100.0);

                nRateo.setText(String.valueOf(rateo));
            }
        });
    }

    protected void setListeners(){
        Button modificaProfilo = findViewById(R.id.editB);
        modificaProfilo.setOnClickListener(v -> {
            final String editProfileUrl = "https://www.facebook.com/profile.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(editProfileUrl));
            startActivity(browserIntent);
        });

        View.OnClickListener doLogout = (v) -> findViewById(R.id.logoutHook).performClick();

        findViewById(R.id.logout).setOnClickListener(doLogout);
        findViewById(R.id.logoutB).setOnClickListener(doLogout);
    }

    protected void handleLogout(){
        // Creo un nuovo tracker solamente nel caso in cui non ne esista già uno;
        if(logoutTraker == null){
            logoutTraker = new AccessTokenTracker() {
                @Override
                protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                    if(currentAccessToken == null){
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getApplicationContext(), getString(R.string.logoutsuccess), Toast.LENGTH_SHORT).show();
                        Utility.goTo(LoginActivity.this,MainActivity.class);
                    }
                }
            };
        }
    }
}
