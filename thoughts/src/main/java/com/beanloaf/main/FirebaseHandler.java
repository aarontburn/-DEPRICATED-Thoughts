package com.beanloaf.main;

import java.io.FileInputStream;
import java.io.IOException;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHandler {

    private FirebaseDatabase firebaseDatabase;
    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com";
    private static final String KEY_PATH = "serviceAccountKey.json";
    private DatabaseReference ref;

    public FirebaseHandler() {
        try {
            FileInputStream serviceAccount = new FileInputStream(KEY_PATH);

            FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
            ref = firebaseDatabase.getReference("<USERNAME>");

            addPostEventListener(ref);

            System.out.println("Sucessfully synced with firebase.");

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

    }

    public DatabaseReference getDatabase() {
        return this.ref;
    }

    public String read(String titleName) {
        // ref.child(titleName).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
        //     @Override
        //     public void onComplete(@NonNull Task<DataSnapshot> task) {
        //         if (!task.isSuccessful()) {
        //             Log.e("firebase", "Error getting data", task.getException());
        //         }
        //         else {
        //             Log.d("firebase", String.valueOf(task.getResult().getValue()));
        //         }
        //     }
        // });
        // return returnString;

        return "";

    }

    public void update(String title, String tag, String date, String body) {
        ref.child(title).child("Tag").setValue(tag, null);
        ref.child(title).child("Date").setValue(date, null);
        ref.child(title).child("Body").setValue(body, null);

    }

    private void addPostEventListener(DatabaseReference mPostReference) {
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data : dataSnapshot.getChildren()) {
                    System.out.println(data);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        mPostReference.addValueEventListener(postListener);
    }

}