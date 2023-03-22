package com.beanloaf.database;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;

public class FormattedInputField extends JPanel {

    private JPasswordField passwordField;
    private JTextField textField;

    private final boolean forPassword;

    public FormattedInputField(final String fieldName) {
        this(fieldName, false);
    }

    public FormattedInputField(final String fieldName, final boolean forPassword) {
        super(new GridBagLayout());
        this.forPassword = forPassword;

        final GBC constraints = new GBC().setAnchor(GridBagConstraints.WEST).setFill(GridBagConstraints.HORIZONTAL);

        final JLabel fieldNameLabel = new JLabel(fieldName);
        fieldNameLabel.setFont(TC.Fonts.h4);
        this.add(fieldNameLabel, constraints);


        if (forPassword) {
            passwordField = new JPasswordField(25);
            passwordField.setFont(TC.Fonts.h4);
            this.add(passwordField, constraints.setGridY(1));
            return;
        }

        textField = new JTextField(25);
        textField.setFont(TC.Fonts.h4);
        this.add(textField, constraints.setGridY(1));

    }

    public String getText() {
        if (forPassword) {
            return new String(passwordField.getPassword());
        }
        return textField.getText();
    }

    public void setText(final String text) {
        if (forPassword) {
            passwordField.setText(text);
            return;
        }
        textField.setText(text);
    }

}
