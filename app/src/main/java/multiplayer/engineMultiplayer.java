package multiplayer;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

import java.sql.Time;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import Login.loginClass;
import firebase.FirebaseClass;
import game.danielesimone.briscola.ActivityGame;
import game.danielesimone.briscola.R;
import game.danielesimone.briscola.Storico;
import gameEngine.Carta;
import gameEngine.Engine;
import gameEngine.Game;
import gameEngine.Giocatore;
import gameEngine.Utility;

import static game.danielesimone.briscola.ActivityGame.leftGame;
import static gameEngine.Game.I_CAMPO_GIOCO;
import static gameEngine.Game.activity;
import static gameEngine.Game.canPlay;
import static gameEngine.Game.centerText;
import static gameEngine.Game.giocante;
import static gameEngine.Game.giocatori;
import static gameEngine.Game.intermezzo;
import static gameEngine.Game.lastManche;
import static multiplayer.ActivityMultiplayerGame.mazzoOnline;
import static multiplayer.ActivityMultiplayerGame.onStop;
import static multiplayer.ActivityMultiplayerGame.snapshot;


public class engineMultiplayer extends Engine{
    public static String codiceStanza;
    public static String role;
    public static final String MAZZO_DELIMETER = ";";
    public static final String CHAT_DELIMETER = "§";

    public static Dialog chat;
    public static Giocatore host, enemy;

    public static void creaStanza(AppCompatActivity c){
        int len = 5;

        String chars = "123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random rnd = new Random();
        StringBuilder sb = new StringBuilder(len);

        for (int i = 0; i < len; i++)
            sb.append(chars.charAt(rnd.nextInt(chars.length())));

        role = "HOST";
        codiceStanza = sb.toString();
        accediHost(c, sb.toString());
    }


    public static void accediHost(AppCompatActivity c, String gameCode){
        GameRoom g = new GameRoom(gameCode, loginClass.getName(), "null", loginClass.getId(), "null","null","null","null", "null");
        FirebaseClass.addToFirebase(g);

        Intent i = new Intent(c, ActivityGame.class);
        i.putExtra("multiplayer",true);
        c.startActivity(i);
        c.finish();
    }

