package com.beanloaf.database;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.*;

import com.beanloaf.events.SaveNewFile;
import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;
import com.google.common.io.BaseEncoding;
import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class FirebaseHandler implements PropertyChangeListener {

    private static final String DATABASE_URL = "https://thoughts-4144a-default-rtdb.firebaseio.com/users/";
    private final Thoughts main;
    private List<ThoughtObject> objectList;

    public ThoughtUser user;

    public boolean isOnline;

    private String apiURL;

    public String registeredEmail = "";

    public String registeredPassword = "";

    public FirebaseHandler(final Thoughts main) {
        this.main = main;
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);
        checkUserFile();
    }

    public void checkUserFile() {
        if (user != null) {
            return;
        }


        try (BufferedReader reader = Files.newBufferedReader(Paths.get(TC.Paths.LOGIN_DIRECTORY.toURI()))) {
            final JSONObject json = (JSONObject) new JSONParser().parse(reader);
            final String email = (String) json.get("email");
            final String password = (String) json.get("password");

            boolean validInfo = true;


            registeredEmail = email;
            registeredPassword = password;


            if (!email.contains("@")) {
                validInfo = false;
            }
            if (password.isEmpty()) {
                validInfo = false;
            }


            if (validInfo && signInUser(registeredEmail, AuthHandler.sp(registeredPassword, true))) {
                start();
            }


        } catch (NoSuchFileException e) {
            System.err.println("Creating user.json");
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    public void start() {
        isConnectedToInternet();
        registerURL();
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

        final ThoughtUser returningUser = AuthHandler.signIn(email, password);
        if (returningUser != null) {
            user = returningUser;
            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.CONNECTED);
            return true;
        }
        System.err.println("Error logging in user.");
        return false;

    }

    public boolean registerNewUser(final String displayName, final String email, final String password) {
        if (user != null) {
            System.err.println("Error: a user is already logged in! Sign out first.");
            return false;
        }
        final ThoughtUser newUser = AuthHandler.signUp(displayName, email, password);
        if (newUser != null) {
            user = newUser;
            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.CONNECTED);
            return true;
        }
        System.err.println("Error registering new user.");
        return false;
    }

    private void registerURL() {
        try {
            if (user == null) {
                throw new IllegalArgumentException("User cannot be null when registering URL.");
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

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }
            responseReader.close();

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(responseBuilder.toString());

            if (json == null) {
                return;
            }


            final Base32 b32 = new Base32();
            for (final Object path : json.keySet()) {
                final String filePath = new String(b32.decode((String) path))
                        .replace("_", " ") + ".json";
                final String title = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Title")));
                final String tag = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Tag")));
                final String date = new String(b32.decode((String) ((JSONObject) json.get(path)).get("Date")));
                final String body = new String(b32.decode(((String) ((JSONObject) json.get(path)).get("Body"))
                        .replace("\\n", "\n").replace("\\t", "\t")));

                objectList.add(new ThoughtObject(title, date, tag, body, new File(filePath)));

            }

            connection.disconnect();
            refreshPushPullLabels();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    public void addEntryIntoDatabase(final ThoughtObject obj) {
        if (!isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }

        try {
            final String path = obj.getPath().getName().replace(".json", "").replace(" ", "_");

            final String json = String.format("{\"%s\": { \"Body\": \"%s\", \"Date\": \"%s\", \"Tag\": \"%s\", \"Title\": \"%s\"}}",
                    BaseEncoding.base32().encode(path.getBytes()).replace("=", ""),
                    BaseEncoding.base32().encode(obj.getBody().getBytes()),
                    BaseEncoding.base32().encode(obj.getDate().getBytes()),
                    BaseEncoding.base32().encode(obj.getTag().getBytes()),
                    BaseEncoding.base32().encode(obj.getTitle().replace("\n", "\\\\n")
                            .replace("\t", "\\\\t").getBytes()));

            final HttpURLConnection connection = (HttpURLConnection) new URL(apiURL).openConnection();
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + user.idToken());
            connection.setRequestMethod("POST");
            connection.setDoOutput(true);

            final OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(json);
            writer.flush();
            writer.close();

            final int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Data successfully inserted to the database.");
            } else {
                System.out.println("Failed to insert data to the database. Response code: " + responseCode);
            }

            refreshItems();



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
            for (final File file : Objects.requireNonNull(sortedFileDirectory)) {
                final ThoughtObject tObj = this.main.readFileContents(file);
                if (tObj != null) {
                    addEntryIntoDatabase(tObj);
                }
            }
            refreshItems();
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

    public void removeEntryFromDatabase(final ThoughtObject obj) {
        if (!isOnline) {
            System.out.println("Not connected to the internet!");
            return;
        }
        try {

            final String path = obj.getPath().getName().replace(".json", "").replace(" ", "_");
            final URL url = new URL(DATABASE_URL + user.localId + "/" + BaseEncoding.base32().encode(path.getBytes()).replace("=", "") + ".json?auth=" + user.idToken());
            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
        refreshItems();


    }


    public void refreshPushPullLabels() {
        if (!isOnline || user == null) {
            ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.DISCONNECTED);
            return;
        }

        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.UNPULLED_FILES,
                Math.max(this.objectList.size() - this.main.sortedThoughtList.size(), 0));

        ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.UNPUSHED_FILES,
                Math.max(this.main.sortedThoughtList.size() - this.objectList.size(), 0));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TC.Properties.REFRESH -> refreshItems();

            default -> {
            }
        }
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
