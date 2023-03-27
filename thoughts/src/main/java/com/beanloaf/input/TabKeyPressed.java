package com.beanloaf.input;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.KeyboardFocusManager;

import javax.swing.JComponent;

import com.beanloaf.view.Thoughts;

public class TabKeyPressed extends KeyAdapter {
    private final Thoughts main;

    public TabKeyPressed(final Thoughts main) {
        super();
        this.main = main;
    }

    @Override
    public void keyPressed(final KeyEvent event) {
        final JComponent component = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final String currentFocused = component.getName();

        if (event.getKeyCode() == KeyEvent.VK_TAB
                || event.getKeyCode() == KeyEvent.VK_ENTER) {

            if (event.getModifiersEx() > 0) { // Going to previous textbox
                switch (currentFocused) {
                    case "titleTextArea":
                        // Do nothing
                        break;
                    case "tagTextArea":
                        main.rightPanel.titleTextArea.requestFocusInWindow();
                        break;
                    case "bodyTextArea":
                        // Do nothing
                        break;
                    default:
                        throw new IllegalArgumentException();
                }

            } else { // Going to next textbox
                switch (currentFocused) {
                    case "titleTextArea":
                        main.rightPanel.tagTextArea.requestFocusInWindow();
                        break;
                    case "tagTextArea":
                        main.rightPanel.bodyTextArea.requestFocusInWindow();
                        break;
                    case "bodyTextArea":
                        // Do nothing
                        break;
                    default:
                        throw new IllegalArgumentException();
                }
            }
            event.consume();
        }
    }
}
