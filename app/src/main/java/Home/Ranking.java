/*
package Home;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ScrollView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;

import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.Game;
import gameEngine.Utility;
import multiplayer.RoomList;

import static Login.loginClass.isFacebookUser;
import static Login.loginClass.isUser;
import static gameEngine.Game.activity;
import static multiplayer.GameRoom.isGameRoom;

public class Ranking extends Dialog {
    public Ranking(AppCompatActivity appCompatActivity){
        super(appCompatActivity);
        this.setContentView(R.layout.text_chat);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
            Iterable<DataSnapshot> result = task.getResult().getChildren();

            ArrayList<RoomList.Room> rooms = new ArrayList<>();

            for(DataSnapshot d: result){
                if(isUser(d)){
                    String NOME_ID = isFacebookUser(d) ? "nome" : "username";

                    for(DataSnapshot row : d.getChildren()){
                        switch (row.getKey()) {
                            case "host": nomeHost = String.valueOf(row.getValue()); break;
                            case "enemy": nomeEnemy = String.valueOf(row.getValue()); break;
                            case "idHost": idHost = String.valueOf(row.getValue()); break;
                            case "gameCode": gameCode = String.valueOf(row.getValue()); break;
                        }
                    }
                }
            }

        });
    }
}
*/
