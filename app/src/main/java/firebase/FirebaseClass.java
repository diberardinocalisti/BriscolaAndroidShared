package firebase;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import Login.loginClass;
import multiplayer.EmailUser;
import multiplayer.GameRoom;
import multiplayer.FbUser;
import multiplayer.User;

import static multiplayer.engineMultiplayer.codiceStanza;

public class FirebaseClass {

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance("https://briscola-75019-default-rtdb.firebaseio.com/").getReference();
    }

    public static DatabaseReference getFbRefSpeicific(String path)
    {
        return FirebaseDatabase.getInstance("https://briscola-75019-default-rtdb.firebaseio.com/").getReference(path);
    }

    public static void addToFirebase(GameRoom g)
    {
        getFbRef().child(g.getGameCode()).setValue(g);
    }

    public static void addUserToFirebase(FbUser u, String uid)
    {
        getFbRef().child(uid).setValue(u);
    }

    public static void addUserToFirebase(EmailUser u, String username)
    {
        getFbRef().child(username).setValue(u);
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

    public static void aggiornaVittorie(String fbUID)
    {
        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String vinte;
                float vinteF = 0.0f;

                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("vinte")) {
                        vinte = String.valueOf(d.getValue());
                        vinteF = Float.parseFloat(vinte);
                        FirebaseClass.editFieldFirebase(fbUID,"vinte",vinteF+1);
                    }
                }
            }
        });
    }

    public static void aggiornaSconfitte(String fbUID)
    {
        FirebaseClass.getFbRef().child(fbUID).get().addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                String perse;
                float perseF;

                for(DataSnapshot d: task.getResult().getChildren())
                {
                    if(d.getKey().equals("perse")){
                        perse = String.valueOf(d.getValue());
                        perseF = Float.parseFloat(perse);
                        FirebaseClass.editFieldFirebase(fbUID,"perse",perseF+1);
                    }
                }

            }
        });
    }
}
