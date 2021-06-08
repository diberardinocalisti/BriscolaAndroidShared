package gameEngine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.briscolav10.R;

import firebase.FirebaseClass;

public class Utility {

    public static void createDialog(Context c, String title,String msg){
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(msg);
        builder.show();
    }

    public static void confirmDialog(Context c, String title, String message, DialogInterface.OnClickListener action, DialogInterface.OnCancelListener onCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("Ok", action);
        builder.setOnCancelListener(onCancel);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void createInputDialog(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        // Set other dialog properties
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE );

        View tipoCarteView = inflater.inflate( R.layout.input_codice_stanza, null );

        EditText input = (EditText)  tipoCarteView.findViewById(R.id.inputCodice);

        builder.setPositiveButton("OK", (dialog, id) -> {
            Toast.makeText(c,"Exists --> " + FirebaseClass.hasRoom(c,String.valueOf(input.getText())),Toast.LENGTH_LONG).show();
        });

        builder.setNegativeButton("ANNULLA", null);
        builder.setView(tipoCarteView);

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    public static void goTo(Context c, Class cl)
    {
        Intent i = new Intent(c,cl);
        c.startActivity(i);
    }
}
