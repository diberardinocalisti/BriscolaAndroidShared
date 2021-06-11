package gameEngine;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;

public class onClick implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Button bottone = (Button) v;
        Carta carta = Engine.getCartaFromButton(bottone);

        if(carta == null)
            return;

        if(carta.getPortatore() != giocante)
            return;

        if(!Game.canPlay || terminata)
            return;

        giocante.lancia(carta);
        final Giocatore vincente = doLogic(carta, getOtherCarta(carta));

        if(vincente == null) {
            prossimoTurno(getOtherPlayer(giocante));
        }else{
            canPlay = false;
            new Handler().postDelayed(() -> terminaManche(vincente), 1750);
        }
    }
}
