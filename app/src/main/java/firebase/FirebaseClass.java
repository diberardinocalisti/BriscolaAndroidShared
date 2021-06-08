package firebase;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

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

    public static boolean hasRoom(Context c, String codiceStanza)
    {
        System.out.println("Stanza --> " + codiceStanza);
        getFbRefSpeicific(codiceStanza).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot dataSnapshot) {

                for(DataSnapshot d : dataSnapshot.getChildren())
                {
                    System.out.println("Children --> " + d.getValue());
                    count++;
                }
            }

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError databaseError) {

            }

        });

        Toast.makeText(c,"Count --> " + count,Toast.LENGTH_LONG).show();

        return true;

    }

}
