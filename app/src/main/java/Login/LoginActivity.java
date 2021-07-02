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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;

import Home.MainActivity;
import gameEngine.Utility;



public class LoginActivity extends AppCompatActivity {
    private CallbackManager callbackManager;

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
        Utility.ridimensionamento(this, findViewById(R.id.campogioco));

        callbackManager = CallbackManager.Factory.create();

        LoginButton l = findViewById(R.id.login_button);

        Button why = findViewById(R.id.button1);
        why.setOnClickListener(v -> Utility.createDialog(this, why.getText().toString(), this.getString(R.string.whylogintext)));

        Button back = findViewById(R.id.button2);
        back.setOnClickListener(v -> super.onBackPressed());

        final LoginActivity curActivity = this;

        //@TODO gestire il logout da facebook
        l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
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
    }


    void loginMsg(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    void accountPage(){
        setContentView(R.layout.fb_profile);
        Utility.ridimensionamento(this, findViewById(R.id.campogioco));

        TextView nome = findViewById(R.id.nome);
        nome.setText(loginClass.getFBNome() + " " + loginClass.getFBCognome());

        TextView nVittorie = findViewById(R.id.vittorieValore);
        TextView nSconfitte = findViewById(R.id.sconfitteValore);
        TextView nRateo = findViewById(R.id.rateoValore);

        TextView accountId = findViewById(R.id.idValore);
        accountId.setText(loginClass.getFBUserId());

        Button modificaProfilo = findViewById(R.id.editB);
        modificaProfilo.setOnClickListener(v -> {
            final String editProfileTut = "https://www.facebook.com/profile.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(editProfileTut));
            startActivity(browserIntent);
        });

        View.OnClickListener doLogout = (v) -> findViewById(R.id.logoutHook).performClick();

        findViewById(R.id.logout).setOnClickListener(doLogout);
        findViewById(R.id.logoutB).setOnClickListener(doLogout);

        ProfilePictureView imgProfile = findViewById(R.id.friendProfilePicture);
        loginClass.setImgProfile(imgProfile);
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
