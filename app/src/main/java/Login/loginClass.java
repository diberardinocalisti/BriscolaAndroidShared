package Login;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.share.Share;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import firebase.FirebaseClass;
import gameEngine.RunnablePar;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.User;

import static Login.LoginActivity.fbUID;

public class loginClass {
    public static final int DEFAULT_COIN = 50;
    public static final int WIN_COIN = 25;
    public static final int LOSE_COIN = -10;

    /**
     *
     * @return true sel'utente è già loggato in facebook, else altrimenti
     */
    public static boolean isFacebookLoggedIn()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        return accessToken != null && !accessToken.isExpired();
    }

    /**
     *
     * @return true se l'utente è loggato con un metodo di accesso, false altrimenti
     */
    public static boolean isLoggedIn(AppCompatActivity appCompatActivity)
    {
        return (loginClass.isFacebookLoggedIn() || loginClass.isUsernameLoggedIn(appCompatActivity)) && Utility.isNetworkAvailable(appCompatActivity);
    }

    public static String getName()
    {
         return (isFacebookLoggedIn() ? getFBNome() : SharedPref.getUsername());
    }

    public static String getFullName(){
        return (isFacebookLoggedIn() ? getFullFBName() : SharedPref.getUsername());
    }

    public static String getId(){
        if(fbUID == null)
            return isFacebookLoggedIn() ? getFBUserId() : SharedPref.getUsername();
        else return fbUID;
    }

    public static boolean isUsernameLoggedIn(AppCompatActivity a){
        return isUsernameLoggedIn(a, true);
    }

    public static boolean isUsernameLoggedIn(AppCompatActivity a, boolean internet){
        return !SharedPref.getUsername().equals("null") && (Utility.isNetworkAvailable(a) || !internet);
    }

    public static String getFBUserId()
    {
        return isFacebookLoggedIn() ? AccessToken.getCurrentAccessToken().getUserId() : "null";
    }

    public static String getUsernameId(){
        return SharedPref.getUsername();
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setImgProfile(AppCompatActivity activity, String userId, ImageView imageIcon) {
        if(userId == null) {
            String imageId = "avatar_" + Utility.randomIntRange(1, Avatar.N_AVATAR);
            imageIcon.setImageDrawable(getAvatarByString(imageId, activity));
            return;
        }

        facebookUserCallbacks(userId,
                // Se l'utente è registrato a facebook
                new Thread(() -> {
                    URL imageURL = null;
                    try{
                        imageURL = new URL("https://graph.facebook.com/" + userId + "/picture?type=large");
                    }
                    catch(MalformedURLException e){
                        e.printStackTrace();
                    }
                    Bitmap bitmap = null;
                    try{
                        bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
                    }
                    catch(IOException e){
                        e.printStackTrace();
                    }

                    Bitmap finalBitmap = bitmap;
                    activity.runOnUiThread(() -> imageIcon.setImageBitmap(finalBitmap));
                })::start,

                // Se l'utente è registrato con email;
                () -> {
                    getDrawableAvatar(userId, drawableAvatar -> imageIcon.setImageDrawable((Drawable) drawableAvatar), activity);
                }
        );
    }

    public static void getStringAvatar(String userId, RunnablePar callback){
        FirebaseClass.getFbRef().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren()){
                    if(d.getKey().equals("avatar")){
                        callback.run(String.valueOf(d.getValue()));
                        return;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error){}
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void getDrawableAvatar(String userId, RunnablePar callback, AppCompatActivity appCompatActivity){
        // Ottiene la stringa dell'avatar memorizzata nel database;
        getStringAvatar(userId, par -> {
            callback.run(
                    // Ottiene il drawable dalla stringa memorizzata nel database e la passa come parametro alla callback;
                    getAvatarByString((String) par, appCompatActivity)
            );
        });
    }

    public static Drawable getAvatarByString(String avatarName, AppCompatActivity appCompatActivity){
        int avatarId = appCompatActivity.getResources().getIdentifier(avatarName, "drawable", appCompatActivity.getPackageName());
        return appCompatActivity.getDrawable(avatarId);
    }

    public static String getFBCognome()
    {
        return Profile.getCurrentProfile().getLastName();
    }

    public static String getFBNome()
    {
        return Profile.getCurrentProfile().getFirstName();
    }

    public static String getFullFBName(){
        return getFBNome() + " " + getFBCognome();
    }

    public static String getMd5(String input)
    {
        try {

            // Static getInstance method is called with hashing MD5
            MessageDigest md = MessageDigest.getInstance("MD5");

            // digest() method is called to calculate message digest
            //  of an input digest() return array of byte
            byte[] messageDigest = md.digest(input.getBytes());

            // Convert byte array into signum representation
            BigInteger no = new BigInteger(1, messageDigest);

            // Convert message digest into hex value
            String hashtext = no.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        }

        // For specifying wrong message digest algorithms
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static void updateAvatar(){
        FirebaseClass.getFbRef().child(LoginActivity.fbUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren())
                {
                    if(d.getKey().equals("avatar"))
                    {
                        SharedPref.setAvatar(String.valueOf(d.getValue()));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static void fetchAndUpdateCoins(AppCompatActivity appCompatActivity){
        fetchAndUpdateCoins(appCompatActivity, () -> {});
    }

    public static void fetchAndUpdateCoins(AppCompatActivity appCompatActivity, Runnable callback){
        if(!isLoggedIn(appCompatActivity)){
            SharedPref.setCoin(0);
            callback.run();
        }else{
            FirebaseClass.getFbRefSpeicific(fbUID).get().addOnCompleteListener(task -> {
                if(!task.getResult().hasChild("monete")){
                    loginClass.resetCoin(appCompatActivity);
                }else{
                    int coin = task.getResult().getValue(User.class).getMonete();
                    SharedPref.setCoin(coin);
                }
                callback.run();
            });
        }

    }

    public static void facebookUserCallbacks(String userId, Runnable onConfirmCallback, Runnable onDenyCallback){
        if(userId == null){
            onDenyCallback.run();
            return;
        }

        FirebaseClass.getFbRef().child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                // Se è un utente Facebook;
                if(snapshot.hasChild("nome") && snapshot.hasChild("cognome")) {
                    onConfirmCallback.run();
                }else {
                    onDenyCallback.run();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error){}
        });
    }

    public static void updateEmail() {
        FirebaseClass.getFbRef().child(LoginActivity.fbUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                for(DataSnapshot d : snapshot.getChildren())
                {
                    if(d.getKey().equals("email"))
                    {
                        SharedPref.setEmail(String.valueOf(d.getValue()));
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }

    public static void resetCoin(AppCompatActivity a){
        setCoin(DEFAULT_COIN, a);
    }

    public static void setCoin(int coin, AppCompatActivity a){
        if(isUsernameLoggedIn(a)){
            FirebaseClass.editFieldFirebase(fbUID,"monete", coin);
            SharedPref.setCoin(coin);
        }
    }

    public static boolean isUser(DataSnapshot d){
        return isFacebookUser(d) || isEmailUser(d);
    }

    public static boolean isFacebookUser(DataSnapshot d){
        return d.hasChild("nome");
    }

    public static boolean isEmailUser(DataSnapshot d){
        return d.hasChild("email");
    }

/*    public static void showAds(Runnable callback, AppCompatActivity appCompatActivity)
    {
        if(!isLoggedIn(appCompatActivity)){
            callback.run();
            return;
        }

        FirebaseClass.getFbRef().child(LoginActivity.fbUID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if(!snapshot.hasChild("removeAd"))
                {
                    callback.run();
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }*/
}
