package com.beanloaf.events;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JTextArea;

import com.beanloaf.res.TC;
import com.beanloaf.view.ThoughtsMain;

public class TextAreaFocusListener implements FocusListener {
    ThoughtsMain main;
    public TextAreaFocusListener(ThoughtsMain main) {
        this.main = main;
    }

    public TextAreaFocusListener() {
        this.main = null;
    }

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
        if (main != null) {
            main.refreshThoughtList();

        }
    }
}