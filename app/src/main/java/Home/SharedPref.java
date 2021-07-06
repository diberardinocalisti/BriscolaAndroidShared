package Home;

import android.content.Context;
import android.content.SharedPreferences;

import com.facebook.share.Share;

import gameEngine.Game;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    private static Context context;

    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String CARTE_SCOP_ID = "CARTE_SCOPERTE";
    private static final String TIPO_CARTE_ID = "TIPO_CARTE";

    private static final String CARTE_DEFAULT = "null";
    private static final boolean SCOPERTE_DEFAULT = false;

    public static boolean getCarteScoperte(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getBoolean(CARTE_SCOP_ID, SCOPERTE_DEFAULT);
    }

    public static String getTipoCarte(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(TIPO_CARTE_ID, CARTE_DEFAULT);
    }


    public static void setCarteScoperte(boolean flag){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(CARTE_SCOP_ID, flag);
        editor.apply();
    }

    public static void setTipoCarte(String tipoCarte){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(TIPO_CARTE_ID, tipoCarte);
        editor.apply();
    }

    public static void setContext(Context context){
        SharedPref.context = context;
    }
}
