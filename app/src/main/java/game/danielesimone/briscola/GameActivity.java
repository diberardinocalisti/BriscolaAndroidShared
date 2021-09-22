package game.danielesimone.briscola;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import gameEngine.Utility;

public class GameActivity extends AppCompatActivity{
    protected void onResume(){
        super.onResume();
        checkIfInternetIsOn();
    }

    protected void checkIfInternetIsOn(){
        if(!Utility.isNetworkAvailable(this)){
            Utility.oneLineDialog(this, this.getString(R.string.internetofflinereason), this.getString(R.string.ok), this.getString(R.string.cancel), this::finishAffinity, this::finishAffinity, this::finishAffinity);
        }
    }
}
