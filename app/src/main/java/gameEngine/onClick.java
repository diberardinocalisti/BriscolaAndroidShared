package gameEngine;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;

import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;

public class onClick implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        final Button bottone = (Button) v;
        final Carta carta = Engine.getCartaFromButton(bottone);

        if(carta == null)
            return;

        if(carta.getPortatore() != giocante)
            return;

        if(Game.areActionsDisabled() || terminata)
            return;

        Game.disableActions();
        Game.cartaGiocata = true;

        Object event = new Object();

        final View destButton = carte[carta.getPortatore().index + I_CAMPO_GIOCO[lastManche][0]];

        muoviCarta(bottone, destButton, false, true, false, event);
        clearText(centerText);

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();
                    activity.runOnUiThread(() -> {
                        if(giocante == null)
                            return;

                        Game.enableActions();

                        giocante.lancia(carta);
                        final Giocatore vincente = doLogic(carta, getOtherCarta(carta));

                        if(vincente == null) {
                            prossimoTurno(getOtherPlayer(giocante));
                        }else{
                            giocante = null;
                            new Handler().postDelayed(() -> terminaManche(vincente), intermezzo);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
