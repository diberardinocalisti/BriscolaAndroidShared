package Login;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.math.BigInteger;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import Home.MainActivity;
import firebase.FirebaseClass;
import game.danielesimone.briscola.R;
import gameEngine.Game;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.EmailUser;

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

    public static String getFBUserId()
    {
        return isFacebookLoggedIn() ? AccessToken.getCurrentAccessToken().getUserId() : "null";
    }

    public static void setImgProfile(AppCompatActivity activity, String userId, ImageView imageIcon) {
        if(userId.equals("null"))
            return;

        new Thread(() -> {
            URL imageURL = null;
            try {
                imageURL = new URL("https://graph.facebook.com/" + userId + "/picture?type=large");
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

    public static boolean isUsernameOk(String username)
    {
        if(username.equals("null"))
            return false;

        if(username.contains(".") || username.contains("#") || username.contains("$") || username.contains("[") || username.contains("]"))
            return false;
        else
            return true;
    }

    public static void setEmailUser() {

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
}
