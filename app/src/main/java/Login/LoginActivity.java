package Login;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

        if(!loginClass.isFacebookLoggedIn()){
            setContentView(R.layout.login_page);
            callbackManager = CallbackManager.Factory.create();

            LoginButton l = (LoginButton) findViewById(R.id.login_button);

            Button why = findViewById(R.id.button1);
            why.setOnClickListener(v -> Utility.createDialog(this, why.getText().toString(), "Effettuando l'accesso potrai giocare in multigiocatore e sfidare i tuoi amici in ogni momento!"));

            Button back = findViewById(R.id.button2);
            back.setOnClickListener(v -> super.onBackPressed());

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
            loginPage();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }


    void loginMsg(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    void loginPage(){
        setContentView(R.layout.fb_profile);
        findViewById(R.id.logout).setOnClickListener(v -> findViewById(R.id.logoutHook).performClick());

        TextView nome = (TextView) findViewById(R.id.nome);
        nome.setText(loginClass.getFBNome() + " " + loginClass.getFBCognome());

        ProfilePictureView imgProfile = (ProfilePictureView) findViewById(R.id.friendProfilePicture);
        loginClass.setImgProfile(imgProfile);
    }
}
