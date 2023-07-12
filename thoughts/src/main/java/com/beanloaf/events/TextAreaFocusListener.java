package com.beanloaf.events;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.text.JTextComponent;

import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class TextAreaFocusListener implements FocusListener {
    private final Thoughts main;
    public TextAreaFocusListener(final Thoughts main) {
        this.main = main;
    }

    @Override
    public void focusGained(final FocusEvent event) {
//        main.leftPanel.validateItemList();
        final JTextComponent j = (JTextComponent) event.getComponent();
        if (j.getText().equals(TC.DEFAULT_TITLE)
                || j.getText().equals(TC.DEFAULT_DATE)
                || j.getText().equals(TC.DEFAULT_TAG)
                || j.getText().equals(TC.DEFAULT_BODY)) {
            j.requestFocusInWindow();
            j.selectAll();
        }
    }

    @Override
    public void focusLost(final FocusEvent event) {
//        main.leftPanel.validateItemList();

        main.refreshThoughtList();

    }
}
