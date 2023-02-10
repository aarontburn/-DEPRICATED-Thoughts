package com.beanloaf.tMainEventHandlers;

import com.beanloaf.main.ThoughtsMain;

public class EditFile {
    private final ThoughtsMain thoughtsMain;

    public EditFile(ThoughtsMain thoughtsMain) {
        this.thoughtsMain = thoughtsMain;
    }

    public void editTitle(String newTitle) {
        this.thoughtsMain.selectedFile.editTitle(newTitle);
    }

    public void editTag(String newTag) {
        this.thoughtsMain.selectedFile.editTag(newTag);
    }

    public void editBody(String newBody) {
        this.thoughtsMain.selectedFile.editBody(newBody);
    }
}