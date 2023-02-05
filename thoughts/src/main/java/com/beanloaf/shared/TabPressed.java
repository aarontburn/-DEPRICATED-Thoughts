package com.beanloaf.shared;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.JTextArea;

public class TabPressed extends KeyAdapter {
    JTextArea textArea;

    public TabPressed(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_TAB
                || (e.getKeyCode() == KeyEvent.VK_ENTER
                        && (textArea.getName().equals("titleLabel") 
                        || (textArea.getName().equals("tagLabel"))))) {
            if (e.getModifiersEx() > 0) {
                textArea.transferFocusBackward();
            } else {
                textArea.transferFocus();
            }
            e.consume();
        }
    }
}