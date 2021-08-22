package gameEngine;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPref {
    private static Context context;

    private static final String SHARED_PREFS = "sharedPrefs";

    private static final String CARTE_SCOP_ID = "CARTE_SCOPERTE";
    private static final String TIPO_CARTE_ID = "TIPO_CARTE";
    private static final String CPU_SKILL_ID = "SKILL_CPU";
    private static final String USERNAME_ID = "USERNAME";
    private static final String PASSWORD_ID = "PASSWORD";

    private static final String CARTE_DEFAULT = "null";
    private static final boolean SCOPERTE_DEFAULT = false;
    private static final int CPU_SKILL_DEFAULT = -1;
    private static final String USERNAME_DEFAULT = "null";
    private static final String PASSWORD_DEFAULT = "null";

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

    public static int getCPUSkill(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getInt(CPU_SKILL_ID, CPU_SKILL_DEFAULT);
    }

    public static void setCPUSkill(int skillValue){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt(CPU_SKILL_ID, skillValue);
        editor.apply();
    }

    public static void setUsername(String username){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(USERNAME_ID, username);
        editor.apply();
    }

    public static String getUsername(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(USERNAME_ID, USERNAME_DEFAULT);
    }

    public static void setPassword(String password){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putString(PASSWORD_ID, password);
        editor.apply();
    }

    public static String getPassword(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        return sharedPreferences.getString(PASSWORD_ID, PASSWORD_DEFAULT);
    }

    public static void setContext(Context context){
        SharedPref.context = context;
    }
}
