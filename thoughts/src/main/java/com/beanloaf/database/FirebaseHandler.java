package com.beanloaf.database;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import com.beanloaf.events.SaveNewFile;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FirebaseHandler {

    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";
    private final Thoughts main;
    private List<ThoughtObject> objectList;

    public ThoughtUser user;

    public boolean isOnline;

    private String apiURL;

    public FirebaseHandler(final Thoughts main) {
        this.main = main;
    }

    public void start() {
        isConnectedToInternet();
        setUserInfo();
        refreshItems();
    }

    public void signOut() {
        user = null;
        isOnline = false;
        objectList = null;
        refreshPushPullLabels();
    }

    public boolean signInUser(final String email, final String password) {
        if (user != null) {
            System.err.println("Error: a user is already logged in! Sign out first.");
            return false;
        }


        final ThoughtUser returningUser = new AuthHandler().signIn(email, password);
        if (returningUser != null) {
            user = returningUser;
            main.thoughtsPCS.firePropertyChange(TC.Properties.CONNECTED);
            return true;
        }
        System.err.println("Error logging in user.");
        return false;

    }

    public boolean registerNewUser(final String email, final String password) {
        if (user != null) {
            System.err.println("Error: a user is already logged in! Sign out first.");
            return false;
        }
        final ThoughtUser newUser = new AuthHandler().signUp(email, password);
        if (newUser != null) {
            user = newUser;
            main.thoughtsPCS.firePropertyChange(TC.Properties.CONNECTED);
            return true;
        }
        System.err.println("Error registering new user.");
        return false;
    }

    private void setUserInfo() {
        try {
            if (user == null) {
                throw new IllegalArgumentException();
            }

            apiURL = DATABASE_URL + user.localId() + ".json?auth=" + user.idToken();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public boolean isConnectedToInternet() {
        try {
            // Checks to see if the pc is connected to the internet
            final URL url = new URL("https://www.google.com");
            final URLConnection connection = url.openConnection();
            connection.connect();
            isOnline = true;
            return true;
        } catch (Exception e) {
            System.out.println("Not connected to the internet.");
            refreshPushPullLabels();
            isOnline = false;
            return false;

        }

    }


    private void refreshItems() {
        if (!isOnline) {
            return;
        }
        objectList = new ArrayList<>();
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            if (connection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP error code : "
                        + connection.getResponseCode());
            }

            final BufferedReader responseReader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(responseBuilder.toString());

            if (json == null) {
                return;
            }

            for (final Object path : json.keySet()) {
                final String filePath = ((String) path).replace("_", " ") + ".json";
                final String title = (String) ((JSONObject) json.get(path)).get("Title");
                final String tag = (String) ((JSONObject) json.get(path)).get("Tag");
                final String date = ((String) ((JSONObject) json.get(path)).get("Date"));
                final String body = ((String) ((JSONObject) json.get(path)).get("Body"))
                        .replace("\\n", "\n").replace("\\t", "\t");

                objectList.add(new ThoughtObject(title, date, tag, body, new File(filePath)));

            }

            connection.disconnect();
            refreshPushPullLabels();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void update(final ThoughtObject obj) {
        if (!isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }

        try {
            final String path = obj.getPath().getName().replace(".json", "").replace(" ", "_");

            final String json = String.format("{\"%s\": { \"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}}",
                    path,
                    obj.getBody(),
                    obj.getDate(),
                    obj.getTag(),
                    obj.getTitle()).replace("\n", "\\\\n").replace("\t", "\\\\t");

            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + user.idToken());
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
        if (!isOnline) {
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
        if (!isOnline) {
            System.out.println("Not connected to the internet!");
            return false;
        }
        refreshItems();
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
        if (!isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(DATABASE_URL + "/" + user.localId + "/" + obj.getPath().getName().replace(" ", "_") + "?auth=" + user.idToken()).openConnection();
            connection.setRequestMethod("DELETE");

            // Check if the DELETE request was successful
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                System.out.println("Database entry deleted successfully.");
            } else {
                System.out.println("Failed to delete database entry.");
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    public void refreshPushPullLabels() {
        if (!isOnline || user == null) {
            this.main.thoughtsPCS.firePropertyChange(TC.Properties.DISCONNECTED);
            return;
        }

        /* Pull */
        int diffPull = this.objectList.size() - this.main.sortedThoughtList.size();
        if (diffPull < 0) {
            diffPull = 0;
        }
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.UNPULLED_FILES, diffPull);

        /* Push */
        int diffPush = this.main.sortedThoughtList.size() - this.objectList.size();
        if (diffPush < 0) {
            diffPush = 0;
        }
        this.main.thoughtsPCS.firePropertyChange(TC.Properties.UNPUSHED_FILES, diffPush);
    }


    public record ThoughtUser(String localId,
                              String email,
                              String displayName,
                              String idToken,
                              boolean registered,
                              String refreshToken,
                              String expiresIn) {

    }


}
