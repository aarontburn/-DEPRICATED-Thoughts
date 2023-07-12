package com.beanloaf.textfields;

import com.beanloaf.input.TabKeyPressed;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

import javax.swing.undo.UndoManager;

public class TitleTextArea extends AbstractTextArea {

    public TitleTextArea(final Thoughts main, final UndoManager undoManager) {
        super(TC.DEFAULT_TITLE, TC.Fonts.h1, main, undoManager);
        this.setColumns(6);
        this.setOpaque(false);
        this.setName("titleTextArea");

    }

    @Override
    public void attachEventHandlers() {
        this.getDocument().putProperty("filterNewlines", true);
        this.addKeyListener(new TabKeyPressed(main));
    }

    @Override
    void editEvent() {
        this.main.selectedFile.setTitle(this.getText());
    }

}
