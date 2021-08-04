package com.example.briscolav10;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        addToFirebase(new Prova("Prova porcod"));
    }

    public static DatabaseReference getFbRef()
    {
        return FirebaseDatabase.getInstance("https://briscola-75019-default-rtdb.firebaseio.com/").getReference();
    }

    public static DatabaseReference getFbRefSpeicific(String path)
    {
        return FirebaseDatabase.getInstance("https://briscola-75019-default-rtdb.firebaseio.com/").getReference(path);
    }


    public static void addToFirebase(Prova g)
    {
        getFbRef().child(g.getA()).setValue(g);
    }
}