    public static void accediGuest(AppCompatActivity c, String input){
        engineMultiplayer.codiceStanza = input;
        engineMultiplayer.role = "NOTHOST";
        ActivityGame.multiplayer = true;
        FirebaseClass.editFieldFirebase(input,"enemy", loginClass.getName());
        FirebaseClass.editFieldFirebase(input,"idEnemy", loginClass.getId());
        Utility.goTo(c, ActivityMultiplayerGame.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void inizializza() {
        creaGiocatori();
        Engine.pulisciTavolo();
        Engine.pulisciPrese();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void checkIfSomeoneLeft(){
        String host = snapshot.getHost();
        String enemy = snapshot.getEnemy();

        if(host.equals("null") && !enemy.equals("null")){
            returnToMainMenu();

            if(role.equals("HOST")) {
                youLeft();
            }else{
                opponentLeft();
            }
        }else if(enemy.equals("null") && !host.equals("null")) {
            returnToMainMenu();

            if(!role.equals("HOST")){
                youLeft();
            }else{
                opponentLeft();
            }
        }
    }

    public static void youLeft(){
        Toast.makeText(activity, activity.getString(R.string.youleft), Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void opponentLeft(){
        Toast.makeText(activity, activity.getString(R.string.enemyleft), Toast.LENGTH_SHORT).show();

        //Aggiorno la sconfitta di chi ha abbandonato la partita;
        FirebaseClass.aggiornaSconfitte(Game.opp.getId());
        //Aggiungo una vittoria;
        FirebaseClass.aggiornaVittorie(Game.user.getId());
    }

    public static void returnToMainMenu(){
        onStop = true;
        if(!leftGame){
            leftGame = true;
            Utility.mainMenu(activity);
            activity.finish();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String creaMazzoFirebase(){
        String mazzoFb = new String();
        Engine.creaMazzo();

        for(Carta c : Game.mazzo)
            mazzoFb += c.getNome() + MAZZO_DELIMETER;

        return mazzoFb.substring(0, mazzoFb.length()-1);
   }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void creaGiocatori() {
        String[] players = new String[]{activity.getString(R.string.guest), activity.getString(R.string.guest)};

        for(int i = 0; i < Game.nGiocatori; i++)
            Game.giocatori[i] = new GiocatoreMP(players[i], i);

        Game.user = giocatori[1];
        Game.opp = giocatori[0];

        if(role.equals("HOST")) {
            host = Game.user;
            enemy = Game.opp;

            ((GiocatoreMP) Game.user).setRuolo("host");
            ((GiocatoreMP) Game.opp).setRuolo("enemy");
        }else{
            host = Game.opp;
            enemy = Game.user;

            ((GiocatoreMP) Game.user).setRuolo("enemy");
            ((GiocatoreMP) Game.opp).setRuolo("host");
        }

        host.setId(snapshot.getIdHost());
        enemy.setId(snapshot.getIdEnemy());

        host.updateIcon();
        enemy.updateIcon();

        host.setNome(snapshot.getHost());
        enemy.setNome(snapshot.getEnemy());

        engineMultiplayer.setOnCLickListener();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void checkIfCartaGiocata(){
        new Thread(() -> {
            // Ad ora non ho trovato una soluzione migliore per far aspettare che tutte le animazioni siano
            // terminate prima di eseguire il listener di firebase;
            while(!canPlay) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            String turno = ((GiocatoreMP) giocante).getRuolo();
            String app = (turno.equals("enemy") ? snapshot.getGiocataDaEnemy() : snapshot.getGiocataDaHost());

            if(app.equals("null"))
                return;

            final String separator = "#";
            String nome = app.split(separator)[0];
            int indice = Integer.parseInt(app.split(separator)[1]);

            Carta c = Engine.getCartaFromName(nome);

            View daMuovere = c.getPortatore().getBottoni()[indice];

/*            if(!daMuovere.isEnabled())
                return;*/

            c.setButton(daMuovere);

            Object event = new Object();
            activity.runOnUiThread(() -> {
                clearText(centerText);
                muoviCarta(daMuovere, Game.carte[c.getPortatore().getIndex() + I_CAMPO_GIOCO[lastManche][0]], c,false, true, false, event);
                giocaCarta(c, event);
            });
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void giocaCarta(Carta c, Object event){
        Game.canPlay = false;
        Game.cartaGiocata = true;

        new Thread(() -> {
            try {
                synchronized (event){
                    event.wait();

                    activity.runOnUiThread(() -> {
                        c.getPortatore().lancia(c);

                        final Giocatore vincente = doLogic(c, getOtherCarta(c));

                        if(vincente == null) {
                            prossimoTurno(getOtherPlayer(c.getPortatore()));
                        }else{
                            new Handler().postDelayed(() -> terminaManche(vincente), intermezzo);
                        }
                    });
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static void initHost() {
        inizializza();

        mazzoOnline = engineMultiplayer.creaMazzoFirebase();
        snapshot.setMazzo(mazzoOnline);

        FirebaseClass.editFieldFirebase(codiceStanza,"mazzo", mazzoOnline);

        Engine.distribuisciCarte(new Giocatore[]{host, enemy});

        ActivityMultiplayerGame.initPartita = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void initEnemy() {
        inizializza();

        mazzoOnline = snapshot.getMazzo();

        Engine.creaMazzo(mazzoOnline);

        Engine.distribuisciCarte(new Giocatore[]{host, enemy});

        ActivityMultiplayerGame.initPartita = true;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void prossimoTurno(Giocatore p) {
        if(p == null)
            return;

        Engine.prossimoTurno(p);
        giocante = p;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void terminaManche(Giocatore vincitore){
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost", "null");
        FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy", "null");

        if(snapshot != null){
            snapshot.setGiocataDaHost("null");
            snapshot.setGiocataDaEnemy("null");
        }

        Engine.terminaManche(vincitore);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void setOnCLickListener(){
        for(Button b : Game.user.getBottoni())
            b.setOnClickListener(engineMultiplayer::onClick);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void onClick(View v){
        if(!Game.canPlay)
            return;

        if(Game.user != giocante)
            return;

        final Button bottone = (Button) v;
        final Carta carta = Engine.getCartaFromButton(bottone);

        int index = Game.user.getIndexFromCarta(carta);

        //Game.canPlay = false;

        if(role.equals("HOST")){
            FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaHost",carta.getNome()+"#"+index);
        }else{
            FirebaseClass.editFieldFirebase(codiceStanza,"giocataDaEnemy",carta.getNome()+"#"+index);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void updateChat(){
        String chatMsg = snapshot.getChat();

        if(chatMsg.equals("null"))
            return;

        String[] chatTkn = chatMsg.split(CHAT_DELIMETER);
        String authorName = chatTkn[0];
        StringBuilder textEntered = new StringBuilder(new String());

        for(int i = 1; i < chatTkn.length; i++)
            textEntered.append(chatTkn[i]);

        FirebaseClass.editFieldFirebase(codiceStanza,"chat","null");
        sendChatMessage(authorName, textEntered.toString(), false);
    }

    public static void createChat(){
        chat = new Dialog(activity);
        chat.setContentView(R.layout.text_chat);
        chat.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        View sendMessage = chat.findViewById(R.id.send);
        TextInputEditText inputEditText = chat.findViewById(R.id.inputDialog);

        View closeDialog = chat.findViewById(R.id.closeDialog);

        removeChatNotis();

        sendMessage.setOnClickListener(v -> {
            String textEntered = inputEditText.getText().toString().trim();

            if(textEntered.isEmpty())
                return;

            inputEditText.setText(new String());

            String author = Game.user.getId();
            String fullMessage = author + CHAT_DELIMETER + textEntered;
            FirebaseClass.editFieldFirebase(codiceStanza, "chat", fullMessage);
        });

        Runnable scrollDown = () -> {
            ScrollView scrollView = chat.findViewById(R.id.scrollView);
            scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
        };

        chat.setOnShowListener(dialog -> {
            scrollDown.run();
            removeChatNotis();
        });

        closeDialog.setOnClickListener(v -> chat.dismiss());
        chat.setOnDismissListener(dialog -> removeChatNotis());
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void sendChatMessage(String authorId, String message, boolean isEvent){
        LinearLayout scrollViewLayout = chat.findViewById(R.id.scrollViewLayout);

        LayoutInflater inflater = LayoutInflater.from(activity);
        View parentView = inflater.inflate(R.layout.singlemsg, null);

        ImageView icon = parentView.findViewById(R.id.icon);
        TextView authorView = parentView.findViewById(R.id.authorName);
        TextView messageView = parentView.findViewById(R.id.messageSent);
        TextView chatSeperator = parentView.findViewById(R.id.chatSeperator2);

        Giocatore author = getPlayerById(authorId);
        String authorName = author == Game.user ? activity.getString(R.string.you) : author.getNome();

        authorView.setText(authorName.trim());
        messageView.setText(message.trim());
        icon.setImageBitmap(author.getIcon());

        if(isEvent || authorName.isEmpty())
            chatSeperator.setText(new String());
        else
            sendChatNotis();

        scrollViewLayout.addView(parentView);

        ScrollView scrollView = chat.findViewById(R.id.scrollView);
        scrollView.post(() -> scrollView.fullScroll(View.FOCUS_DOWN));
    }

    public static void removeChatNotis(){
        View chatNotis = activity.findViewById(R.id.chatNotis);
        chatNotis.setVisibility(View.INVISIBLE);
    }

    public static void sendChatNotis(){
        if(!chat.isShowing()){
            View chatNotis = activity.findViewById(R.id.chatNotis);
            chatNotis.setVisibility(View.VISIBLE);
        }

        final MediaPlayer mp = MediaPlayer.create(activity, R.raw.chatnotis);
        mp.start();
    }

    public static void openChat(){
        chat.show();
    }
}
