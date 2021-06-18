package Home;

import android.content.Context;
import android.content.SharedPreferences;

import gameEngine.Game;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String CARTE_SCOP_ID = "CARTE_SCOPERTE";
    private static final String TIPO_CARTE_ID = "TIPO_CARTE";

    private static final String CARTE_DEFAULT = "napoletane";
    private static final boolean SCOPERTE_DEFAULT = false;

    public static boolean getCarteScoperte(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(CARTE_SCOP_ID, SCOPERTE_DEFAULT);
    }

    public static String getTipoCarte(Context c){
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(TIPO_CARTE_ID, CARTE_DEFAULT);
    }


    public static void setCarteScoperte(Context c, boolean flag){
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(CARTE_SCOP_ID, flag);
        editor.apply();
    }

    public static void setTipoCarte(Context c, String tipoCarte){
        SharedPreferences sharedPreferences = c.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TIPO_CARTE_ID, tipoCarte);
        editor.apply();
    }
}
