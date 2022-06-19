package game.danielesimone.briscola;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import Login.LoginActivity;
import Login.loginClass;
import gameEngine.Utility;

public class GameActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkIfInternetIsOn();
    }

    protected void checkIfInternetIsOn(){
        if(!Utility.isNetworkAvailable(this)){
            if(loginClass.isUsernameLoggedIn(this, false)){
                Toast.makeText(this, getString(R.string.internetofflinereason), Toast.LENGTH_LONG).show();
                LoginActivity.doLogout(this);
            }
        }
    }
}
