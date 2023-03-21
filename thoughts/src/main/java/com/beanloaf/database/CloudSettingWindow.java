package com.beanloaf.database;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowEvent;

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
        final GBC c = new GBC().setFill(GridBagConstraints.HORIZONTAL);

        final JLabel displayName = new JLabel(authHandler.user.displayName());
        final JLabel email = new JLabel(authHandler.user.email());
        final JLabel userLocalID = new JLabel(authHandler.user.localId());


        contentContainer.add(displayName, c);
        contentContainer.add(email, c.setGridY(1));
        contentContainer.add(userLocalID, c.setGridY(2));


        final JButton signOutButton = new JButton("Sign Out");

        signOutButton.addActionListener(event -> {
            authHandler.signOut();
            changeDisplay(this::createLoginRegisterButtons);

        });
        contentContainer.add(signOutButton, c.setGridY(3));


    }

    private void createLoginRegisterButtons() {
        final JButton loginButton = new JButton("Login");
        loginButton.setFont(TC.Fonts.h4);
        loginButton.setPreferredSize(new Dimension(150, 75));
        loginButton.addActionListener(event -> changeDisplay(this::createLoginFields));
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
        contentContainer.add(emailInputField, constraints);

        final FormattedInputField passwordInputField = new FormattedInputField("Password", true);
        contentContainer.add(passwordInputField, constraints.setGridY(1));

        final JButton submitButton = new JButton("Login");
        submitButton.setPreferredSize(new Dimension(100, 50));
        submitButton.setFont(TC.Fonts.h4);
        submitButton.addActionListener(event -> {
            if (emailInputField.getText().contains("@")
                    && !passwordInputField.getText().isBlank()) {

                if (authHandler.signInUser(emailInputField.getText(), passwordInputField.getText())) {
                    authHandler.start();
                    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }

            }
        });
        contentContainer.add(submitButton, constraints.setGridY(2).setWeightY(0.4));

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
        contentContainer.add(passwordInputField, constraints.setGridY(1));

        final FormattedInputField reenterPasswordInputField = new FormattedInputField("Re-enter Password", true);
        contentContainer.add(reenterPasswordInputField, constraints.setGridY(2));

        final JButton submitButton = new JButton("Register");
        submitButton.setPreferredSize(new Dimension(150, 50));
        submitButton.setFont(TC.Fonts.h4);

        submitButton.addActionListener(event -> {
            if (emailInputField.getText().contains("@")
                    && !passwordInputField.getText().isBlank()
                    && !reenterPasswordInputField.getText().isBlank()
                    && passwordInputField.getText().equals(reenterPasswordInputField.getText())) {

                if (authHandler.registerNewUser(emailInputField.getText(), passwordInputField.getText())) {
                    authHandler.start();
                    this.dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
                }

            }
        });


        contentContainer.add(submitButton, constraints.setGridY(3).setWeightY(0.4));
    }


}
