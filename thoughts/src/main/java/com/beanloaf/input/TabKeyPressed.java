package com.beanloaf.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.KeyboardFocusManager;

import javax.swing.JComponent;
import javax.swing.JTextArea;

import com.beanloaf.view.Thoughts;

public class TabKeyPressed extends KeyAdapter {
    JTextArea textArea;
    Thoughts main;

    public TabKeyPressed(Thoughts main, JTextArea textArea) {
        this.textArea = textArea;
        this.main = main;
    }

    public TabKeyPressed(JTextArea textArea) {
        this.textArea = textArea;
        this.main = null;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        JComponent component = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final String currentFocused = component.getName();

        if (e.getKeyCode() == KeyEvent.VK_TAB
                || (e.getKeyCode() == KeyEvent.VK_ENTER)) {

            if (e.getModifiersEx() > 0) { // Going to previous textbox
                switch (currentFocused) {
                    case "titleLabel":
                        // Do nothing
                        break;
                    case "tagLabel":
                        main.rightPanel.titleLabel.requestFocusInWindow();
                        break;
                    case "bodyLabel":
                        // Do nothing
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

            } else { // Going to next textbox
                switch (currentFocused) {
                    case "titleLabel":
                        main.rightPanel.tagLabel.requestFocusInWindow();
                        break;
                    case "tagLabel":
                        main.rightPanel.bodyLabel.requestFocusInWindow();
                        break;
                    case "bodyLabel":
                        // Do nothing
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            e.consume();
        }
    }
}