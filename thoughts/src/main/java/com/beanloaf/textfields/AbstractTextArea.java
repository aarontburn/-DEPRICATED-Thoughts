package com.beanloaf.textfields;

import com.beanloaf.events.TextAreaFocusListener;
import com.beanloaf.objects.GBC;
import com.beanloaf.view.Thoughts;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.undo.UndoManager;
import java.awt.Font;
import java.awt.GridBagLayout;

public abstract class AbstractTextArea extends JTextArea implements DocumentListener {

    public final Thoughts main;
    public final UndoManager undoManager;
    private final GhostText ghostText;

    public AbstractTextArea(final String text, final Font font, final Thoughts main, final UndoManager undoManager) {
        super(text);
        this.main = main;
        this.undoManager = undoManager;
        this.setLayout(new GridBagLayout());
        this.setFont(font);

        defaultDocumentSettings();
        attachEventHandlers();

        ghostText = new GhostText(text, font);
        this.add(ghostText, new GBC().setAnchor(GBC.Anchor.NORTHWEST));

    }

    private void defaultDocumentSettings() {
        this.getDocument().addDocumentListener(this);
        this.getDocument().putProperty("labelType", this);
        this.getDocument().addUndoableEditListener(undoManager);
        this.addFocusListener(new TextAreaFocusListener(this.main));

    }

    abstract void attachEventHandlers();

    abstract void editEvent();

    public void textChanged() {
        if (this.main.ready) {
            ghostText.setDisplay(this.getText().isBlank());
            if (this.main.selectedFile != null) {
                editEvent();
            }
        }




    }

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
