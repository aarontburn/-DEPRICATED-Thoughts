package com.beanloaf.textfields;

import com.beanloaf.input.TabKeyPressed;
import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;
import com.beanloaf.view.ThoughtsMain;

import javax.swing.event.DocumentEvent;
import javax.swing.undo.UndoManager;
import java.awt.*;

public class TagTextArea extends TextArea {
    private final GhostText ghostText;

    public TagTextArea(final ThoughtsMain main, final UndoManager undoManager) {
        super(TC.DEFAULT_TAG, main, undoManager);
        this.setLayout(new GridBagLayout());
        this.setColumns(9);
        this.setOpaque(false);
        this.setFont(TC.Fonts.h3);
        this.setName("tagLabel");

        ghostText = new GhostText(TC.DEFAULT_TAG, TC.Fonts.h3);
        this.add(ghostText, new GBC(0, 0, 0.1, 0.1)
                .setAnchor(GridBagConstraints.NORTHWEST));
        attachEventHandlers();

    }

    @Override
    void attachEventHandlers() {
        this.getDocument().putProperty("filterNewlines", true);
        this.addKeyListener(new TabKeyPressed(this.main, this));
    }

    @Override
    void textChanged() {
        if (this.main.ready) {
            ghostText.setDisplay(this.getText().isBlank());
            if (this.main.selectedFile != null) {
                this.main.selectedFile.editTitle(this.getText());
            }
        }
    }



    @Override
    public void insertUpdate(DocumentEvent e) {
        textChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        textChanged();
    }
}
