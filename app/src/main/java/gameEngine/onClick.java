package gameEngine;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;
import android.os.Build;
import android.view.View;
import android.widget.Button;
import androidx.annotation.RequiresApi;

public class onClick implements View.OnClickListener {
    // @todo adattare il vecchio algoritmo;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Button bottone = (Button) v;
        Carta carta = Engine.getCartaFromButton(bottone);

        giocante.lancia(carta);

        /*if(carta == null)
            return;

        if(carta.getPortatore() != giocante)
            return;

        if(!Game.canPlay || terminata)
            return;

        giocante.lancia(carta);
        Giocatore vincente = doLogic(carta, getOtherCarta(carta));

        if(vincente == null) {
            prossimoTurno(getOtherPlayer(giocante));
        }else{
            Giocatore finalVincente = vincente;
            new Thread(() -> {
                try{
                    terminaManche(finalVincente);
                }catch(InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }
            }).start();
        }*/
    }
}
