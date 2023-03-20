package com.beanloaf.textfields;

import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

import javax.swing.undo.UndoManager;
import java.awt.*;

public class BodyTextArea extends AbstractTextArea {

    public BodyTextArea(final Thoughts main, final UndoManager undoManager) {
        super(TC.DEFAULT_BODY, TC.Fonts.p, main, undoManager);
        this.setTabSize(2);


        if (main.settings.isLightMode()) {
            this.setBackground(Color.LIGHT_GRAY);
        } else {
            this.setBackground(new Color(32, 32, 32));
        }

        this.setLineWrap(true);
        this.setWrapStyleWord(true);

    }


    @Override
    void attachEventHandlers() {

    }

    @Override
    void editEvent() {
        this.main.selectedFile.editBody(this.getText());
    }


}
