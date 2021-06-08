package firebase;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class firebase {

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance().getReference();
    }

}
