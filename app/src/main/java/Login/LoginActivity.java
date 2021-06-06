package Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.ActivityGame;
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


        if(!loginClass.isFacebookLoggedIn())
        {
            TextView i;
            LoginButton l;
            setContentView(R.layout.login_page);

            this.registerButtons();

            callbackManager = CallbackManager.Factory.create();

            i = (TextView)findViewById(R.id.info);
            l = (LoginButton)findViewById(R.id.login_button);

            l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    i.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
                    logIn();
                }

                @Override
                public void onCancel() {
                    i.setText("Login attempt canceled.");
                }

                @Override
                public void onError(FacebookException e) {
                    i.setText("Login attempt failed.");
                }
            });
        }else{
            logIn();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
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
}