package gameEngine;

import static gameEngine.Engine.*;
import static gameEngine.Game.*;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import androidx.annotation.RequiresApi;

import com.example.briscolav10.R;

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

        if(!Game.canPlay || terminata)
            return;

        final View destButton = carte[carta.getPortatore().index + I_CAMPO_GIOCO[0]];

        Game.canPlay = false;

        Object event = new Object();

        muoviCarta(bottone, destButton, false, true, event);

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();
                    activity.runOnUiThread(() -> {
                        Game.canPlay = true;

                        giocante.lancia(carta);
                        final Giocatore vincente = doLogic(carta, getOtherCarta(carta));

                        if(vincente == null) {
                            prossimoTurno(getOtherPlayer(giocante));
                        }else{
                            new Handler().postDelayed(() -> terminaManche(vincente), 1750);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }
}
