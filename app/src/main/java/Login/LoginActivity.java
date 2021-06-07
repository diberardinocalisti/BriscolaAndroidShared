package Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

import Home.MainActivity;
import gameEngine.Utility;


public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private CallbackManager callbackManager;

    SignInButton sButton;
    private GoogleApiClient gApi;
    private static final int SIGN_IN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(!loginClass.isFacebookLoggedIn())
        {
            TextView i;
            LoginButton l;
            setContentView(R.layout.login_page);

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();

            gApi = new GoogleApiClient.Builder(this).enableAutoManage(this,this).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

            sButton = (SignInButton) findViewById(R.id.googleLogin);

            sButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = Auth.GoogleSignInApi.getSignInIntent(gApi);
                    startActivityForResult(i, SIGN_IN);
                }
            });

            this.registerButtons();

            callbackManager = CallbackManager.Factory.create();

            i = (TextView)findViewById(R.id.info);
            l = (LoginButton)findViewById(R.id.login_button);

            //@TODO gestire il logout da facebook
            l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    loginMsg("Login effettuato con successo!");
                }

                @Override
                public void onCancel() {
                    loginMsg("Ops... qualcosa è andato storto!");
                }

                @Override
                public void onError(FacebookException e) {
                    loginMsg("Ops... qualcosa è andato storto!");
                }
            });
        }else{
            logIn();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);

        if(requestCode == SIGN_IN)
        {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }else
        {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }


    private void handleSignInResult(GoogleSignInResult result){
        if(result.isSuccess()){
            Intent i = new Intent(getApplicationContext(),MainActivity.class);
            startActivity(i);
            Toast.makeText(getBaseContext(),"Ti sei loggato con successo!",Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(getApplicationContext(),"Sign in cancel",Toast.LENGTH_LONG).show();
        }
    }


    void loginMsg(CharSequence msg)
    {
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    void logIn(){

        setContentView(R.layout.fb_profile);

        TextView nome, cognome;
        ProfilePictureView imgProfile;

        nome = (TextView)findViewById(R.id.nome);
        cognome = (TextView) findViewById(R.id.cognome);
        imgProfile = (ProfilePictureView) findViewById(R.id.friendProfilePicture);


        loginClass.setImgProfile(imgProfile);
        nome.setText(loginClass.getFBNome());
        cognome.setText(loginClass.getFBCognome());

    }

    void registerButtons(){
        Button why = findViewById(R.id.button1);
        why.setOnClickListener(v -> Utility.createDialog(this, why.getText().toString(), "Effettuando l'accesso potrai giocare in multigiocatore e sfidare i tuoi amici in ogni momento!"));

        Button back = findViewById(R.id.button2);
        back.setOnClickListener(v -> super.onBackPressed());
    }

    @Override
    public void onConnectionFailed(@NonNull @org.jetbrains.annotations.NotNull ConnectionResult connectionResult) {

    }
}
