package gameEngine;


import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;

import static gameEngine.Game.*;
import static UI.CDialog.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import Home.MainMenu;
import Home.SharedPref;
import UI.CDialog;

import static gameEngine.Game.giocatori;

public class Settings extends AppCompatActivity {

    @SuppressLint("ResourceType")
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void createSettingsMenu(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE );

        // VIEW;
        View tipoCarteView = inflater.inflate( R.layout.spinner_tipo_carte, null );

        // SPINNER;
        Spinner tipoCarte = tipoCarteView.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item,c.getResources().getStringArray(R.array.tipoCarte));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCarte.setAdapter(adapter);

        // CHECKBOX;
        CheckBox carteScoperte = tipoCarteView.findViewById(R.id.checkbox);

        String selectedItem = SharedPref.getTipoCarte();
        List<String> opzioniTipo = Arrays.asList(c.getResources().getStringArray(R.array.tipoCarte));
        opzioniTipo = opzioniTipo.stream().map(String::toLowerCase).collect(Collectors.toList());

        int indexList = opzioniTipo.indexOf(selectedItem);
        tipoCarte.setSelection(indexList);

        carteScoperte.setChecked(SharedPref.getCarteScoperte());

        // CONFIRM;
        builder.setPositiveButton(c.getString(R.string.ok), (dialog, id) -> {
            if(carteScoperte.isChecked()){
                SharedPref.setCarteScoperte(true);
                if(Game.CPU != null)
                    Game.CPU.scopriCarte();
            }else{
                SharedPref.setCarteScoperte(false);
                if(Game.CPU != null)
                    Game.CPU.copriCarte();
            }

            // TODO: Nella modalità multiplayer, ricordarsi di bloccare l'ozione "carte scoperte";
            Engine.aggiornaTipoCarte(tipoCarte.getSelectedItem().toString());
        });

        builder.setNegativeButton(c.getString(R.string.cancel), null);
        builder.setView(tipoCarteView);

        builder.create().show();
    }

}
