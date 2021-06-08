package gameEngine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import firebase.FirebaseClass;
import multiplayer.MultiplayerActivity;

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

    public static void createInputDialogMultiplayer(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        // Set other dialog properties
        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE );

        View tipoCarteView = inflater.inflate( R.layout.input_codice_stanza, null );

        EditText input = (EditText)  tipoCarteView.findViewById(R.id.inputCodice);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        FirebaseClass.getFbRefSpeicific(input.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                                if(dataSnapshot.exists())
                                    FirebaseClass.editFieldFirebase(input.getText().toString());
                                else
                                {
                                    goTo(c, MultiplayerActivity.class);
                                    Toast.makeText(c,"Errore! La stanza non esiste.",Toast.LENGTH_LONG).show();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

                            }

                        });
                    }
                }
        );

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
