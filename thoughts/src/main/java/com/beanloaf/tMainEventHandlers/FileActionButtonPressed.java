package com.beanloaf.tMainEventHandlers;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;

import com.beanloaf.main.ThoughtsMain;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.shared.SaveNewFile;

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
        if (this.main.selectedFile == null) {
            return;
        }
        switch (action) {
            case "sort":
                try {
                    String path = this.main.selectedFile.getPath()
                            .toString().split(Pattern.quote(File.separator))[1];

                    if (path.equals("unsorted")) {
                        // Moves the file to sorted
                        this.main.selectedFile.getPath().renameTo(new File(
                                this.main.selectedFile.getPath().toString().replace(
                                        "unsorted", "sorted")));

                        l.setContentFields(0);

                    } else if (path.equals("sorted")) {
                        // Moves file to unsorted
                        this.main.selectedFile.getPath().renameTo(new File(
                                this.main.selectedFile.getPath().toString().replace(
                                        "sorted", "unsorted")));

                        l.setContentFields(0);
                    } else {
                        throw new IllegalArgumentException();
                    }

                } catch (Exception er) {
                    er.printStackTrace();
                }
                break;

            case "delete":
                this.main.selectedFile.getPath().delete();
                l.setContentFields(0);
                break;

            case "newFile":
                ThoughtObject tObj = new SaveNewFile().save();
                l.setContentFields(tObj);
                this.main.leftTabs.setSelectedIndex(0);
                main.selectTitle();
                break;

            default:
                throw new IllegalArgumentException(action + " is not a valid arguemnt in FileActionButtonPressed.");
        }

        this.main.refreshThoughtList();

    }
}