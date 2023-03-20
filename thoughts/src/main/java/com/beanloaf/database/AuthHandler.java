package com.beanloaf.database;

import org.apache.commons.codec.binary.Base32;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AuthHandler {

    private static final String KEY = "IFEXUYKTPFATGSCXPFHE6OC2LBSEG4JYIJGDC4RNIFWXUUS7L5HEOM2CJ4YWYMA";
    public AuthHandler() {

    }


    public FirebaseHandler.ThoughtUser signIn(final String email, final String password) {

        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signInWithPassword?key=" + new String(new Base32().decode(KEY)));

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));

            final String userId = (String) json.get("localId");
            final String userEmail = (String) json.get("email");
            final String displayName = (String) json.get("displayName");
            final String idToken = (String) json.get("idToken");
            final boolean registered = (Boolean) json.get("registered");
            final String refreshToken = (String) json.get("refreshToken");
            final String expiresIn = (String) json.get("expiresIn");

            System.out.println("User ID: " + userId);
            System.out.println("Email: " + userEmail);
            System.out.println("Display Name: " + displayName);
            System.out.println("ID Token: " + idToken);
            System.out.println("Registered: " + registered);
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("Expires in: " + expiresIn);

            return new FirebaseHandler.ThoughtUser(userId, userEmail, displayName, idToken, registered, refreshToken, expiresIn);



        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public FirebaseHandler.ThoughtUser signUp(final String email, final String password) {
        try {
            final URL url = new URL("https://identitytoolkit.googleapis.com/v1/accounts:signUp?key=" + new String(new Base32().decode(KEY)));

            final HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            final String requestBody = "{\"email\":\"" + email + "\",\"password\":\"" + password + "\",\"returnSecureToken\":true}";
            final OutputStream outputStream = connection.getOutputStream();
            outputStream.write(requestBody.getBytes());
            outputStream.flush();

            final BufferedReader responseReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            final StringBuilder responseBuilder = new StringBuilder();
            String line;
            while ((line = responseReader.readLine()) != null) {
                responseBuilder.append(line);
            }

            final JSONObject json = (JSONObject) new JSONParser()
                    .parse(new StringReader(responseBuilder.toString()));

            System.out.println("Created new Account: ");
            final String userId = (String) json.get("localId");
            final String userEmail = (String) json.get("email");
            final String displayName = (String) json.get("displayName");
            final String idToken = (String) json.get("idToken");
            final String refreshToken = (String) json.get("refreshToken");
            final String expiresIn = (String) json.get("expiresIn");

            System.out.println("User ID: " + userId);
            System.out.println("Email: " + userEmail);
            System.out.println("Display Name: " + displayName);
            System.out.println("ID Token: " + idToken);
            System.out.println("Refresh Token: " + refreshToken);
            System.out.println("Expires in: " + expiresIn);

            return new FirebaseHandler.ThoughtUser(userId, userEmail, displayName, idToken, true, refreshToken, expiresIn);


        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

}
