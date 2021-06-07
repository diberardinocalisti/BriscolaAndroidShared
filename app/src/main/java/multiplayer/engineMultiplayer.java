package multiplayer;

import java.util.Random;

public class engineMultiplayer {

    public static String creaStanza()
    {
        String codice = null;
        int len = 5;

        String chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        return sb.toString();
    }


}
