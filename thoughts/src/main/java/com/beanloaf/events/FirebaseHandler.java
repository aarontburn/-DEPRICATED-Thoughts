package com.beanloaf.events;

import java.io.File;
import java.io.FileInputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseHandler implements ValueEventListener {

    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com";
    private static final String KEY_PATH = "serviceAccountKey.json";

    private final Thoughts main;

    private DatabaseReference ref;
    public boolean isOnline;
    private List<ThoughtObject> objectList;
    private boolean isStartup;

    public FirebaseHandler(final Thoughts main) {
        this.main = main;
        try {
            // Checks to see if the pc is connected to the internet
            final URL url = new URL("https://www.google.com");
            final URLConnection connection = url.openConnection();
            connection.connect();
            isOnline = true;

            final FileInputStream serviceAccount = new FileInputStream(KEY_PATH);
            final FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl(DATABASE_URL)
                    .build();

            final FirebaseApp app = FirebaseApp.initializeApp(options);
            AuthHandler authHandler = new AuthHandler(app);






            final String userID = authHandler.signIn("lovebermany@gmail.com", "password123");




            final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance(DATABASE_URL);

            ref = firebaseDatabase.getReference("<USERNAME>");
            ref.addValueEventListener(this);

            System.out.println("Successfully synced with firebase.");

        } catch (Exception e) {
            isOnline = false;
            System.out.println("Not connected to the internet.");

            refreshPushPullLabels();
        }

    }



    public void update(final ThoughtObject obj) {
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
            final File[] sortedFileDirectory = TC.Paths.SORTED_DIRECTORY_PATH.listFiles();
            for (final File file : sortedFileDirectory) {
                final ThoughtObject tObj = this.main.readFileContents(file);
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
            if (this.objectList != null) {
                for (final ThoughtObject tObj : this.objectList) {
                    new SaveNewFile(tObj).fbSave();
                }
                this.main.refreshThoughtList();
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<ThoughtObject> getList() {
        return this.objectList;
    }

    /**
     * Deletes a file from the database.
     * 
     * @param obj
     */
    public void delete(final ThoughtObject obj) {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        final String path = obj.getPath().getName().replace(".json", "");

        ref.child(path).removeValue((error, ref) -> {
            if (error == null) {
                System.out.println("Successfully deleted file.");
            } else {
                System.err.println("Error occured on deletion.");
            }
        });
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onDataChange(final DataSnapshot dataSnapshot) {
        System.out.println("onDataChange() fired");
        objectList = new ArrayList<>();
        for (final DataSnapshot data : dataSnapshot.getChildren()) {
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
            this.main.thoughtsPCS.firePropertyChange(TC.Properties.DISCONNECTED);

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
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.UNPULLED_FILES, null, diffPull);
        /* Push */
        int diffPush = this.main.sortedThoughtList.size() - this.objectList.size();
        if (diffPush < 0) {
            diffPush = 0;
        }
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.UNPUSHED_FILES, null, diffPush);

    }

    @Override
    public void onCancelled(final DatabaseError databaseError) {
        // Do nothing
    }

}
