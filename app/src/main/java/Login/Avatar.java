package Login;

import android.widget.ImageView;
import android.widget.TextView;

public class Avatar {
    public static final int N_AVATAR = 20;
    public static final PremiumAvatar[] PREMIUM_AVATARS = {
            new PremiumAvatar("Maestro", "avatar_21", 150),
            new PremiumAvatar("John Bick", "avatar_22", 150),
            new PremiumAvatar("Lio Nessi", "avatar_23", 150),
            new PremiumAvatar("D. Brump", "avatar_24", 150),
            new PremiumAvatar("Doge", "avatar_25", 150),
    };

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

    public static class PremiumAvatar{
        private String nomeAvatar, idAvatar;
        private int costoAvatar;

        private PremiumAvatar(String nomeAvatar, String idAvatar, int costoAvatar){
            this.nomeAvatar = nomeAvatar;
            this.idAvatar = idAvatar;
            this.costoAvatar = costoAvatar;
        }

        public String getNomeAvatar(){
            return nomeAvatar;
        }

        public void setNomeAvatar(String nomeAvatar){
            this.nomeAvatar = nomeAvatar;
        }

        public String getIdAvatar(){
            return idAvatar;
        }

        public void setIdAvatar(String idAvatar){
            this.idAvatar = idAvatar;
        }

        public int getCostoAvatar(){
            return costoAvatar;
        }

        public void setCostoAvatar(int costoAvatar){
            this.costoAvatar = costoAvatar;
        }
    }
}
