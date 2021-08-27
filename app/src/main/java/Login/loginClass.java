package Login;


import android.annotation.SuppressLint;
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

public class loginClass {

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
    public static boolean isLoggedIn()
    {
        return loginClass.isFacebookLoggedIn() || loginClass.isUsernameLoggedIn();
    }

    public static String getName()
    {
         return (isFacebookLoggedIn() ? getFBNome() : SharedPref.getUsername());
    }

    public static boolean isUsernameLoggedIn(){
        return !SharedPref.getUsername().equals("null");
    }

    public static String getImageId(){
        return isFacebookLoggedIn() ? AccessToken.getCurrentAccessToken().getUserId() : SharedPref.getAvatar();
    }

    public static String getFBUserId()
    {
        return isFacebookLoggedIn() ? AccessToken.getCurrentAccessToken().getUserId() : "null";
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setImgProfile(AppCompatActivity activity, String imageId, ImageView imageIcon) {
        if(imageId == null)
            imageId = "avatar_" + Utility.randomIntRange(1, Avatar.N_AVATAR);

        // Se l'utente non è registrato a facebook userà l'avatar selezionato durante la registrazione;
        if(imageId.startsWith("avatar")){
            imageIcon.setImageDrawable(getAvatarByString(imageId, activity));
            return;
        }

        String finalImageId = imageId;
        new Thread(() -> {
            URL imageURL = null;
            try {
                imageURL = new URL("https://graph.facebook.com/" + finalImageId + "/picture?type=large");
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }

            Bitmap finalBitmap = bitmap;
            activity.runOnUiThread(() -> imageIcon.setImageBitmap(finalBitmap));
        }).start();

    }

    public static void getStringAvatar(RunnablePar callback){
        FirebaseClass.getFbRef().child(LoginActivity.fbUID).addListenerForSingleValueEvent(new ValueEventListener() {
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
    public static void getDrawableAvatar(RunnablePar callback, AppCompatActivity appCompatActivity){
        getStringAvatar(par -> callback.run(getAvatarByString((String) par, appCompatActivity)));
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

    public static boolean isFirebaseStringValid(String string)
    {
        string = string.trim();

        if(string.equals("null") || string.isEmpty() || string.contains(".")  || string.contains("#") || string.contains("$") || string.contains("[") || string.contains("]"))
            return false;
        else
            return true;
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

    public static boolean isUser(DataSnapshot d){
        return isFacebookUser(d) || isEmailUser(d);
    }

    public static boolean isFacebookUser(DataSnapshot d){
        return d.hasChild("nome");
    }

    public static boolean isEmailUser(DataSnapshot d){
        return d.hasChild("username");
    }
}
