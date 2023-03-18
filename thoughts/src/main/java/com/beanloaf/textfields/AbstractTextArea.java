package com.beanloaf.textfields;

import com.beanloaf.events.TextAreaFocusListener;
import com.beanloaf.objects.GBC;
import com.beanloaf.view.Thoughts;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.*;

public abstract class AbstractTextArea extends JTextArea implements DocumentListener {

    public static final GBC GHOST_TEXT_CONSTRAINTS = new GBC(0, 0, 0.1, 0.1)
            .setAnchor(GridBagConstraints.NORTHWEST);

    public final Thoughts main;
    public final UndoManager undoManager;

    public AbstractTextArea(final String text, final Thoughts main, final UndoManager undoManager) {
        super(text);
        this.main = main;
        this.undoManager = undoManager;
        defaultDocumentSettings();
        attachEventHandlers();


    }

    private void defaultDocumentSettings() {
        this.getDocument().addDocumentListener(this);
        this.getDocument().putProperty("labelType", this);
        this.getDocument().addUndoableEditListener(undoManager);
        this.addFocusListener(new TextAreaFocusListener(this.main));

    }

    abstract void attachEventHandlers();

    abstract void textChanged();

    @Override
    public void insertUpdate(final DocumentEvent event) {
        textChanged();
    }

    @Override
    public void removeUpdate(final DocumentEvent event) {
        textChanged();
    }

    @Override
    public void changedUpdate(final DocumentEvent event) {
        textChanged();
    }
}
