package multiplayer;

import android.os.Build;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import firebase.FirebaseClass;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Giocatore;
import multiplayer.Game.ActivityMultiplayerGame;

import static gameEngine.Engine.getCartaFromName;
import static multiplayer.engineMultiplayer.DELIMITER;
import static multiplayer.engineMultiplayer.arrayListToString;
import static multiplayer.engineMultiplayer.codiceStanza;
import static multiplayer.Game.ActivityMultiplayerGame.*;
import static multiplayer.engineMultiplayer.removeCardFromMazzo;

public class GiocatoreMP extends Giocatore {
    public GiocatoreMP(String player, int i) {
        super(player, i);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void pesca() {
        String carteDisponibili = mazzoOnline;
        String[] strTok = carteDisponibili.split(DELIMITER);

        String cartaPescata = strTok[0];

        removeCardFromMazzo(cartaPescata);

        FirebaseClass.editFieldFirebase(codiceStanza, "carteRimanenti", mazzoOnline);
        snapshot.setCarteRimanenti(mazzoOnline);

        super.pesca(getCartaFromName(cartaPescata));
    }
}
