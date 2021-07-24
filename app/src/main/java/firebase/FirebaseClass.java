package firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Login.loginClass;
import multiplayer.GameRoom;

import static multiplayer.engineMultiplayer.codiceStanza;

public class FirebaseClass {

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance("https://briscola-472a8-default-rtdb.firebaseio.com/").getReference();
    }

    public static DatabaseReference getFbRefSpeicific(String path)
    {
        return FirebaseDatabase.getInstance("https://briscola-472a8-default-rtdb.firebaseio.com/").getReference(path);
    }


    public static void addToFirebase(GameRoom g)
    {
        getFbRef().child(g.getGameCode()).setValue(g);
    }

    public static <T> void editFieldFirebase(String codiceStanza, String fieldToUpdate, T value)
    {
        DatabaseReference update =  getFbRefSpeicific(codiceStanza); //Mi posiziono nella tabella della stanza
        update.child(fieldToUpdate).setValue(value);
    }

    public static <T> void deleteFieldFirebase(String specific,String field)
    {
        if(specific != null)
            getFbRefSpeicific(specific).child(field).removeValue();
        else
            getFbRef().child(field).removeValue();
    }
}
