package firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import multiplayer.GameRoom;

public class FirebaseClass {

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance("https://briscola-d6aed-default-rtdb.europe-west1.firebasedatabase.app/").getReference();
    }

    public static void addToFirebase(GameRoom g)
    {
        getFbRef().child(g.getGameCode()).setValue(g);
    }

}
