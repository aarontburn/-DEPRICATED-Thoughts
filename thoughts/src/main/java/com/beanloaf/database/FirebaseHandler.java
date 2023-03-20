package com.beanloaf.database;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import com.beanloaf.events.SaveNewFile;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;
import com.google.common.io.BaseEncoding;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FirebaseHandler {

    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";
    private final Thoughts main;
    public boolean isOnline;
    private List<ThoughtObject> objectList;

    private String userID = "";

    private String idToken = "";

    public FirebaseHandler(final Thoughts main) {
        this.main = main;

        try {
            // Checks to see if the pc is connected to the internet
            final URL url = new URL("https://www.google.com");
            final URLConnection connection = url.openConnection();
            connection.connect();
            isOnline = true;

            final AuthHandler authHandler = new AuthHandler();
            final ThoughtUser user = authHandler.signIn("lovebermany@gmail.com", "password123");
            userID = user.uid();
            idToken = user.idToken();

            getAll();


            System.out.println("Successfully synced with firebase.");

        } catch (Exception e) {
            isOnline = false;
            System.out.println("Not connected to the internet.");

            refreshPushPullLabels();
        }

    }


    private void getAll() {
        objectList = new ArrayList<>();

        try {
            final URL url = new URL(DATABASE_URL + userID + ".json?auth=" + idToken);
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : " + connection.getResponseCode());
            }

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(responseBuilder.toString());

            for (Object path : json.keySet()) {
                final String filePath = path + ".json";
                final String title = (String) ((JSONObject) json.get(path)).get("Title");
                final String tag = (String) ((JSONObject) json.get(path)).get("Tag");
                final String date = (String) ((JSONObject) json.get(path)).get("Date");
                final String body = ((String) ((JSONObject) json.get(path)).get("Body")).replace("\\n", "\n");

                objectList.add(new ThoughtObject(title, date, tag, body, new File(filePath)));

            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void update(final ThoughtObject obj) {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }

        try {
            final String path = obj.getPath().getName().replace(".json", "");

            String json = String.format("{\"%s\": { \"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}}",
                    path,
                    obj.getBody(),
                    obj.getDate(),
                    obj.getTag(),
                    obj.getTitle());

            json = json.replace("\n", "\\\\n");

            final URL url = new URL(DATABASE_URL + userID + ".json?auth=" + idToken);

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + idToken);
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully inserted to the database.");
            } else {
                System.out.println("Failed to insert data to the database. Response code: " + responseCode);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

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


    public boolean pull() {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return false;
        }
        getAll();
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

    public void delete(final ThoughtObject obj) {
        if (!this.isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        final String path = obj.getPath().getName().replace(".json", "");


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


    public record ThoughtUser(String uid,
                              String email,
                              String displayName,
                              String idToken,
                              boolean registered,
                              String refreshToken,
                              String expiresIn) {

    }


}
