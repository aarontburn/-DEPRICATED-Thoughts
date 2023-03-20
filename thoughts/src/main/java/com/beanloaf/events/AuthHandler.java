package com.beanloaf.events;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthHandler {

    private static final String API_KEY = "";
    private static final String SIGN_IN_URL = "https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + API_KEY;

    private final FirebaseAuth auth;

    public AuthHandler(final FirebaseApp firebaseApp) {
        auth = FirebaseAuth.getInstance(firebaseApp);
    }


    public String signIn(final String email, final String password) {
        try {
            final HttpURLConnection connection = (HttpURLConnection) new URL(SIGN_IN_URL).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }
            final String response = responseBuilder.toString();
            System.out.println(response);

            JSONObject json = (JSONObject) new JSONParser().parse(new StringReader(response));
            String userId = (String) json.get("localId");
            System.out.println("User ID: " + userId);
            return userId;



        } catch (Exception e) {
            e.printStackTrace();
        }

        return "";

    }

    public void createNewUser(final String email, final String password) {
        try {
            UserRecord userRecord = auth.createUser(new UserRecord.CreateRequest()
                    .setEmail(email)
                    .setPassword(password));

            System.out.println("Successfully created user: " + userRecord.getUid());

        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
    }

}
