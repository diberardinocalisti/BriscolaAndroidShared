package gameEngine;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;

public class Settings extends AppCompatActivity {


    public void createSettingsMenu(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        // Add the buttons
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked OK button
                    }
                });
                builder.setNegativeButton("Esci", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                    }
                });
        // Set other dialog properties
        View mView = getLayoutInflater().inflate(R.layout.spinner_tipo_carte,null);
        Spinner tipoCarte = (Spinner) mView.findViewById(R.id.spinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(c, android.R.layout.simple_spinner_item,getResources().getStringArray(R.array.tipoCarte));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        tipoCarte.setAdapter(adapter);

        // Create the AlertDialog
        builder.setView(mView);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
