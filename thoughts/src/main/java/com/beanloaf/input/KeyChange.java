package com.beanloaf.input;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.beanloaf.main.ThoughtsMain;
import com.beanloaf.res.TC;

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

        this.main.emptyTitle.setText(((this.main.titleLabel.getText().isBlank())
                ? TC.DEFAULT_TITLE
                : ""));
        this.main.emptyTag.setText(((this.main.tagLabel.getText().isBlank())
                ? TC.DEFAULT_TAG
                : ""));
        this.main.emptyBody.setText(((this.main.bodyLabel.getText().isBlank())
                ? TC.DEFAULT_BODY
                : ""));

    }

    public void textChanged(DocumentEvent e) {
        if (!this.main.ready) {
            return;
        }

        JTextArea j = (JTextArea) e.getDocument().getProperty("labelType");
        String labelName = j.getName();
        switch (labelName) {
            case "titleLabel":
                if (j.getText().isBlank()) {
                    this.main.emptyTitle.setText(TC.DEFAULT_TITLE);
                } else {
                    this.main.emptyTitle.setText("");
                }
                if (this.main.selectedFile != null) {
                    this.main.selectedFile.editTitle(j.getText());
                }
                break;

            case "tagLabel":
                if (j.getText().isBlank()) {
                    this.main.emptyTag.setText(TC.DEFAULT_TAG);
                } else {
                    this.main.emptyTag.setText("");
                }
                if (this.main.selectedFile != null) {
                    this.main.selectedFile.editTag(j.getText());
                }
                break;

            case "bodyLabel":
                if (j.getText().isBlank()) {
                    this.main.emptyBody.setText(TC.DEFAULT_BODY);
                } else {
                    this.main.emptyBody.setText("");
                }
                if (this.main.selectedFile != null) {
                    this.main.selectedFile.editBody(j.getText());

                }
                break;

            default:
                throw new IllegalArgumentException(labelName);
        }
    }

}