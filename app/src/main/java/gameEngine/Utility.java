package gameEngine;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.briscolav10.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.NotNull;

import Login.loginClass;
import firebase.FirebaseClass;
import multiplayer.Game.ActivityMultiplayerGame;
import multiplayer.engineMultiplayer;

import static gameEngine.Game.activity;
import static gameEngine.Game.textAnimDuration;

public class Utility {
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void enableTopBar(AppCompatActivity c){
        int resId = c.getResources().getIdentifier("topbar", "drawable", c.getPackageName());
        c.getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        c.getSupportActionBar().setBackgroundDrawable(c.getResources().getDrawable(resId));
        c.getSupportActionBar().setCustomView(R.layout.actionbar);

        String idS = "lefticon";
        int id = c.getResources().getIdentifier(idS, "id", c.getPackageName());
        c.findViewById(id).setOnClickListener(v -> c.onBackPressed());

        idS = "righticon";
        id = c.getResources().getIdentifier(idS, "id", c.getPackageName());
        c.findViewById(id).setOnClickListener(v -> new Settings().createSettingsMenu(c));
    }

    public static void oneLineDialog(Context c, String title, Runnable callback){
        new UI.CDialog((Activity) c, title, callback).show();
    }

    public static void createDialog(AppCompatActivity c, String title, String msg){
        Dialog dialog = new Dialog(c);

        dialog.setContentView(R.layout.text_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.titleDialog);
        TextView dialogText = dialog.findViewById(R.id.textDialog);

        dialogTitle.setText(title);
        dialogText.setText(msg);

        Button dialogOk = dialog.findViewById(R.id.okDialog);
        dialogOk.setOnClickListener(v -> dialog.dismiss());

        ImageView dialogClose = dialog.findViewById(R.id.closeDialog);
        dialogClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void createInputDialogMultiplayer(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);

        LayoutInflater inflater = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View tipoCarteView = inflater.inflate( R.layout.input_codice_stanza, null );
        EditText input = tipoCarteView.findViewById(R.id.inputCodice);

        builder.setPositiveButton(c.getString(R.string.ok), (dialog, which) -> FirebaseClass.getFbRefSpeicific(input.getText().toString().toUpperCase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {
                String host = "null";
                String enemy = "null";

                if(dataSnapshot.exists())
                {
                    for(DataSnapshot d : dataSnapshot.getChildren()){
                        String key = d.getKey();
                        Object value = d.getValue();

                        if(key.equals("host"))
                            host = String.valueOf(value);
                        if(key.equals("enemy"))
                            enemy = String.valueOf(value);
                    }

                    if(!host.equals("null") && !enemy.equals("null")){
                        Toast.makeText(c.getApplicationContext(), c.getText(R.string.roomfull), Toast.LENGTH_LONG).show();
                    }else{
                        engineMultiplayer.codiceStanza = input.getText().toString();
                        engineMultiplayer.role = "NOTHOST";
                        FirebaseClass.editFieldFirebase(input.getText().toString(),"enemy", loginClass.getFBNome());
                        goTo(c, ActivityMultiplayerGame.class);
                    }
                }else{
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

    public static void goTo(Context c, Class cl){
        goTo((AppCompatActivity) c, cl);
    }

    public static void goTo(AppCompatActivity c, Class cl){
        Intent i = new Intent(c,cl);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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

    public static void textAnimation(String msg, TextView view, Runnable callback){
        final long animSpeed = textAnimDuration;
        final float accelMultip = 2;
        final float diffY = 200;

        view.setText(msg);

        AnimationSet upAnim = new AnimationSet(false);
        AnimationSet downAnim = new AnimationSet(false);

        Animation fadeIn = new AlphaAnimation(0f, 1f);
        fadeIn.setInterpolator(new AccelerateInterpolator(accelMultip));
        fadeIn.setDuration(animSpeed);
        fadeIn.setFillAfter(false);

        Animation fadeOut = new AlphaAnimation(1f, 0f);
        fadeOut.setInterpolator(new AccelerateInterpolator(accelMultip));
        fadeOut.setDuration(animSpeed);
        fadeOut.setFillAfter(false);

        final Animation down = new TranslateAnimation(0, 0, 0, -diffY);
        down.setDuration(animSpeed);
        down.setFillAfter(false);
        down.setInterpolator(new AccelerateInterpolator(accelMultip));
        down.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationRepeat(Animation animation){}
            @Override public void onAnimationEnd(Animation animation) {
                callback.run();
            }
        });

        final Animation up = new TranslateAnimation(0, 0, diffY, 0);
        up.setDuration(animSpeed);
        up.setFillAfter(true);
        up.setInterpolator(new AccelerateInterpolator(accelMultip));
        up.setAnimationListener(new Animation.AnimationListener() {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationRepeat(Animation animation){}
            @Override public void onAnimationEnd(Animation animation) {
                new Thread(() -> {
                    try {
                        Thread.sleep(animSpeed * 2);
                        activity.runOnUiThread(() -> view.startAnimation(downAnim));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();

            }
        });

        upAnim.addAnimation(up);
        upAnim.addAnimation(fadeIn);

        downAnim.addAnimation(down);
        downAnim.addAnimation(fadeOut);

        view.startAnimation(upAnim);
    }
}
