package gameEngine;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;

import UI.UiColor;
import game.danielesimone.briscola.R;
import multiplayer.RoomList;
import multiplayer.engineMultiplayer;

import static Login.loginClass.setImgProfile;
import static gameEngine.Game.activity;
import static gameEngine.Game.maxPunti;
import static gameEngine.Game.nGiocatori;

public class Storico extends AppCompatActivity {
    private static final String fileName = "storico.json";
    private static final String fileDir = "data";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"ResourceType", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.storico_partite);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.enableTopBar(this);

        initializeLayout();
        refreshMatches();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void refreshMatches(){
        TextView alert = findViewById(R.id.alertStorico);
        alert.setText(new String());

        ViewGroup scrollView = findViewById(R.id.scrollPartiteLayout);
        scrollView.removeAllViews();

        showMatches();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void resetMatches(){
        Utility.writeFile(this, new String(), fileDir, fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void initializeLayout(){
        TextView alert = findViewById(R.id.alertStorico);
        alert.setText(new String());

        Button resetBtn = findViewById(R.id.reset);

        resetBtn.setOnClickListener(v -> {
            if(!getMatches(this).isEmpty()){
                Utility.oneLineDialog(this, this.getString(R.string.resetconfirm), () -> {
                    resetMatches();
                    refreshMatches();
                });
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void showMatches(){
        TextView alert = findViewById(R.id.alertStorico);

        ArrayList<Partita> partite = getMatches(this);
        Collections.reverse(partite);

        if(!partite.isEmpty()){
            for(Partita p : partite)
                addMatchToList(p.getPunti(), p.getNomeAvversario(), p.getIdAvversario(), p.getData());
        }else{
            alert.setText(this.getString(R.string.nomatchfound));
        }
    }

    protected void addMatchToList(int punti, String nomeAvversario, String idAvversario, String dataString){
        LinearLayout scrollViewLayout = findViewById(R.id.scrollPartiteLayout);

        LayoutInflater inflater = LayoutInflater.from(this);
        View parentView = inflater.inflate(R.layout.singlematch, null);
        
        TextView description = parentView.findViewById(R.id.description);

        TextView data = parentView.findViewById(R.id.data);
        data.setText(dataString);

        ImageView oppIcon = parentView.findViewById(R.id.oppIcon);

        String punteggio = punti + " " + this.getString(R.string.points);
        description.setText(MessageFormat.format("{0}: {1}", nomeAvversario, punteggio));

        setImgProfile(this, idAvversario, oppIcon);

        boolean vittoria = punti > maxPunti/nGiocatori;
        description.setTextColor(vittoria ? UiColor.GREEN : UiColor.RED);

        scrollViewLayout.addView(parentView);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addPartita(AppCompatActivity context, int punti, String nomeAvversario, String idAvversario, String data){
        Partita lastMatch = new Partita(punti, nomeAvversario, idAvversario, data);
        ArrayList<Partita> matches = getMatches(context);
        matches.add(lastMatch);

        String jsonString = arrayListToJsonString(matches);
        Utility.writeFile(context, jsonString, fileDir, fileName);
    }

    protected static String arrayListToJsonString(ArrayList<Partita> arrayList){
        return new Gson().toJson(arrayList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected static ArrayList<Partita> getMatches(AppCompatActivity context){
        ArrayList<Partita> partite = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(Utility.readFile(context, fileDir, fileName));

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject obj = jsonArray.getJSONObject(i);

                int punti = Integer.parseInt(obj.getString("punti"));
                String data = obj.getString("data");
                String nomeAvversario = obj.getString("nomeAvversario");
                String idAvversario = obj.getString("idAvversario");

                partite.add(new Partita(punti, nomeAvversario, idAvversario, data));
            }

        }catch(JSONException e) {
            e.printStackTrace();
        }

        return partite;
    }

    public static class Partita {
        private final Integer punti;
        private final String nomeAvversario, idAvversario;
        private final String data;

        public Partita(Integer punti, String nomeAvversario, String idAvversario, String data){
            this.punti = punti;
            this.nomeAvversario = nomeAvversario;
            this.idAvversario = idAvversario == null ? "null" : idAvversario;
            this.data = data;
        }

        public String getNomeAvversario() {
            return nomeAvversario;
        }

        public String getIdAvversario() {
            return idAvversario;
        }

        public Integer getPunti() {
            return punti;
        }

        public String getData() {
            return data;
        }
    }
}
