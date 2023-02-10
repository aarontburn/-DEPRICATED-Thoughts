package com.beanloaf.events;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;

import com.beanloaf.main.ThoughtsMain;
import com.beanloaf.objects.ThoughtObject;

public class FileActionButtonPressed implements ActionListener {
    ThoughtsMain main;

    public FileActionButtonPressed(ThoughtsMain main) {
        this.main = main;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton component = (JButton) e.getSource();
        String action = component.getName();
        ListItemPressed l = new ListItemPressed(this.main);

        switch (action) {
            case "sort":
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
                break;

            case "delete":
                if (this.main.selectedFile == null) {
                    return;
                }
                this.main.selectedFile.getPath().delete();
                l.setContentFields(0);
                break;

            case "newFile":
                String title, tag, body;
                title = tag = body = "";

                if (!main.settings.isTitleLocked()) {
                    title = main.titleLabel.getText();
                }

                if (!main.settings.isTagLocked()) {
                    tag = main.tagLabel.getText();
                }

                if (!main.settings.isBodyLocked()) {
                    body = main.bodyLabel.getText();
                }

                ThoughtObject tObj = new SaveNewFile(title, tag, body).save();
                l.setContentFields(tObj);
                this.main.leftTabs.setSelectedIndex(0);
                main.selectTextField(main.titleLabel);
                break;

            default:
                throw new IllegalArgumentException(action + " is not a valid arguemnt in FileActionButtonPressed.");
        }

        this.main.refreshThoughtList();

    }
}