package Home;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

import androidx.annotation.NonNull;
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
import gameEngine.Utility;
import multiplayer.RoomList;
import multiplayer.engineMultiplayer;
import okhttp3.internal.Util;

import static Login.loginClass.setImgProfile;
import static gameEngine.Game.activity;
import static gameEngine.Game.maxPunti;
import static gameEngine.Game.nGiocatori;

public class Storico extends Dialog{
    private static final String fileName = "storico.json";
    private static final String fileDir = "data";
    private AppCompatActivity context;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public Storico(@NonNull AppCompatActivity context){
        super(context);
        this.context = context;

        this.setContentView(R.layout.storico_partite);
        this.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        Utility.ridimensionamento(context, this.findViewById(R.id.storico_parent));

        initializeLayout();
        refreshMatches();

       // Utility.ridimensionamento(context, this.findViewById(R.id.storico_parent));

        this.create();
        this.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void refreshMatches(){
        TextView alert = findViewById(R.id.storico_alertStorico);
        alert.setText(new String());

        ViewGroup scrollView = findViewById(R.id.storico_scrollPartiteLayout);
        scrollView.removeAllViews();

        showMatches();
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void resetMatches(){
        Utility.writeFile(context, new String(), fileDir, fileName);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void initializeLayout(){
        TextView alert = findViewById(R.id.storico_alertStorico);
        alert.setText(new String());

/*        Button resetBtn = findViewById(R.id.storico_reset);

        resetBtn.setOnClickListener(v -> {
            if(!getMatches(context).isEmpty()){
                Utility.oneLineDialog(context, context.getString(R.string.resetconfirm), () -> {
                    resetMatches();
                    refreshMatches();
                });
            }
        });*/
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    protected void showMatches(){
        TextView alert = findViewById(R.id.storico_alertStorico);

        ArrayList<Partita> partite = getMatches(context);
        Collections.reverse(partite);

        if(!partite.isEmpty()) for(Partita p : partite)
            addMatchToList(p.getPunti(), p.getNomeAvversario(), p.getIdAvversario(), p.getData());
        else alert.setText(context.getString(R.string.nomatchfound));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void addMatchToList(int punti, String nomeAvversario, String idAvversario, String dataString){
        LinearLayout scrollViewLayout = findViewById(R.id.storico_scrollPartiteLayout);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.singlematch, null);

        ViewGroup parentView = view.findViewById(R.id.singlematch_parent);
        TextView description = view.findViewById(R.id.singlematch_description);
        TextView data = view.findViewById(R.id.singlematch_data);
        ImageView oppIcon = view.findViewById(R.id.singlematch_oppIcon);

        data.setText(dataString);

        @SuppressLint("DefaultLocale") String punteggio = String.format("%d %s", punti, context.getString(R.string.points));
        description.setText(String.format("%s: %s", nomeAvversario, punteggio));

        setImgProfile(context, idAvversario, oppIcon);

        boolean isVittoria = punti > maxPunti / nGiocatori;
        boolean isSconfitta = punti < maxPunti / nGiocatori;
        boolean isPareggio = punti == maxPunti / nGiocatori;

        int color = 0;
        if(isVittoria) color = UiColor.GREEN;
        else if(isSconfitta) color = UiColor.RED;
        else if(isPareggio) color = UiColor.YELLOW;

        description.setTextColor(color);

        Utility.ridimensionamento(context, parentView);
        scrollViewLayout.addView(view);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void addPartita(AppCompatActivity context, Partita lastMatch){
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
        }
        catch(JSONException e){
            e.printStackTrace();
        }

        return partite;
    }

    public static class Partita{
        private final Integer punti;
        private final String nomeAvversario, idAvversario;
        private final String data;

        public Partita(Integer punti, String nomeAvversario, String idAvversario, String data){
            this.punti = punti;
            this.nomeAvversario = nomeAvversario;
            this.idAvversario = idAvversario == null ? "null" : idAvversario;
            this.data = data;
        }

        public String getNomeAvversario(){
            return nomeAvversario;
        }

        public String getIdAvversario(){
            return idAvversario;
        }

        public Integer getPunti(){
            return punti;
        }

        public String getData(){
            return data;
        }
    }
}