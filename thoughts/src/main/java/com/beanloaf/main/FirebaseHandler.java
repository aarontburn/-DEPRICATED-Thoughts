package com.beanloaf.main;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.shared.SaveNewFile;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHandler implements ValueEventListener {

    private FirebaseDatabase firebaseDatabase;
    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com";
    private static final String KEY_PATH = "serviceAccountKey.json";
    private DatabaseReference ref;

    private ArrayList<ThoughtObject> objectList = null;

    public FirebaseHandler() {
        try {
            final FileInputStream serviceAccount = new FileInputStream(KEY_PATH);

            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
            ref = firebaseDatabase.getReference("<USERNAME>");
            ref.addValueEventListener(this);

            System.out.println("Sucessfully synced with firebase.");

        } catch (IOException ioe) {
            System.out.println(ioe.getMessage());
        }

    }

    public DatabaseReference getDatabase() {
        return this.ref;
    }

    public void update(String title, String tag, String date, String body, String fileName) {
        ref.child(title).child("Tag").setValue(tag, null);
        ref.child(title).child("Date").setValue(date, null);
        ref.child(title).child("Body").setValue(body, null);
        ref.child(title).child("FilePath").setValue(fileName, null);

    }

    public void uploadAllSorted() {

    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDataChange(DataSnapshot dataSnapshot) {
        objectList = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            final String title = data.getKey();
            final Map<String, String> value = (Map<String, String>) data.getValue();
            final String tag = value.get("Tag");
            final String date = value.get("Date");
            final String body = value.get("Body");
            final String fileName = value.get("FilePath");

            new SaveNewFile(title, tag, body, date, fileName).fbSave();

        }
    }

    public ArrayList<ThoughtObject> getAllSorted() {
        if (this.objectList != null) {
            return this.objectList;
        }
        return new ArrayList<ThoughtObject>();

    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Do nothing
    }

}
