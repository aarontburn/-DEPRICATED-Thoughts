package com.beanloaf.textfields;

import com.beanloaf.input.TabKeyPressed;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

import javax.swing.undo.UndoManager;

public class TagTextArea extends AbstractTextArea {

    public TagTextArea(final Thoughts main, final UndoManager undoManager) {
        super(TC.DEFAULT_TAG, TC.Fonts.h3, main, undoManager);
        this.setColumns(9);
        this.setOpaque(false);
        this.setName("tagTextArea");

    }

    @Override
    void attachEventHandlers() {
        this.getDocument().putProperty("filterNewlines", true);
        this.addKeyListener(new TabKeyPressed(main));
    }

    @Override
    void editEvent() {
        this.main.selectedFile.editTag(this.getText());
    }


}
