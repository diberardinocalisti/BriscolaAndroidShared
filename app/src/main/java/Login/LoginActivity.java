package Login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;


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


            callbackManager = CallbackManager.Factory.create();

            i = (TextView)findViewById(R.id.info);
            l = (LoginButton)findViewById(R.id.login_button);

            l.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    i.setText("User ID: " + loginResult.getAccessToken().getUserId() + "\n" + "Auth Token: " + loginResult.getAccessToken().getToken());
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

        }else
        {
            setContentView(R.layout.fb_profile);

            TextView nome,cognome;
            ProfilePictureView imgProfile;

            nome = (TextView)findViewById(R.id.nome);
            cognome = (TextView) findViewById(R.id.cognome);
            imgProfile = (ProfilePictureView) findViewById(R.id.friendProfilePicture);


            loginClass.setImgProfile(imgProfile);
            nome.setText(loginClass.getFBNome());
            cognome.setText(loginClass.getFBCognome());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);

        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}