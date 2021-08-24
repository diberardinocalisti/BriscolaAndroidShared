package Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import game.danielesimone.briscola.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.appevents.suggestedevents.ViewOnClickListener;
import com.facebook.login.Login;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.Share;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.EmailUser;
import multiplayer.RoomList;
import multiplayer.User;

import static multiplayer.GameRoom.isGameRoom;


public class LoginActivity extends AppCompatActivity {
    public static AccessTokenTracker logoutTraker;
    public static String fbUID;
    public CallbackManager callbackManager;
    public static boolean login = false;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utility.enableTopBar(this);

        if(!loginClass.isLoggedIn()){
            loginPage();
        }else{
            accountPage();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode , resultCode , data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    protected void loginPage(){
        setContentView(R.layout.login_page);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        callbackManager = CallbackManager.Factory.create();

        Button loginHelp = findViewById(R.id.login_help);
        Button loginFacebook = findViewById(R.id.login_facebook);
        Button loginUsername = findViewById(R.id.login_username);
        LoginButton loginFacebookHook = findViewById(R.id.login_hook);
        Button back = findViewById(R.id.login_back);

        loginFacebook.setOnClickListener(v -> loginFacebookHook.performClick());
        loginUsername.setOnClickListener(v -> loginDialog());

        loginHelp.setOnClickListener(v -> {
            String title = this.getString(R.string.whylogin);
            String description = this.getString(R.string.whylogintext);
            Utility.createDialog(this, title, description);
        });

        back.setOnClickListener(v -> super.onBackPressed());

        loginFacebookHook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken token = loginResult.getAccessToken();
                fbUID = token.getUserId();

                FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        boolean esiste = false;
                        for(DataSnapshot d: task.getResult().getChildren()){
                            if(d.getKey().equals(fbUID)){
                                esiste = true;
                                break;
                            }

                        }

                        if(!esiste) {
                            login = true;
                            User user = new User(0,0,"","");
                            FirebaseClass.addUserToFirebase(user, fbUID);
                        }
                    }
                });

                accountPage();
            }

            @Override
            public void onCancel() {
                loginError(LoginActivity.this.getString(R.string.unknownerror));
            }

            @Override
            public void onError(FacebookException e) {
                loginError(LoginActivity.this.getString(R.string.unknownerror));
            }
        });
    }


    protected void loginDialog(){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.login_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText usernameInput = dialog.findViewById(R.id.loginUsernameInput);
        TextInputEditText passwordInput = dialog.findViewById(R.id.loginPasswordInput);
        Button login = dialog.findViewById(R.id.loginConfirm);
        Button register = dialog.findViewById(R.id.loginRegister);
        TextView forgotPassword = dialog.findViewById(R.id.recoverPassword);

        ImageView close = dialog.findViewById(R.id.loginClose);

        login.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String hashPassword = loginClass.getMd5(password);

            // Accedi all'account già esistente;
            FirebaseClass.getFbRef().child(username).get().addOnCompleteListener(task -> {
                boolean entrato = false;

                if(task.isSuccessful()){
                    for(DataSnapshot d: task.getResult().getChildren()){
                        entrato = true;

                        Object value = d.getValue();
                        Object key = d.getKey();

                        if(key.equals("password")){
                            if(hashPassword.equals(value)){
                                LoginActivity.fbUID = username;
                                SharedPref.setUsername(username);
                                SharedPref.setPassword(hashPassword);
                                loginClass.updateEmail();

                                Toast.makeText(getApplicationContext(), LoginActivity.this.getString(R.string.registersuccess),Toast.LENGTH_SHORT).show();
                                accountPage();
                                break;
                            }else{
                                Utility.oneLineDialog(this, this.getString(R.string.loginerror), null);
                            }
                        }
                    }
                }

                if(!entrato)
                    Utility.oneLineDialog(this, this.getString(R.string.loginerror), null);

                dialog.dismiss();
            });

        });

        register.setOnClickListener(v -> {
            dialog.dismiss();
            registerDialog();
        });

        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPassword.setOnClickListener(v -> {
            // TODO: Recupera password;
        });

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    protected void registerDialog(){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.register_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        TextInputEditText usernameInput = dialog.findViewById(R.id.registerUsernameInput);
        TextInputEditText passwordInput = dialog.findViewById(R.id.registerPasswordInput);
        TextInputEditText emailInput = dialog.findViewById(R.id.registerEmailInput);
        Button register = dialog.findViewById(R.id.registerConfirm);
        ImageView close = dialog.findViewById(R.id.registerClose);

        register.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String hashPassword = loginClass.getMd5(password);
            String email = emailInput.getText().toString().trim().replace(".", "_"); // Firebase non accetta punti;

            final Integer usernameRequiredLength = 3;
            if(username.length() < usernameRequiredLength){
                String message = this.getString(R.string.usernamelength).replace("{length}", usernameRequiredLength.toString());
                Utility.oneLineDialog(this, message, null);
                return;
            }

            if(!loginClass.isFirebaseStringValid(username)){
                Utility.oneLineDialog(this, this.getString(R.string.usernameerror), null);
                return;
            }

            String tempEmail = email.replace("_", ".");
            boolean isValidEmail = (!TextUtils.isEmpty(tempEmail) && Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches());
            if(!isValidEmail || !loginClass.isFirebaseStringValid(email)){
                String message = this.getString(R.string.emailnotvalid);
                Utility.oneLineDialog(this, message, null);
                return;
            }

            final Integer passwordRequiredLength = 6;
            if(password.length() < passwordRequiredLength){
                String message = this.getString(R.string.passwordlength).replace("{length}", passwordRequiredLength.toString());
                Utility.oneLineDialog(this, message, null);
                return;
            }

            FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
                boolean emailExists = false;

                outerLoop: for(DataSnapshot d: task.getResult().getChildren()){
                    for(DataSnapshot row : d.getChildren()){
                        if(row.getKey().equals("email")){
                            String emailChecked = String.valueOf(row.getValue());
                            if(emailChecked.equals(email)) {
                                emailExists = true;
                                break outerLoop;
                            }
                        }
                    }
                }

                if(emailExists){
                    String message = this.getString(R.string.emailexisting);
                    Utility.oneLineDialog(this, message, null);
                    return;
                }

                FirebaseClass.getFbRef().addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if(snapshot.hasChild(username)){
                            Utility.oneLineDialog(LoginActivity.this, LoginActivity.this.getString(R.string.usernameexisting), null);
                        }else{
                            EmailUser emailUser = new EmailUser(0,0, email,hashPassword);
                            FirebaseClass.addUserToFirebase(emailUser, username);

                            SharedPref.setUsername(username);
                            SharedPref.setPassword(hashPassword);
                            SharedPref.setEmail(email);
                            LoginActivity.fbUID = username;

                            Toast.makeText(getApplicationContext(), LoginActivity.this.getString(R.string.registersuccess),Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                            accountPage();
                        }
                    }

                    @Override public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                });
            });
        });

        close.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    protected void loginError(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    protected void accountPage(){
        setContentView(R.layout.fb_profile);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        final int refreshRate = 100;

        // Dato che il listener onSuccess viene eseguito nonostante le informazioni del profilo non siano ancora state ancora
        // lette, aspetto finché getCurrentProfile restituisce il profilo dell'utente;
        new Thread(() -> {
            if(loginClass.isFacebookLoggedIn()){
                while(Profile.getCurrentProfile() == null) {
                    try {
                        Thread.sleep(refreshRate);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }

            LoginActivity.this.runOnUiThread(() -> {
                setStatistiche();
                setListeners();
                handleLogout();
            });
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }

    protected void setStatistiche(){
        TextView nome = findViewById(R.id.nome);
        TextView accountId = findViewById(R.id.idValore);

        String nomeProfilo = loginClass.isFacebookLoggedIn() ? loginClass.getFullFBName() : loginClass.getName();

        nome.setText(nomeProfilo);

        if(loginClass.isUsernameLoggedIn()){
            accountId.setText(SharedPref.getEmail().split("@")[0]);
        }else if (loginClass.isFacebookLoggedIn()) {
            accountId.setText(loginClass.getFBUserId());
        }

        ImageView imgProfile = findViewById(R.id.friendProfilePicture);
        loginClass.setImgProfile(this, loginClass.getFBUserId(), imgProfile);

        TextView nVittorie = findViewById(R.id.vittorieValore);
        TextView nSconfitte = findViewById(R.id.sconfitteValore);
        TextView nRateo = findViewById(R.id.rateoValore);

        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String vinte, perse;
                float rateo, vinteF = 0.0f, perseF = 0.0f;

                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("perse")){
                        perse = String.valueOf(d.getValue());
                        perseF = Float.parseFloat(perse);
                        nSconfitte.setText(String.valueOf(d.getValue()));
                    }

                    if(d.getKey().equals("vinte")) {
                        vinte = String.valueOf(d.getValue());
                        vinteF = Float.parseFloat(vinte);
                        nVittorie.setText(String.valueOf(d.getValue()));
                    }
                }


                rateo = (vinteF != 0.0 && perseF != 0.0 ? vinteF/perseF : 0);

                if(perseF == 0.0)
                    rateo = vinteF;

                rateo = (float) (Math.round(rateo*100.0)/100.0);

                nRateo.setText(String.valueOf(rateo));
            }
        });
    }

    protected void setListeners(){
        Button modificaProfilo = findViewById(R.id.editB);
        modificaProfilo.setOnClickListener(v -> {
            if(loginClass.isFacebookLoggedIn()){
                final String editProfileUrl = "https://www.facebook.com/profile.php";
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(editProfileUrl));
                startActivity(browserIntent);
            }else{
                String message = this.getString(R.string.erroredit);
                Utility.oneLineDialog(this, message, null);
            }
        });

        View.OnClickListener doLogout = null;
        if(loginClass.isFacebookLoggedIn()){
            doLogout = (v) -> findViewById(R.id.logoutHook).performClick();
        }else if(loginClass.isUsernameLoggedIn()){
            doLogout = (v) -> {
                Utility.oneLineDialog(this, this.getString(R.string.confirmlogout), () -> {
                    SharedPref.setUsername("null");
                    SharedPref.setPassword("null");
                    Toast.makeText(getApplicationContext(), getString(R.string.logoutsuccess), Toast.LENGTH_SHORT).show();
                    Utility.goTo(LoginActivity.this, MainActivity.class);
                });
            };
        }

        findViewById(R.id.logout).setOnClickListener(doLogout);
        findViewById(R.id.logoutB).setOnClickListener(doLogout);
    }

    protected void handleLogout(){
        if(loginClass.isFacebookLoggedIn()){
            // Creo un nuovo tracker solamente nel caso in cui non ne esista già uno;
            if(logoutTraker == null) {
                logoutTraker = new AccessTokenTracker() {
                    @Override
                    protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                        if (currentAccessToken == null) {
                            LoginManager.getInstance().logOut();
                            Toast.makeText(getApplicationContext(), getString(R.string.logoutsuccess), Toast.LENGTH_SHORT).show();
                            Utility.goTo(LoginActivity.this, MainActivity.class);
                        }
                    }
                };
            }
        }
    }
}
