package gameEngine;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;

public class onClick implements View.OnClickListener {
    // @todo adattare il vecchio algoritmo;
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

        // @// TODO: 06/06/2021 problema in getOtherCarta, restituisce una carta diversa da quella che è effettivamente nel piano di gioco; 
        Giocatore vincente = doLogic(carta, getOtherCarta(carta));
        giocante.lancia(carta);

        if(vincente == null) {
            prossimoTurno(getOtherPlayer(giocante));
        }else{
            Giocatore finalVincente = vincente;
            try {
                terminaManche(finalVincente);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
