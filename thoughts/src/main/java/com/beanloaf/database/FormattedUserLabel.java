package com.beanloaf.database;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;

public class FormattedUserLabel extends JPanel {

    private final static Font font = TC.Fonts.h4;

    public FormattedUserLabel(final FirebaseHandler.ThoughtUser user) {
        super(new GridBagLayout());

        final GBC c = new GBC().setAnchor(GridBagConstraints.WEST);

        final JLabel userNameText = new JLabel("Name");
        userNameText.setFont(font);
        this.add(userNameText, c.increaseGridY());
        final JLabel userNameLabel = new JLabel(user.displayName().isEmpty() ? " " : user.displayName());
        userNameLabel.setFont(font);
        this.add(userNameLabel, c.increaseGridY());

        this.add(new JLabel(" "), c.increaseGridY());

        final JLabel emailText = new JLabel("Email");
        emailText.setFont(font);
        this.add(emailText, c.increaseGridY());
        final JLabel emailLabel = new JLabel(user.email());
        emailLabel.setFont(font);
        this.add(emailLabel, c.increaseGridY());

        this.add(new JLabel(" "), c.increaseGridY());

        final JLabel userIdText = new JLabel("User Id");
        userIdText.setFont(font);
        this.add(userIdText, c.increaseGridY());
        final JLabel userIdLabel = new JLabel(user.localId());
        userIdLabel.setFont(font);
        this.add(userIdLabel, c.increaseGridY());

    }


}
