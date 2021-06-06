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
        boolean isLoggedIn = accessToken != null && !accessToken.isExpired();

        return isLoggedIn;
    }

    public static String getFBUserId()
    {
        AccessToken accessToken = AccessToken.getCurrentAccessToken();
        String userId = accessToken.getUserId();

        return userId;
    }

    public static void setImgProfile(ProfilePictureView img)
    {
        img.setProfileId(getFBUserId());
    }

    public static String getFBCognome()
    {
        Profile p = Profile.getCurrentProfile();
        return p.getLastName();
    }

    public static String getFBNome()
    {
        Profile p = Profile.getCurrentProfile();
        return p.getFirstName();
    }

}
