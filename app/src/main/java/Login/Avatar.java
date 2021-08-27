package Login;

import android.widget.ImageView;
import android.widget.TextView;

public class Avatar {
    public static final int N_AVATAR = 20;

    private final String nomeAvatar;
    private final String idAvatar;
    private final TextView textViewAvatar;
    private final ImageView imageViewAvatar;

    public Avatar(String nomeAvatar, String idAvatar, TextView textViewAvatar, ImageView imageViewAvatar){
        this.nomeAvatar = nomeAvatar;
        this.idAvatar = idAvatar;
        this.textViewAvatar = textViewAvatar;
        this.imageViewAvatar = imageViewAvatar;
    }

    public String getNomeAvatar() {
        return nomeAvatar;
    }

    public String getIdAvatar() {
        return idAvatar;
    }

    public TextView getTextViewAvatar() {
        return textViewAvatar;
    }

    public ImageView getImageViewAvatar() {
        return imageViewAvatar;
    }
}
