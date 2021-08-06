package multiplayer;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import firebase.FirebaseClass;
import game.danielesimone.briscolav10.R;
import gameEngine.Utility;

import static Login.loginClass.setImgProfile;
import static multiplayer.GameRoom.isGameRoom;

public class RoomList extends AppCompatActivity {
    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.room_list);

        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.enableTopBar(this);

        initializeLayout();
        showRooms();
    }

    protected void initializeLayout(){
        ProgressBar progressBar = findViewById(R.id.loadingBar);

        View refreshBtn = findViewById(R.id.refresh);

        refreshBtn.setOnClickListener(v -> {
            if(progressBar.getVisibility() == View.VISIBLE)
                return;

            LinearLayout scrollViewLayout = findViewById(R.id.scrollViewLayout);
            scrollViewLayout.removeAllViews();
            showRooms();
        });
    }

    protected void showRooms(){
        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView alert = findViewById(R.id.alert);
        alert.setText(new String());

        FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
            Iterable<DataSnapshot> result = task.getResult().getChildren();

            int nRooms = 0;

            for(DataSnapshot d: result){
                if(isGameRoom(d)){
                    nRooms++;

                    String nomeHost = new String(), nomeEnemy = new String(), idHost = new String(), gameCode = new String();
                    for(DataSnapshot row : d.getChildren()){
                       if(row.getKey().equals("host"))
                            nomeHost = String.valueOf(row.getValue());
                        else if(row.getKey().equals("enemy"))
                            nomeEnemy = String.valueOf(row.getValue());
                        else if(row.getKey().equals("idHost"))
                            idHost = String.valueOf(row.getValue());
                        else if(row.getKey().equals("gameCode"))
                            gameCode = String.valueOf(row.getValue());

                        //if(row.getKey().equals("gameCode"))
                            //FirebaseClass.deleteFieldFirebase(null, String.valueOf(row.getValue()));
                    }

                    addRoom(nomeHost, nomeEnemy, idHost, gameCode);
                }
            }

            if(nRooms == 0){
                alert.setText(getString(R.string.noavailableroom));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void addRoom(String nomeHost, String nomeEnemy, String idHost, String gameCode){
        LinearLayout scrollViewLayout = findViewById(R.id.scrollViewLayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.singleroom, null);

        TextView hostName = parentView.findViewById(R.id.hostName);
        ImageView hostImage = parentView.findViewById(R.id.hostIcon);
        TextView nPlayers = parentView.findViewById(R.id.nPlayers);
        View joinBtn = parentView.findViewById(R.id.joinIcon);

        hostName.setText(nomeHost);
        setImgProfile(this, idHost, hostImage);

        scrollViewLayout.addView(parentView);

        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.INVISIBLE);

        boolean isFull = !nomeEnemy.equals("null");

        if(isFull){
            nPlayers.setText(getString(R.string.full));
        }

        joinBtn.setOnClickListener(v -> {
            if(isFull){
                Utility.oneLineDialog(RoomList.this, RoomList.this.getString(R.string.roomfull), () -> {});
            }else{
                engineMultiplayer.accediGuest(RoomList.this, gameCode);
            }
        });

    }
}
