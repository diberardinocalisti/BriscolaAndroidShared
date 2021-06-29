package gameEngine;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.service.autofill.Dataset;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import org.w3c.dom.Text;

import Login.loginClass;
import firebase.FirebaseClass;
import multiplayer.Game.ActivityMultiplayerGame;
import multiplayer.MultiplayerActivity;
import multiplayer.engineMultiplayer;

public class Utility {


    public static void createDialog(Context c, String title, String msg){
        confirmDialog(c, title, msg, null, null);
    }

    public static void confirmDialog(Context c, String title, String message, DialogInterface.OnClickListener action, DialogInterface.OnCancelListener onCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(c.getString(R.string.ok), action);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void confirmDenyDialog(Context c, String title, String message, DialogInterface.OnClickListener action, DialogInterface.OnCancelListener onCancel){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton(c.getString(R.string.ok), action);
        builder.setNegativeButton(c.getString(R.string.cancel), null);
        builder.setOnCancelListener(onCancel);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void createInputDialogMultiplayer(Context c)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService( c.LAYOUT_INFLATER_SERVICE );
        View tipoCarteView = inflater.inflate( R.layout.input_codice_stanza, null );
        EditText input = tipoCarteView.findViewById(R.id.inputCodice);

        builder.setPositiveButton(c.getString(R.string.ok), (dialog, which) -> FirebaseClass.getFbRefSpeicific(input.getText().toString().toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                String host = "null";
                String enemy = "null";

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot d : dataSnapshot.getChildren())
                    {
                        String key = d.getKey();
                        Object value = d.getValue();

                        if(key.equals("host"))
                            host = String.valueOf(value);
                        if(key.equals("enemy"))
                            enemy = String.valueOf(value);

                    }

                    if(!host.equals("null") && !enemy.equals("null"))
                    {
                        Toast.makeText(c.getApplicationContext(), c.getText(R.string.roomfull), Toast.LENGTH_LONG).show();
                    }else
                    {
                        engineMultiplayer.codiceStanza = input.getText().toString();
                        engineMultiplayer.role = "NOTHOST";
                        FirebaseClass.editFieldFirebase(input.getText().toString(),"enemy", loginClass.getFBNome());
                        goTo(c, ActivityMultiplayerGame.class);
                    }

                }
                else
                {
                    Toast.makeText(c, c.getText(R.string.roomnotexisting), Toast.LENGTH_LONG).show();
                }

            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }

        })
        );

        builder.setNegativeButton(c.getString(R.string.cancel), null);
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

    // Ridimensiona i componenti in base alla dimensione dello schermo, NOTA: da utilizzare ogni qual volta si cambia la content view;
    public static void ridimensionamento(AppCompatActivity activity, ViewGroup v){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        final double baseHeight = 1920;
        double height = displayMetrics.heightPixels;

        for (int i = 0; i < v.getChildCount(); i++) {
            View vAtI = v.getChildAt(i);

            int curHeight = vAtI.getLayoutParams().height;
            int curWidth = vAtI.getLayoutParams().width;
            double rapporto = height/baseHeight;

            if(curHeight > ViewGroup.LayoutParams.MATCH_PARENT)
                vAtI.getLayoutParams().height = (int) (curHeight * rapporto);

            if(curWidth > ViewGroup.LayoutParams.MATCH_PARENT)
                vAtI.getLayoutParams().width = (int) (curWidth * rapporto);

            if(vAtI instanceof TextView){
                int curSize = (int) ((TextView) vAtI).getTextSize();
                int newSize = (int) (curSize * rapporto);

                ((TextView) vAtI).setTextSize(TypedValue.COMPLEX_UNIT_PX, newSize);
            }

            vAtI.requestLayout();

            if(vAtI instanceof ViewGroup){
                ridimensionamento(activity, (ViewGroup) vAtI);
            }
        }
    }
}
