package com.beanloaf.textfields;

import com.beanloaf.res.TC;
import com.beanloaf.view.ThoughtsMain;

import javax.swing.undo.UndoManager;
import java.awt.*;

public class BodyTextArea extends TextArea {

    private final GhostText ghostText;

    public BodyTextArea(final ThoughtsMain main, final UndoManager undoManager) {
        super(TC.DEFAULT_BODY, main, undoManager);
        this.setTabSize(2);
        this.setBackground(new Color(32, 32, 32));
        this.setFont(TC.Fonts.p);
        this.setLineWrap(true);
        this.setWrapStyleWord(true);
        this.setName("bodyLabel");
        this.setLayout(new GridBagLayout());

        ghostText = new GhostText(TC.DEFAULT_BODY, TC.Fonts.p);
        this.add(ghostText, GHOST_TEXT_CONSTRAINTS);

    }


    @Override
    void attachEventHandlers() {

    }

    @Override
    public void textChanged() {
        if (this.main.ready) {
            ghostText.setDisplay(this.getText().isBlank());
            if (this.main.selectedFile != null) {
                this.main.selectedFile.editTitle(this.getText());
            }
        }
    }

}
