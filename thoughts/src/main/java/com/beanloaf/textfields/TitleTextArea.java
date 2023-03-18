package com.beanloaf.textfields;

import com.beanloaf.input.TabKeyPressed;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

import javax.swing.undo.UndoManager;
import java.awt.*;

public class TitleTextArea extends AbstractTextArea {

    private final GhostText ghostText;

    public TitleTextArea(final Thoughts main, final UndoManager undoManager) {
        super(TC.DEFAULT_TITLE, main, undoManager);
        this.setColumns(6);
        this.setOpaque(false);
        this.setFont(TC.Fonts.h1);
        this.setName("titleLabel");
        this.setLayout(new GridBagLayout());

        this.ghostText = new GhostText(TC.DEFAULT_TITLE, TC.Fonts.h1);
        this.add(ghostText, GHOST_TEXT_CONSTRAINTS);
        attachEventHandlers();
    }

    @Override
    public void attachEventHandlers() {
        this.getDocument().putProperty("filterNewlines", true);
        this.addKeyListener(new TabKeyPressed(main));
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
