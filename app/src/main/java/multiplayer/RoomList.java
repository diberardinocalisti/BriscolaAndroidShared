package multiplayer;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import okhttp3.internal.Util;

import static Login.loginClass.setImgProfile;
import static multiplayer.GameRoom.isGameRoom;
import static multiplayer.engineMultiplayer.codiceStanza;

public class RoomList extends AppCompatActivity {
    private int selectedItem;
    private String[] filterOptions;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Utility.enableTopBar(this);
        setContentView(R.layout.room_list);

        initializeLayout();
        refreshRooms();

        Utility.ridimensionamento(this, findViewById(R.id.room_list_parent));
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    protected void initializeLayout(){
        Utility.showAd(this);

        View settingsBtn = findViewById(R.id.room_list_roomSettings);

        filterOptions = new String[]{RoomList.this.getString(R.string.showall), RoomList.this.getString(R.string.shownotempty)};
        selectedItem = 0;

        SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.room_list_swipeRefreshLayout);
        ScrollView scrollView = findViewById(R.id.room_list_scrollView);

        swipeRefreshLayout.setEnabled(true);
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            swipeRefreshLayout.setEnabled(scrollY == 0);
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(false);
            refreshRooms();
        });

        settingsBtn.setOnClickListener(v -> {
            Dialog dialog = new Dialog(this);

            dialog.setContentView(R.layout.room_settings);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            ViewGroup parentView = dialog.findViewById(R.id.room_settings_parent);
            Spinner filterSpinner = dialog.findViewById(R.id.room_settings_filterSpinner);

            ArrayAdapter<String> adapter = new ArrayAdapter<>(RoomList.this, android.R.layout.simple_spinner_item, filterOptions);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            filterSpinner.setAdapter(adapter);
            filterSpinner.setSelection(selectedItem);

            View confirmButton = dialog.findViewById(R.id.room_settings_accept);
            View closeButton = dialog.findViewById(R.id.room_settings_close);
            View cancelButton = dialog.findViewById(R.id.room_settings_cancel);
            View.OnClickListener closeAction = v2 -> dialog.dismiss();

            confirmButton.setOnClickListener(v1 -> {
                selectedItem = filterSpinner.getSelectedItemPosition();
                dialog.dismiss();
                refreshRooms();
            });

            closeButton.setOnClickListener(closeAction);
            cancelButton.setOnClickListener(closeAction);

            dialog.create();
            Utility.ridimensionamento(this, parentView);
            dialog.show();
        });
    }

    protected void refreshRooms(){
        LinearLayout scrollViewLayout = findViewById(R.id.room_list_scrollViewLayout);
        scrollViewLayout.removeAllViews();

        showRooms();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void showRooms(){
        ProgressBar progressBar = findViewById(R.id.room_list_loadingBar);
        progressBar.setVisibility(View.VISIBLE);

        TextView alert = findViewById(R.id.room_list_alert);
        alert.setText(new String());

        boolean showAll = filterOptions[selectedItem].equals(this.getString(R.string.showall));

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

                    if(!isFull || showAll)
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void addRoomToList(String nomeHost, String idHost, String gameCode, boolean isFull){
        //FirebaseClass.deleteFieldFirebase(null, gameCode);

        LinearLayout scrollViewLayout = findViewById(R.id.room_list_scrollViewLayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.singleroom, null);

        ViewGroup parentView = view.findViewById(R.id.singleroom_parent);
        TextView hostName = view.findViewById(R.id.singleroom_hostName);
        ImageView hostImage = view.findViewById(R.id.singleroom_hostIcon);
        View joinBtn = view.findViewById(R.id.singleroom_joinIcon);
        TextView nPlayers = view.findViewById(R.id.singleroom_nPlayers);

        hostName.setText(nomeHost);
        setImgProfile(this, idHost, hostImage);

        Utility.ridimensionamento(this, parentView);
        scrollViewLayout.addView(view);

        ProgressBar progressBar = findViewById(R.id.room_list_loadingBar);
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
