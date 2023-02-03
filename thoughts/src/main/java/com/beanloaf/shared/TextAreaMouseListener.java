package com.beanloaf.shared;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JTextArea;

import com.beanloaf.common.TC;

public class TextAreaMouseListener extends MouseAdapter {
    public void mouseClicked(MouseEvent e) {
        JTextArea j = (JTextArea) e.getComponent();
        if (j.getText().equals(TC.DEFAULT_TITLE)
                || j.getText().equals(TC.DEFAULT_DATE)
                || j.getText().equals(TC.DEFAULT_TAG)
                || j.getText().equals(TC.DEFAULT_BODY)) {
            j.requestFocusInWindow();
            j.selectAll();
        }
    }
}