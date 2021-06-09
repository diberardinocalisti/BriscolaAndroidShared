package gameEngine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.briscolav10.ActivityGame;
import com.example.briscolav10.R;

import static gameEngine.Game.*;

import java.util.ArrayList;
import java.util.List;

import Home.MainMenu;

import static gameEngine.Game.giocatori;

public class Settings extends AppCompatActivity {


    private boolean checked = false;

    public void createSettingsMenu(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        // Set other dialog properties
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE );

        View tipoCarteView = inflater.inflate( R.layout.spinner_tipo_carte, null );

        Spinner tipoCarte = tipoCarteView.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item,c.getResources().getStringArray(R.array.tipoCarte));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCarte.setAdapter(adapter);

        CheckBox carteScoperte = tipoCarteView.findViewById(R.id.checkbox);

        carteScoperte.setChecked(MainMenu.carteScoperte);
        builder.setPositiveButton("OK", (dialog, id) -> {
            if(carteScoperte.isChecked()){
                MainMenu.carteScoperte = true;
            }else{
                MainMenu.carteScoperte = false;
            }
        });

        builder.setNegativeButton("ANNULLA", null);
        builder.setView(tipoCarteView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
