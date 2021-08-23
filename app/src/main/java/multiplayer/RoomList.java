package multiplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Collections;

import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.Utility;

import static Login.loginClass.setImgProfile;
import static multiplayer.GameRoom.isGameRoom;
import static multiplayer.engineMultiplayer.codiceStanza;

public class RoomList extends AppCompatActivity {
    private int selectedItem;
    private String[] filterOptions;

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
        refreshRooms();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void initializeLayout(){
        Utility.showAd(this);

        ProgressBar progressBar = findViewById(R.id.loadingBar);

        View refreshBtn = findViewById(R.id.refresh);
        View settingsBtn = findViewById(R.id.roomSettings);

        refreshBtn.setOnClickListener(v -> {
            if(progressBar.getVisibility() == View.VISIBLE)
                return;

            refreshRooms();
        });

        filterOptions = new String[]{RoomList.this.getString(R.string.showall), RoomList.this.getString(R.string.shownotempty)};
        selectedItem = 0;

        settingsBtn.setOnClickListener(v -> {
            AlertDialog.Builder dialog = new AlertDialog.Builder(RoomList.this);
            LayoutInflater inflater = (LayoutInflater) RoomList.this.getSystemService( LAYOUT_INFLATER_SERVICE );

            View roomSettings = inflater.inflate(R.layout.room_settings, null);
            Spinner filterSpinner = roomSettings.findViewById(R.id.filterSpinner);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(RoomList.this, android.R.layout.simple_spinner_item, filterOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filterSpinner.setAdapter(adapter);
            filterSpinner.setSelection(selectedItem);

            dialog.setPositiveButton(RoomList.this.getString(R.string.confirm), (dialog1, which) -> {
                selectedItem = filterSpinner.getSelectedItemPosition();
                refreshRooms();
            });

            dialog.setNegativeButton(RoomList.this.getString(R.string.cancel), null);
            dialog.setView(roomSettings);
            dialog.create().show();
        });
    }

    protected void refreshRooms(){
        LinearLayout scrollViewLayout = findViewById(R.id.scrollViewLayout);
        scrollViewLayout.removeAllViews();

        showRooms();
    }

    protected void showRooms(){
        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView alert = findViewById(R.id.alert);
        alert.setText(new String());

        FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
            Iterable<DataSnapshot> result = task.getResult().getChildren();

            ArrayList<Room> rooms = new ArrayList<>();

            for(DataSnapshot d: result){
                if(isGameRoom(d)){
                    String nomeHost = new String(), nomeEnemy = new String(), idHost = new String(), gameCode = new String();

                    for(DataSnapshot row : d.getChildren()){
                        switch (row.getKey()) {
                            case "host": nomeHost = String.valueOf(row.getValue()); break;
                            case "enemy": nomeEnemy = String.valueOf(row.getValue()); break;
                            case "idHost": idHost = String.valueOf(row.getValue()); break;
                            case "gameCode": gameCode = String.valueOf(row.getValue()); break;
                        }
                    }

                    boolean isFull = !nomeEnemy.equals("null");
                    rooms.add(new Room(nomeHost, idHost, gameCode, isFull));
                }
            }

            Collections.sort(rooms, (room1, room2) -> room1.getNomeHost().toLowerCase().compareTo(room2.getNomeHost().toLowerCase()));

            if(rooms.size() > 0){
                for(Room room : rooms)
                    addRoomToList(room.getNomeHost(), room.getHostId(), room.getGameCode(), room.isFull());
            }else{
                alert.setText(getString(R.string.noavailableroom));
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

    protected void addRoomToList(String nomeHost, String idHost, String gameCode, boolean isFull){
        //FirebaseClass.deleteFieldFirebase(null, gameCode);

        boolean showAll = filterOptions[selectedItem].equals(this.getString(R.string.showall));

        if(isFull && !showAll)
            return;

        LinearLayout scrollViewLayout = findViewById(R.id.scrollViewLayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.singleroom, null);

        TextView hostName = parentView.findViewById(R.id.hostName);
        ImageView hostImage = parentView.findViewById(R.id.hostIcon);
        View joinBtn = parentView.findViewById(R.id.joinIcon);
        TextView nPlayers = parentView.findViewById(R.id.nPlayers);

        hostName.setText(nomeHost);
        setImgProfile(this, idHost, hostImage);

        scrollViewLayout.addView(parentView);

        ProgressBar progressBar = findViewById(R.id.loadingBar);
        progressBar.setVisibility(View.INVISIBLE);

        if(isFull)
            nPlayers.setText(this.getString(R.string.full));

        joinBtn.setOnClickListener(v -> {
            if(!isFull)
                engineMultiplayer.accediGuest(RoomList.this, gameCode);
            else
                Utility.oneLineDialog(this, this.getString(R.string.roomfull), () -> {});
        });
    }

    public class Room {
        private final String gameCode;
        private final String nomeHost;
        private final String hostId;
        private final boolean isFull;

        public Room(String nomeHost, String hostId, String gameCode, boolean isFull) {
            this.nomeHost = nomeHost;
            this.hostId = hostId;
            this.gameCode = gameCode;
            this.isFull = isFull;
        }

        public String getGameCode() {
            return gameCode;
        }

        public String getNomeHost() {
            return nomeHost;
        }

        public String getHostId() {
            return hostId;
        }

        public boolean isFull() {
            return isFull;
        }
    }
}
