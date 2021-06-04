package gameEngine;

import android.app.AlertDialog;
import android.content.Context;

public class Utility {

    public static void createDialog(Context c, String title,String msg)
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(c);
        builder.setTitle(title);
        builder.setCancelable(true);
        builder.setMessage(msg);
        builder.show();
    }

}
