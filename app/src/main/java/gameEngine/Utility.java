package gameEngine;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Locale;

import Home.MainActivity;
import game.danielesimone.briscola.R;

import static gameEngine.Game.activity;
import static gameEngine.Game.textAnimDuration;

public class Utility{
    public static int randomIntRange(int startNum, int endNum){
        return (int) ((Math.random() * (endNum - startNum)) + startNum);
    }

    public static boolean isNumber(String number){
        try{
            Integer.parseInt(number);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public static void runnablePercentage(int percentage, Runnable callback){
        if(percentage > 100)
            percentage = 100;
        else if(percentage < 0)
            percentage = 0;

        final int numberPicked = (int) (Math.random() * 100);

        if(numberPicked <= percentage)
            callback.run();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String getTimeString(){
        String ora = Integer.toString(LocalDateTime.now().getHour());
        String minuti = Integer.toString(LocalDateTime.now().getMinute());

        if(minuti.length() == 1)
            minuti = String.format("0%s", minuti);

        if(ora.length() == 1)
            ora = String.format("0%s", ora);

        DateFormat formatoData = DateFormat.getDateInstance(DateFormat.SHORT, Locale.ITALY);
        String giorno = formatoData.format(new Date());

        return giorno + ", " + ora + ":" + minuti;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void writeFile(AppCompatActivity context, String content, String fileDir, String fileName){
        File externalFile = new File(context.getExternalFilesDir(fileDir), fileName);
        FileOutputStream fileOutputStream;

        try{
            fileOutputStream = new FileOutputStream(externalFile);
            fileOutputStream.write(content.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static String readFile(AppCompatActivity context, String fileDir, String fileName) {
        FileReader fr;
        File externalFile = new File(context.getExternalFilesDir(fileDir), fileName);
        StringBuilder fileString = new StringBuilder();
        try{
            fr = new FileReader(externalFile);
            BufferedReader bufferedReader = new BufferedReader(fr);
            String line = bufferedReader.readLine();
            while(line != null){
                fileString.append(line).append("\n");
                line = bufferedReader.readLine();
            }
        }catch(FileNotFoundException ignored){} catch(IOException ignored){}

        if(fileString.toString().isEmpty())
            return "[{}]";
        else
            return fileString.toString();
    }

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

    public static void showAd(AppCompatActivity appCompatActivity){
        AdView mAdView = appCompatActivity.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    // Input dialog;
    public static void inputDialog(Context c, String title, RunnablePar callback){
        Dialog dialog = new Dialog(c);

        dialog.setContentView(R.layout.input_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextView dialogTitle = dialog.findViewById(R.id.titleDialog);
        dialogTitle.setText(title);

        TextView dialogInput = dialog.findViewById(R.id.inputDialog);

        Runnable action = () -> {
            callback.run(dialogInput.getText().toString());
            dialog.dismiss();
        };

        Button dialogOk = dialog.findViewById(R.id.okDialog);
        dialogOk.setOnClickListener(v -> action.run());

        ImageView dialogClose = dialog.findViewById(R.id.closeDialog);
        dialogClose.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    public static void oneLineDialog(Context c, String title, Runnable callback){
        new UI.CDialog((Activity) c, title, callback).show();
    }

    public static void oneLineDialog(Context c, String title, String option1, String option2, Runnable firstCallback, Runnable secondCallback, Runnable dismissCallback){
        new UI.CDialog((Activity) c, title, option1, option2, firstCallback, secondCallback, dismissCallback).show();
    }

    public static void createDialog(Context c, String title, String msg){
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

    public static void goTo(AppCompatActivity c, Class cl){
        Intent i = new Intent(c,cl);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        c.startActivity(i);
    }

    public static void mainMenu(AppCompatActivity c){
        goTo(c, MainActivity.class);
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
