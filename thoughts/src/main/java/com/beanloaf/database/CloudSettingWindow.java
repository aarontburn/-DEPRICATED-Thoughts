package com.beanloaf.database;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;
import org.json.simple.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class CloudSettingWindow extends JFrame {

    private final FirebaseHandler authHandler;
    private JPanel contentContainer;


    public CloudSettingWindow(final FirebaseHandler auth) {
        super("Cloud Settings");
        this.authHandler = auth;
        createUI();
        setVisible(true);

    }

    private void createUI() {
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setFocusable(true);
        this.setSize(700, 615);
        this.setLocationRelativeTo(null);
        this.getContentPane().setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));


        final JLabel displayLabel = new JLabel("Thoughts: Cloud Integration", SwingConstants.CENTER);
        displayLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        displayLabel.setFont(TC.Fonts.h2);
        displayLabel.setForeground(Color.white);
        displayLabel.setPreferredSize(new Dimension(700, 100));
        this.add(displayLabel);


        contentContainer = new JPanel(new GridBagLayout());
        contentContainer.setPreferredSize(new Dimension(700, 500));
        this.add(contentContainer);


        if (authHandler.user == null) {
            createLoginRegisterButtons();
        } else {
            userSignedInScreen();
        }


    }


    private void changeDisplay(final Runnable runnable) {
        contentContainer.removeAll();
        contentContainer.revalidate();
        runnable.run();
        contentContainer.repaint();
    }


    private void userSignedInScreen() {
        final GBC c = new GBC().setInsets(0, 25, 0, 25).setAnchor(GridBagConstraints.WEST);

        final FormattedUserLabel userDisplay = new FormattedUserLabel(authHandler.user);
        contentContainer.add(userDisplay, c.increaseGridY());

        final JButton signOutButton = new JButton("Sign Out");
        signOutButton.setFont(TC.Fonts.h4);
        signOutButton.addActionListener(event -> {
            saveLoginInformation(authHandler.user.email(), "");
            authHandler.checkUserFile();
            authHandler.signOut();
            changeDisplay(this::createLoginRegisterButtons);

        });
        contentContainer.add(signOutButton, c.increaseGridY().setAnchor(GridBagConstraints.EAST));


    }

    private void createLoginRegisterButtons() {
        final JButton loginButton = new JButton("Login");
        loginButton.setFont(TC.Fonts.h4);
        loginButton.setPreferredSize(new Dimension(150, 75));
        loginButton.addActionListener(event -> {
            changeDisplay(this::createLoginFields);
            authHandler.checkUserFile();
        });
        contentContainer.add(loginButton, new GBC());

        final JButton registerButton = new JButton("Register");
        registerButton.setFont(TC.Fonts.h4);
        registerButton.setPreferredSize(new Dimension(150, 75));
        registerButton.addActionListener(event -> changeDisplay(this::createRegisterFields));
        contentContainer.add(registerButton, new GBC(1, 0));
    }

    private void createLoginFields() {
        final GBC constraints = new GBC().setWeightY(0.2);

        final JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(new Dimension(75, 35)));
        backButton.setFont(TC.Fonts.h5);
        backButton.addActionListener(event -> changeDisplay(this::createLoginRegisterButtons));
        contentContainer.add(backButton, new GBC().setAnchor(GridBagConstraints.NORTHWEST));

        final FormattedInputField emailInputField = new FormattedInputField("Email");
        emailInputField.setText(authHandler.registeredEmail.equals("") ? "" : authHandler.registeredEmail);
        contentContainer.add(emailInputField, constraints);

        final FormattedInputField passwordInputField = new FormattedInputField("Password", true);
        passwordInputField.setText(
                authHandler.registeredPassword.equals("")
                        ? ""
                        : AuthHandler.sp(authHandler.registeredPassword, true));
        contentContainer.add(passwordInputField, constraints.increaseGridY());


        final JLabel errorLabel = new JLabel("");
        errorLabel.setFont(TC.Fonts.h5);
        errorLabel.setForeground(Color.red);
        contentContainer.add(errorLabel, constraints.increaseGridY().setWeightY(0.1));


        final JButton submitButton = new JButton("Login");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.setFont(TC.Fonts.h4);
        submitButton.addActionListener(event -> {
            if (emailInputField.getText().contains("@")
                    && !passwordInputField.getText().isBlank()) {

                if (authHandler.signInUser(emailInputField.getText(), passwordInputField.getText())) {
                    authHandler.start();
                    saveLoginInformation(emailInputField.getText(), passwordInputField.getText());
                    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                } else {
                    errorLabel.setText("Error: Invalid email and password combination.");

                }

            }
        });
        contentContainer.add(submitButton, constraints.increaseGridY().setWeightY(0.4));

    }

    private void createRegisterFields() {
        final GBC constraints = new GBC().setWeightY(0.2);

        final JButton backButton = new JButton("Back");
        backButton.setPreferredSize(new Dimension(new Dimension(75, 35)));
        backButton.setFont(TC.Fonts.h5);
        backButton.addActionListener(event -> changeDisplay(this::createLoginRegisterButtons));
        contentContainer.add(backButton, new GBC().setAnchor(GridBagConstraints.NORTHWEST));

        final FormattedInputField emailInputField = new FormattedInputField("Email");
        contentContainer.add(emailInputField, constraints);

        final FormattedInputField passwordInputField = new FormattedInputField("Password (needs to be at least 6 characters)", true);
        contentContainer.add(passwordInputField, constraints.increaseGridY());

        final FormattedInputField reenterPasswordInputField = new FormattedInputField("Re-enter Password", true);
        contentContainer.add(reenterPasswordInputField, constraints.increaseGridY());

        final JLabel errorLabel = new JLabel("");
        errorLabel.setFont(TC.Fonts.h5);
        errorLabel.setForeground(Color.red);
        contentContainer.add(errorLabel, constraints.increaseGridY().setWeightY(0.1));

        final JButton submitButton = new JButton("Register");
        submitButton.setPreferredSize(new Dimension(150, 50));
        submitButton.setFont(TC.Fonts.h4);

        submitButton.addActionListener(event -> {
            if (!emailInputField.getText().contains("@")) {
                errorLabel.setText("Error: Invalid email.");
                return;
            }
            if (passwordInputField.getText().isBlank() || passwordInputField.getText().length() < 6) {
                errorLabel.setText("Error: Invalid Password.");
                return;
            }
            if (!passwordInputField.getText().equals(reenterPasswordInputField.getText())) {
                errorLabel.setText("Error: Passwords don't match.");
                return;
            }

            if (authHandler.registerNewUser(emailInputField.getText(), passwordInputField.getText())) {
                authHandler.start();
                saveLoginInformation(emailInputField.getText(), passwordInputField.getText());
                this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
            } else {
                errorLabel.setText("Error: User already exists.");
            }

        });

        contentContainer.add(submitButton, constraints.increaseGridY().setWeightY(0.4));
    }

    private void saveLoginInformation(final String email, final String password) {

        try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(TC.Paths.LOGIN_DIRECTORY.toURI()))) {

            final Map<String, String> textContent = new HashMap<>();

            textContent.put("email", email);
            textContent.put("password", password.equals("") ? password : AuthHandler.sp(password, false));

            final JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
