package firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Login.loginClass;
import multiplayer.GameRoom;

public class FirebaseClass {

    public static boolean exists;
    public static int count = 0;

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance("https://briscola-d6aed-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    public static DatabaseReference getFbRefSpeicific(String path)
    {
        return FirebaseDatabase.getInstance("https://briscola-d6aed-default-rtdb.europe-west1.firebasedatabase.app/").getReference(path);
    }


    public static void addToFirebase(GameRoom g)
    {
        getFbRef().child(g.getGameCode()).setValue(g);
    }

    public static void editFieldFirebase(String codiceStanza)
    {
        DatabaseReference update =  getFbRef().child(codiceStanza); //Mi posiziono nella tabella della stanza
        update.child("enemy").setValue(loginClass.getFBNome());
    }


}
