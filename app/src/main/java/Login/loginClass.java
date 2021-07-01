package Login;


import com.facebook.AccessToken;
import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;

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

    public static String getFBUserId()
    {
        return AccessToken.getCurrentAccessToken().getUserId();
    }

    public static void setImgProfile(ProfilePictureView img)
    {
        img.setProfileId(getFBUserId());
    }

    public static String getFBCognome()
    {
        return Profile.getCurrentProfile().getLastName();
    }

    public static String getFBNome()
    {
        return Profile.getCurrentProfile().getFirstName();
    }

}
