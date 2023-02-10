package com.beanloaf.main;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

import com.beanloaf.common.TC;
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

    private final ThoughtsMain main;

    private FirebaseDatabase firebaseDatabase;
    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com";
    private static final String KEY_PATH = "serviceAccountKey.json";
    private DatabaseReference ref;

    private ArrayList<ThoughtObject> objectList = null;

    public FirebaseHandler(ThoughtsMain main) {
        this.main = main;
        if (this.main == null) {
            throw new IllegalArgumentException("main passed into FirebaseHandler is null");
        }
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

    /**
     * Creates a new entry in the database, or updates the entry if the same path
     * has been found.
     * 
     * @param title
     * @param tag
     * @param date
     * @param body
     * @param fileName
     */
    public void update(String title, String tag, String date, String body, String fileName) {
        final String path = fileName.replace(".json", "");
        ref.child(path).child("Title").setValue(title, null);
        ref.child(path).child("Tag").setValue(tag, null);
        ref.child(path).child("Date").setValue(date, null);
        ref.child(path).child("Body").setValue(body, null);
    }

    public void update(ThoughtObject obj) {
        final String path = obj.getPath().getName().replace(".json", "");
        ref.child(path).child("Title").setValue(obj.getTitle(), null);
        ref.child(path).child("Tag").setValue(obj.getTag(), null);
        ref.child(path).child("Date").setValue(obj.getDate(), null);
        ref.child(path).child("Body").setValue(obj.getBody(), null);
    }

    /**
     * Pushes all files in the sorted directory into database.
     * 
     * Note: THIS ONLY PUSHES SORTED FILES.
     */
    public void push() {
        try {
            File[] sortedFileDirectory = TC.SORTED_DIRECTORY_PATH.listFiles();
            for (File file : sortedFileDirectory) {
                ThoughtObject tObj = this.main.readFileContents(file);
                if (tObj != null) {
                    update(tObj);
                }
            }
            System.out.println("Successfully pushed files to database.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Pulls all files from database and puts them in sorted.
     */
    public void pull() {
        try {
            for (ThoughtObject tObj : this.objectList) {
                new SaveNewFile(tObj).fbSave();
            }
            this.main.refreshThoughtList();
            System.out.println("Successfully pulled data from database.");
        } catch (Exception e) {
            e.printStackTrace();
        }



    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDataChange(DataSnapshot dataSnapshot) {
        objectList = new ArrayList<>();
        for (DataSnapshot data : dataSnapshot.getChildren()) {
            final Map<String, String> value = (Map<String, String>) data.getValue();
            final String filePath = data.getKey() + ".json";
            final String title = value.get("Title");
            final String tag = value.get("Tag");
            final String date = value.get("Date");
            final String body = value.get("Body");

            this.objectList.add(new ThoughtObject(title, date, tag, body, new File(filePath)));
        }
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Do nothing
    }

}
