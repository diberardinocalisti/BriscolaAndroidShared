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

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscolav10.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.Utility;
import multiplayer.User;


public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;
    public static String fbUID;
    public TextView nVittorie, nSconfitte, nRateo;
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

    void loginPage(){
        setContentView(R.layout.login_page);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        callbackManager = CallbackManager.Factory.create();

        LoginButton l = findViewById(R.id.login_button);
        //l.setPermissions("user_friends");

        Button why = findViewById(R.id.button1);
        why.setOnClickListener(v -> Utility.createDialog(this, why.getText().toString(), this.getString(R.string.whylogintext)));

        Button back = findViewById(R.id.button2);
        back.setOnClickListener(v -> super.onBackPressed());

        final LoginActivity curActivity = this;

        //@TODO gestire il logout da facebook
        l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                    AccessToken token = loginResult.getAccessToken();
                    String uid = token.getUserId();
                    fbUID = uid;

                    FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
                        if(task.isSuccessful()){
                            boolean esiste = false;
                            for(DataSnapshot d: task.getResult().getChildren()){
                                if(d.getKey().equals(uid)){
                                    esiste = true;
                                    break;
                                }
                            }

                            if(!esiste) {
                                login = true;
                                User user = new User(0,0,"","");
                                FirebaseClass.addUserToFirebase(user,uid);
                            }
                        }
                    });
                    // TODO: indirizzare l'utente alla pagina del profilo una volta effettuato l'accesso;
                    Utility.goTo(curActivity, MainActivity.class);
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

        AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null)
                {
                    LoginManager.getInstance().logOut();
                    Toast.makeText(getApplicationContext(),"Logout!",Toast.LENGTH_SHORT).show();
                    Utility.goTo(LoginActivity.this,MainActivity.class);
                }
            }
        };
    }


    void loginMsg(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    void accountPage(){
        setContentView(R.layout.fb_profile);
        Utility.ridimensionamento(this, findViewById(R.id.parent));

        TextView nome = findViewById(R.id.nome);
        nome.setText(loginClass.getFullFBName());

        nVittorie = findViewById(R.id.vittorieValore);
        nSconfitte = findViewById(R.id.sconfitteValore);
        nRateo = findViewById(R.id.rateoValore);

        setStatistiche();
        setListeners();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    public void setStatistiche()
    {
        TextView accountId = findViewById(R.id.idValore);
        accountId.setText(loginClass.getFBUserId());

        ImageView imgProfile = findViewById(R.id.friendProfilePicture);
        loginClass.setImgProfile(this, loginClass.getFBUserId(), imgProfile);

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String vinte, perse;
                float rateo, vinteF = 0.0f, perseF = 0.0f;

                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("perse"))
                    {
                        perse = String.valueOf(d.getValue());
                        perseF = Float.parseFloat(perse);
                        nSconfitte.setText(String.valueOf(d.getValue()));
                    }

                    if(d.getKey().equals("vinte"))
                    {
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

    public void setListeners(){
        Button modificaProfilo = findViewById(R.id.editB);
        modificaProfilo.setOnClickListener(v -> {
            final String editProfileTut = "https://www.facebook.com/profile.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(editProfileTut));
            startActivity(browserIntent);
        });

        View.OnClickListener doLogout = (v) -> findViewById(R.id.logoutHook).performClick();

        findViewById(R.id.logout).setOnClickListener(doLogout);
        findViewById(R.id.logoutB).setOnClickListener(doLogout);
    }
}
