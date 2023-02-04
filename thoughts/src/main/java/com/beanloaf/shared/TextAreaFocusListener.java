package com.beanloaf.shared;

import java.awt.event.FocusListener;
import java.awt.event.FocusEvent;

import javax.swing.JTextArea;

import com.beanloaf.common.TC;

public class TextAreaFocusListener implements FocusListener {
    @Override
    public void focusGained(FocusEvent e) {
        JTextArea j = (JTextArea) e.getComponent();
        if (j.getText().equals(TC.DEFAULT_TITLE)
                || j.getText().equals(TC.DEFAULT_DATE)
                || j.getText().equals(TC.DEFAULT_TAG)
                || j.getText().equals(TC.DEFAULT_BODY)) {
            j.requestFocusInWindow();
            j.selectAll();
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        // Do nothing
    }
}