package gameEngine;

import android.os.Build;
import android.view.View;
import android.widget.Button;

import androidx.annotation.RequiresApi;

import static gameEngine.Game.mazzo;

public class onClick implements View.OnClickListener {
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        Button bottone = (Button) v;

        // Carta carta = Engine.getCartaFromButton(bottone);

        Carta carta = mazzo.get(0);
        bottone.setBackground(carta.getImage());

        /*Carta carta = new Carta(null, null, null, null);

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
