package com.beanloaf.tMainEventHandlers;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.beanloaf.common.TC;
import com.beanloaf.main.ThoughtsMain;

public class KeyChange implements DocumentListener {

    public ThoughtsMain main;

    public KeyChange(ThoughtsMain main) {
        this.main = main;
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        textChanged(e);
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        textChanged(e);
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        textChanged(e);
    }

    public void checkEmpty() {
        if (this.main.titleLabel.getText().isBlank()) {
            this.main.emptyTitle.setText(TC.DEFAULT_TITLE);
        } else {
            this.main.emptyTitle.setText("");
        }

        if (this.main.tagLabel.getText().isBlank()) {
            this.main.emptyTitle.setText(TC.DEFAULT_TITLE);
        } else {
            this.main.emptyTag.setText("");
        }

        if (this.main.bodyArea.getText().isBlank()) {
            this.main.emptyTitle.setText(TC.DEFAULT_TITLE);
        } else {
            this.main.emptyBody.setText("");
        }

    }

    public void textChanged(DocumentEvent e) {
        if (!this.main.ready) {
            return;
        }

        JTextArea j = (JTextArea) e.getDocument().getProperty("labelType");
        String labelName = j.getName();
        EditFile fileEdit = new EditFile(this.main);

        switch (labelName) {
            case "titleLabel":
                if (j.getText().isBlank()) {
                    this.main.emptyTitle.setText(TC.DEFAULT_TITLE);
                } else {
                    this.main.emptyTitle.setText("");
                }
                if (this.main.selectedFile != null) {
                    fileEdit.editTitle(j.getText());
                }
                break;

            case "tagLabel":
                if (j.getText().isBlank()) {
                    this.main.emptyTag.setText(TC.DEFAULT_TAG);
                } else {
                    this.main.emptyTag.setText("");
                }
                if (this.main.selectedFile != null) {
                    fileEdit.editTag(j.getText());
                }
                break;

            case "bodyArea":
                if (j.getText().isBlank()) {
                    this.main.emptyBody.setText(TC.DEFAULT_BODY);
                } else {
                    this.main.emptyBody.setText("");
                }
                if (this.main.selectedFile != null) {
                    fileEdit.editBody(j.getText());
                }
                break;

            default:
                throw new IllegalArgumentException(labelName);
        }
    }

}