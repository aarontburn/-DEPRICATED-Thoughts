package com.beanloaf.events;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

import com.beanloaf.main.ThoughtsMain;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.DatabaseReference.CompletionListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHandler implements ValueEventListener {

    private final ThoughtsMain main;

    private FirebaseDatabase firebaseDatabase;
    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com";
    private static final String KEY_PATH = "serviceAccountKey.json";
    private DatabaseReference ref;

    public boolean isOnline;

    private ArrayList<ThoughtObject> objectList = null;
    private boolean isStartup = false;

    public FirebaseHandler(ThoughtsMain main) {
        this.main = main;
        try {
            // Checks to see if the pc is connected to the internet
            URL url = new URL("https://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            isOnline = true;

            final FileInputStream serviceAccount = new FileInputStream(KEY_PATH);
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);
            ref = firebaseDatabase.getReference("<USERNAME>");
            ref.addValueEventListener(this);

            System.out.println("Successfully synced with firebase.");

        } catch (Exception e) {
            isOnline = false;
            System.out.println("Not connected to the internet.");

            refreshPushPullLabels();
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
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        final String path = fileName.replace(".json", "");
        ref.child(path).child("Title").setValue(title, null);
        ref.child(path).child("Tag").setValue(tag, null);
        ref.child(path).child("Date").setValue(date, null);
        ref.child(path).child("Body").setValue(body, null);
    }

    public void update(ThoughtObject obj) {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
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
     * 
     * @return True if the push was successful, false otherwise.
     */
    public boolean push() {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return false;
        }
        try {
            File[] sortedFileDirectory = TC.SORTED_DIRECTORY_PATH.listFiles();
            for (File file : sortedFileDirectory) {
                ThoughtObject tObj = this.main.readFileContents(file);
                if (tObj != null) {
                    update(tObj);
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Pulls all files from database and puts them in sorted.
     * 
     * @return True if the pull was successful, false otherwise.
     */
    public boolean pull() {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return false;
        }
        try {
            for (ThoughtObject tObj : this.objectList) {
                new SaveNewFile(tObj).fbSave();
            }
            this.main.refreshThoughtList();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<ThoughtObject> getList() {
        return this.objectList;
    }

    /**
     * Deletes a file from the database.
     * 
     * @param obj
     */
    public void delete(ThoughtObject obj) {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        final String path = obj.getPath().getName().replace(".json", "");
        ref.child(path).removeValue(new CompletionListener() {
            @Override
            public void onComplete(DatabaseError error, DatabaseReference ref) {
                if (error == null) {
                    System.out.println("Successfully deleted file.");
                } else {
                    System.err.println("Error occured on deletion.");
                }
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDataChange(DataSnapshot dataSnapshot) {
        System.out.println("onDataChange() fired");
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
        if (!isStartup && this.main.settings.isPullOnStartup()) {
            isStartup = true;
            pull();
        }

        refreshPushPullLabels();
    }

    public void refreshPushPullLabels() {
        if (!this.isOnline) {
            this.main.pullLabel.setText("Not connected.");
            this.main.pushLabel.setText("Not connected.");
            this.main.pullButton.setEnabled(isOnline);
            this.main.pushButton.setEnabled(isOnline);
            return;
        }

        /* Pull */
        /*-
         * This way will compares file contents rather than number, but will probably
         * cause performance issues as more files are added.
         * 
         *  int newPullFiles = 0;
         *  for (ThoughtObject obj : this.objectList) {
         *      for (ThoughtObject mainObj : this.main.sortedThoughtList) {
         *          if (!mainObj.equals(obj)) {
         *              newPullFiles++;
         *          }
         *      }
         *  }
         * 
         */

        int diffPull = this.objectList.size() - this.main.sortedThoughtList.size();
        if (diffPull < 0) {
            diffPull = 0;
        }
        this.main.pullLabel.setText(String.valueOf(diffPull) + " files can be pulled.");
        /* Push */
        int diffPush = this.main.sortedThoughtList.size() - this.objectList.size();
        if (diffPush < 0) {
            diffPush = 0;
        }
        this.main.pushLabel.setText(String.valueOf(diffPush) + " files not pushed.");
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        // Do nothing
    }

}
