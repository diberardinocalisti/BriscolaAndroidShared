package Login;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import Home.Shop;
import UI.UiColor;
import game.danielesimone.briscola.GameActivity;
import game.danielesimone.briscola.R;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.ArrayList;

import Home.MainActivity;
import firebase.FirebaseClass;
import gameEngine.SharedPref;
import gameEngine.Utility;
import multiplayer.EmailUser;
import multiplayer.FbUser;

import static Login.loginClass.getFBUserId;
import static Login.loginClass.getFullName;
import static Login.loginClass.getId;
import static Login.loginClass.isFacebookLoggedIn;
import static Login.loginClass.isLoggedIn;
import static Login.loginClass.isUsernameLoggedIn;
import static firebase.FirebaseClass.isFirebaseStringValid;
import static java.lang.String.*;


public class LoginActivity extends GameActivity{
    public static AccessTokenTracker logoutTraker;
    public static String fbUID;
    public CallbackManager callbackManager;
    public static boolean login = false;
    public static Avatar selectedAvatar;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Utility.enableTopBar(this);

        if(!loginClass.isLoggedIn(this)){
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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

        loginFacebook.setOnClickListener(v -> {
            String title = this.getString(R.string.featurenotavailable);
            Utility.oneLineDialog(this, title, null);
        });

        loginUsername.setOnClickListener(v -> loginDialog());

        loginHelp.setOnClickListener(v -> {
            String title = this.getString(R.string.whylogin);
            String description = this.getString(R.string.whylogintext);
            Utility.createDialog(this, title, description);
        });

        back.setOnClickListener(v -> super.onBackPressed());

        loginFacebookHook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
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
                            FbUser user = new FbUser(0,0,25,"","");
                            FirebaseClass.addUserToFirebase(user, fbUID);
                        }else{
                            //Se l'utente c'?? nel db ma non  ha il campo monete glielo aggiungo
                            if(!task.getResult().hasChild("monete"))
                                loginClass.resetCoin(LoginActivity.this);
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


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void loginDialog(){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.login_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ViewGroup parentView = dialog.findViewById(R.id.login_parent);
        TextInputEditText usernameInput = dialog.findViewById(R.id.login_UsernameInput);
        TextInputEditText passwordInput = dialog.findViewById(R.id.login_PasswordInput);
        Button login = dialog.findViewById(R.id.login_Confirm);
        Button register = dialog.findViewById(R.id.login_Register);
        //TextView forgotPassword = dialog.findViewById(R.id.login_recoverPassword);
        ImageView close = dialog.findViewById(R.id.login_Close);

        login.setOnClickListener(v -> {
            String username = usernameInput.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();
            String hashPassword = loginClass.getMd5(password);

            if(!isFirebaseStringValid(username)){
                Utility.oneLineDialog(this, this.getString(R.string.loginerror), null);
                return;
            }

            // Accedi all'account gi?? esistente;
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
                                loginClass.updateAvatar();

                                Toast.makeText(getApplicationContext(), LoginActivity.this.getString(R.string.loginsuccess), Toast.LENGTH_SHORT).show();
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
            // Nel caso in cui un utente ha inserito i dati di accesso nella pagina di login ma deve ancora registrarsi,
            // ricordiamo i dati inseriti nella pagina di register in modo tale che non dovr?? reinserirli nuovamente;
            String previousUsernameInput = usernameInput.getText().toString().trim();
            String previousPasswordInput = passwordInput.getText().toString().trim();

            dialog.dismiss();
            registerDialog(previousUsernameInput, previousPasswordInput);
        });

/*        forgotPassword.setPaintFlags(forgotPassword.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        forgotPassword.setOnClickListener(v -> {
            // TODO: Recupera password;
        });*/

        close.setOnClickListener(v -> dialog.dismiss());

        Utility.ridimensionamento(this, parentView);
        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void registerDialog(String previousUsernameInput, String previousPasswordInput){
        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.register_username_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ViewGroup parentView = dialog.findViewById(R.id.register_parent);
        TextInputEditText usernameInput = dialog.findViewById(R.id.register_UsernameInput);
        TextInputEditText passwordInput = dialog.findViewById(R.id.register_PasswordInput);
        TextInputEditText emailInput = dialog.findViewById(R.id.register_EmailInput);
        Button register = dialog.findViewById(R.id.register_Confirm);
        ImageView close = dialog.findViewById(R.id.register_Close);

        usernameInput.setText(previousUsernameInput);
        passwordInput.setText(previousPasswordInput);

        selectedAvatar = null;
        ArrayList<Avatar> avatars = new ArrayList<>(Avatar.N_AVATAR);

        new Thread(() -> {
            showPremiumAvatars(dialog.findViewById(R.id.register_avatarScrollLayout), avatars);
            showFreeAvatars(dialog.findViewById(R.id.register_avatarScrollLayout), avatars);

            runOnUiThread(() -> {
                register.setOnClickListener(v -> {
                    String username = usernameInput.getText().toString().trim();
                    String password = passwordInput.getText().toString().trim();
                    String hashPassword = loginClass.getMd5(password);
                    String email = emailInput.getText().toString().trim().replace(".", "_"); // Firebase non accetta punti;

                    final int usernameRequiredLength = 3;
                    final int usernameMaxLength = 16;
                    if(username.length() < usernameRequiredLength || username.length() > usernameMaxLength){
                        String message = this.getString(R.string.usernamelength);
                        Utility.oneLineDialog(this, message, null);
                        return;
                    }

                    if(!isFirebaseStringValid(username)){
                        Utility.oneLineDialog(this, this.getString(R.string.usernameerror), null);
                        return;
                    }

                    String tempEmail = email.replace("_", ".");
                    boolean isValidEmail = (!TextUtils.isEmpty(tempEmail) && Patterns.EMAIL_ADDRESS.matcher(tempEmail).matches());
                    if(!isValidEmail || !isFirebaseStringValid(email)){
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

                    if(selectedAvatar == null){
                        String message = this.getString(R.string.erroravatar);
                        Utility.oneLineDialog(this, message, null);
                        return;
                    }

                    FirebaseClass.getFbRef().get().addOnCompleteListener(task -> {
                        boolean emailExists = false;

                        outerLoop: for(DataSnapshot d: task.getResult().getChildren()){
                            for(DataSnapshot row : d.getChildren()){
                                if(row.getKey().equals("email")){
                                    String emailChecked = valueOf(row.getValue());
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
                                    EmailUser emailUser = new EmailUser(0,0,50, selectedAvatar.getIdAvatar(), email,hashPassword);
                                    FirebaseClass.addUserToFirebase(emailUser, username);

                                    SharedPref.setUsername(username);
                                    SharedPref.setPassword(hashPassword);
                                    SharedPref.setEmail(email);
                                    SharedPref.setAvatar(selectedAvatar.getIdAvatar());

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

                Utility.ridimensionamento(this, parentView);
                dialog.show();
            });
        }).start();
    }

    protected void loginError(CharSequence msg){
        Intent i = new Intent(getApplicationContext(), LoginActivity.class);
        startActivity(i);
        Toast.makeText(getBaseContext(),msg,Toast.LENGTH_LONG).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void accountPage(){
        setContentView(R.layout.fb_profile);
        Utility.ridimensionamento(this, findViewById(R.id.parent));
        Utility.showAd(this);

        final int refreshRate = 100;

        // Dato che il listener onSuccess viene eseguito nonostante le informazioni del profilo non siano ancora state ancora
        // lette, aspetto finch?? getCurrentProfile restituisce il profilo dell'utente;
        new Thread(() -> {
            if(isFacebookLoggedIn()){
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

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void setStatistiche(){
        TextView nome = findViewById(R.id.nome);
        TextView accountId = findViewById(R.id.idValore);
        TextView nVittorie = findViewById(R.id.vittorieValore);
        TextView nSconfitte = findViewById(R.id.sconfitteValore);
        TextView nRateo = findViewById(R.id.rateoValore);
        ImageView imgProfile = findViewById(R.id.profilePicture);

        FirebaseClass.getFbRef().child(getId()).get().addOnCompleteListener(task -> {
            String nomeProfilo = getFullName();
            String accountIdText = isFacebookLoggedIn() ? getFBUserId() : SharedPref.getEmail().split("@")[0];

            nome.setText(nomeProfilo);
            accountId.setText(accountIdText);
            loginClass.setImgProfile(this, getId(), imgProfile);

            String vinte, perse;
            float rateo, vinteF = 0.0f, perseF = 0.0f;

            for(DataSnapshot d: task.getResult().getChildren())
            {
                if(d.getKey().equals("perse")){
                    perse = valueOf(d.getValue());
                    perseF = Float.parseFloat(perse);
                    nSconfitte.setText(valueOf(d.getValue()));
                }

                if(d.getKey().equals("vinte")) {
                    vinte = valueOf(d.getValue());
                    vinteF = Float.parseFloat(vinte);
                    nVittorie.setText(valueOf(d.getValue()));
                }
            }


            rateo = (vinteF != 0.0 && perseF != 0.0 ? vinteF/perseF : 0);

            if(perseF == 0.0)
                rateo = vinteF;

            rateo = (float) (Math.round(rateo*100.0)/100.0);

            nRateo.setText(valueOf(rateo));
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void setListeners(){
        Button modificaProfilo = findViewById(R.id.editB);
        modificaProfilo.setOnClickListener(v -> modificaProfilo());

        View.OnClickListener doLogout = null;
        if(isFacebookLoggedIn()){
            doLogout = (v) -> findViewById(R.id.logoutHook).performClick();
        }else if(loginClass.isUsernameLoggedIn(this)){
            doLogout = (v) -> logoutDialog();
        }

        findViewById(R.id.logout).setOnClickListener(doLogout);
        findViewById(R.id.logoutB).setOnClickListener(doLogout);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void modificaProfilo() {
        if(isFacebookLoggedIn()) {
            final String editProfileUrl = "https://www.facebook.com/profile.php";
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(editProfileUrl));
            startActivity(browserIntent);
            return;
        }

        Dialog dialog = new Dialog(this);

        dialog.setContentView(R.layout.edit_profile);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        ViewGroup parentView = dialog.findViewById(R.id.edit_parent);
        TextInputEditText editUsernameInput = dialog.findViewById(R.id.edit_UsernameInput);
        Button editProfileConfirm = dialog.findViewById(R.id.edit_ProfileConfirm);
        ImageView editProfileClose = dialog.findViewById(R.id.edit_ProfileClose);

        editUsernameInput.setText(SharedPref.getUsername());
        ArrayList<Avatar> avatars = new ArrayList<>(Avatar.N_AVATAR);

        selectedAvatar = null;

        Dialog loadingDialog = Utility.createLoadingDialog(this);

        new Thread(() -> {
            showPremiumAvatars(dialog.findViewById(R.id.edit_AvatarScrollLayout), avatars);
            showFreeAvatars(dialog.findViewById(R.id.edit_AvatarScrollLayout), avatars);

            runOnUiThread(() -> {
                loadingDialog.dismiss();

                for(Avatar avatar : avatars){
                    if(avatar.getIdAvatar().equals(SharedPref.getAvatar())){
                        avatar.getTextViewAvatar().setTextColor(UiColor.YELLOW);
                        selectedAvatar = avatar;
                        break;
                    }
                }

                editProfileConfirm.setOnClickListener(v2 -> {
                    if(!selectedAvatar.getIdAvatar().equals(SharedPref.getAvatar())) {
                        FirebaseClass.editFieldFirebase(getId(), "avatar", selectedAvatar.getIdAvatar());
                        SharedPref.setAvatar(selectedAvatar.getIdAvatar());

                        ImageView imgProfile = findViewById(R.id.profilePicture);
                        loginClass.getDrawableAvatar(getId(), drawableAvatar -> imgProfile.setImageDrawable((Drawable) drawableAvatar), this);
                    }

                    TextView nomeProfilo = findViewById(R.id.nome);
                    String newUsername = editUsernameInput.getText().toString();

                    if(!newUsername.equals(SharedPref.getUsername())){
                        FirebaseClass.getFbRef().addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                // Se esiste esiste gi?? un utente con il nuovo nome inserito;
                                if(snapshot.hasChild(newUsername)){
                                    Utility.oneLineDialog(LoginActivity.this, LoginActivity.this.getString(R.string.usernameexisting), null);
                                }else{
                                    FirebaseClass.getFbRef().child(getId()).get().addOnCompleteListener(task -> {
                                        if(task.isSuccessful()) {
                                            EmailUser emailUser = new EmailUser();

                                            for (DataSnapshot d : task.getResult().getChildren()) {
                                                String key = String.valueOf(d.getKey());
                                                String value = String.valueOf(d.getValue());

                                                switch(key){
                                                    case "vinte": emailUser.setVinte(Integer.parseInt(value)); break;
                                                    case "perse": emailUser.setPerse(Integer.parseInt(value)); break;
                                                    case "avatar": emailUser.setAvatar(value); break;
                                                    case "email": emailUser.setEmail(value); break;
                                                    case "password": emailUser.setPassword(value); break;
                                                }
                                            }

                                            FirebaseClass.deleteFieldFirebase(null, getId());

                                            fbUID = newUsername;
                                            SharedPref.setUsername(newUsername);
                                            FirebaseClass.addUserToFirebase(emailUser, newUsername);
                                            nomeProfilo.setText(newUsername);
                                        }
                                    });
                                }
                            }

                            @Override public void onCancelled(@NonNull @NotNull DatabaseError error) {}
                        });
                    }

                    dialog.dismiss();
                });

                editProfileClose.setOnClickListener(v3 -> dialog.dismiss());

                Utility.ridimensionamento(this, parentView);
                dialog.show();
            });
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showPremiumAvatars(ViewGroup gallery, ArrayList<Avatar> avatars){
        for(Avatar.PremiumAvatar avatar : Avatar.PREMIUM_AVATARS)
            mostraAvatarPremium(avatar, avatars, gallery);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void mostraAvatarPremium(Avatar.PremiumAvatar avatar, ArrayList<Avatar> avatars, ViewGroup gallery){
        String avatarName = avatar.getNomeAvatar();
        String avatarId = avatar.getIdAvatar();

        if(!isUsernameLoggedIn(this)){
            showAvatar(gallery, avatars, avatarId, avatarName, false);
        }else{
            Object locker = new Object();

            FirebaseClass.getFbRef().child(fbUID).addListenerForSingleValueEvent(new ValueEventListener(){
                @Override
                public void onDataChange(DataSnapshot snapshot){
                    EmailUser currUser = snapshot.getValue(EmailUser.class);
                    Class<?> userClass = currUser.getClass();

                    try{
                        Field selectedAvatarField = userClass.getDeclaredField(avatarId);
                        boolean hasUnlockedAvatar = selectedAvatarField.getBoolean(currUser);

                        showAvatar(gallery, avatars, avatarId, avatarName, hasUnlockedAvatar);

                        synchronized (locker){
                            locker.notifyAll();
                        }
                    }
                    catch(NoSuchFieldException | IllegalAccessException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onCancelled(@NonNull @NotNull DatabaseError error){}
            });

            try{
                synchronized (locker){
                    locker.wait();
                }
            }
            catch(InterruptedException e){
                e.printStackTrace();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void showFreeAvatars(ViewGroup gallery, ArrayList<Avatar> avatars){
        for (int i = 0; i < Avatar.N_AVATAR; i++){
            String avatarIdStr = "avatar_" + (i + 1);
            showAvatar(gallery, avatars, avatarIdStr, format("Avatar %d", (i + 1)), true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    protected void showAvatar(ViewGroup gallery, ArrayList<Avatar> avatars, String avatarIdStr, String avatarName, boolean isItemUnlocked){
        LayoutInflater inflater = LayoutInflater.from(this);

        int avatarId = this.getResources().getIdentifier(avatarIdStr, "drawable", this.getPackageName());

        Drawable avatarDrawable = this.getDrawable(avatarId);
        View view = inflater.inflate(R.layout.singleavatar, gallery, false);

        TextView avatarNameTextView = view.findViewById(R.id.avatarName);
        ImageView avatarImage = view.findViewById(R.id.avatarImage);

        avatarNameTextView.setText(avatarName);
        avatarImage.setImageDrawable(avatarDrawable);

        Avatar avatar = new Avatar(avatarName, avatarIdStr, avatarNameTextView, avatarImage);

        avatars.add(avatar);

        avatarImage.setOnClickListener(v1 -> {
            if(isItemUnlocked){
                for(Avatar a : avatars)
                    a.getTextViewAvatar().setTextColor(Color.WHITE);

                avatar.getTextViewAvatar().setTextColor(UiColor.YELLOW);
                selectedAvatar = avatar;
            }else{
                Utility.oneLineDialog(this, this.getString(R.string.visitshop), null);
            }
        });

        ImageView itemLockedIcon = view.findViewById(R.id.avatarLocked);
        itemLockedIcon.setVisibility(isItemUnlocked ? View.INVISIBLE : View.VISIBLE);

        gallery.addView(view);
    }

    protected void logoutDialog(){
        Utility.oneLineDialog(this, this.getString(R.string.confirmlogout), this::logout);
    }

    protected void logout(){
        if(isUsernameLoggedIn(this)){
            doLogout(this);
            logoutMsg();
        }
    }

    public static void doLogout(AppCompatActivity appCompatActivity){
        if(isUsernameLoggedIn(appCompatActivity, false)){
            SharedPref.setUsername("null");
            SharedPref.setPassword("null");
            SharedPref.setEmail("null");
            SharedPref.setAvatar("null");
            loginClass.fetchAndUpdateCoins(appCompatActivity);
        }
    }

    public void logoutMsg(){
        Toast.makeText(this, this.getString(R.string.logoutsuccess), Toast.LENGTH_SHORT).show();
        Utility.returnToMainMenu(this);
    }

    protected void handleLogout(){
        if(isFacebookLoggedIn()){
            // Creo un nuovo tracker solamente nel caso in cui non ne esista gi?? uno;
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

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        final Configuration override = new Configuration(newBase.getResources().getConfiguration());
        override.fontScale = 1.0f;
        applyOverrideConfiguration(override);
    }
}
