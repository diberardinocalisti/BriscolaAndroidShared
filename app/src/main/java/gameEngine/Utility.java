package gameEngine;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import com.example.briscolav10.R;

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

    public static void goTo(Context c, Class cl)
    {
        Intent i = new Intent(c,cl);
        c.startActivity(i);
    }
}
