package com.beanloaf.input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;

import com.beanloaf.events.SaveNewFile;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.view.ThoughtsMain;

public class FileActionButtonPressed implements ActionListener {
    private final ThoughtsMain main;

    public FileActionButtonPressed(ThoughtsMain main) {
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton component = (JButton) e.getSource();
        String action = component.getName();
        ListItemPressed l = new ListItemPressed(this.main);

        switch (action) {
            case "sort" -> {
                try {
                    if (this.main.selectedFile == null) {
                        return;
                    }
                    String path = this.main.selectedFile.getPath()
                            .toString().split(Pattern.quote(File.separator))[2];

                    if (path.equals("unsorted")) {
                        // Moves the file to sorted
                        final String filePath = this.main.selectedFile.getPath().toString()
                                .replace("unsorted", "sorted");

                        this.main.selectedFile.getPath().renameTo(new File(filePath));
                        l.setContentFields(0);

                    } else if (path.equals("sorted")) {
                        // Moves file to unsorted
                        final String filePath = this.main.selectedFile.getPath().toString()
                                .replace("sorted", "unsorted");

                        this.main.selectedFile.getPath().renameTo(new File(filePath));

                        l.setContentFields(0);
                    } else {
                        throw new IllegalArgumentException(path);
                    }

                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
            case "delete" -> {
                if (this.main.selectedFile == null) {
                    return;
                }
                String path = this.main.selectedFile.getPath()
                        .toString().split(Pattern.quote(File.separator))[2];
                if (path.equals("sorted")) {
                    this.main.db.delete(this.main.selectedFile);
                }
                this.main.selectedFile.getPath().delete();
                l.setContentFields(0);
            }
            case "newFile" -> {
                String title, tag, body;
                title = tag = body = "";
                if (main.settings.isTitleLocked()) {
                    title = main.rightPanel.titleLabel.getText();
                }
                if (main.settings.isTagLocked()) {
                    tag = main.rightPanel.tagLabel.getText();
                }
                if (main.settings.isBodyLocked()) {
                    body = main.rightPanel.bodyLabel.getText();
                }
                ThoughtObject tObj = new SaveNewFile(title, tag, body).save();
                l.setContentFields(tObj);
                this.main.leftTabs.setSelectedIndex(0);
                this.main.selectTextField(this.main.rightPanel.titleLabel);
            }
            default ->
                    throw new IllegalArgumentException(action + " is not a valid arguemnt in FileActionButtonPressed.");
        }

        this.main.refreshThoughtList();

    }
}