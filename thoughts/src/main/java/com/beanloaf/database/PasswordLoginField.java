package com.beanloaf.database;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class PasswordLoginField extends FormattedInputField {

    private final JButton resetPasswordButton;

    public PasswordLoginField(final String fieldName) {
        super(fieldName, true);

        resetPasswordButton = new JButton("Reset Password");
        resetPasswordButton.setFont(TC.Fonts.h5);
        this.add(resetPasswordButton, new GBC().setGridY(3).setAnchor(GridBagConstraints.EAST));

    }


    public void resetButtonEvent(final ActionListener listener) {
        resetPasswordButton.addActionListener(listener);
    }

}
