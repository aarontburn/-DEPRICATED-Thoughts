package com.beanloaf.input;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.regex.Pattern;

import javax.swing.JButton;

import com.beanloaf.events.SaveNewFile;
import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

public class FileActionButtonPressed implements ActionListener {
    private final Thoughts main;


    public FileActionButtonPressed(final Thoughts main) {
        this.main = main;
    }

    @Override
    public void actionPerformed(final ActionEvent event) {
        final JButton component = (JButton) event.getSource();
        final String action = component.getName();

        final ThoughtObject obj = main.selectedFile;


        switch (action) {
            case "sort" -> {
                try {
                    if (obj == null) {
                        return;
                    }
                    final String path = obj.getPath()
                            .toString().split(Pattern.quote(File.separator))[2];

                    if ("unsorted".equals(path)) {
                        // Moves the file to sorted
                        final String filePath = obj.getPath().toString()
                                .replace("unsorted", "sorted");


                        obj.getPath().renameTo(new File(filePath));
                        main.db.addEntryIntoDatabase(obj);



                        l.setContentFields(0);

                    } else if ("sorted".equals(path)) {
                        // Moves file to unsorted
                        final String filePath = obj.getPath().toString()
                                .replace("sorted", "unsorted");

                        main.db.removeEntryFromDatabase(obj);
                        obj.getPath().renameTo(new File(filePath));
                        


                        l.setContentFields(0);
                    } else {
                        throw new IllegalArgumentException(path);
                    }

                } catch (Exception er) {
                    er.printStackTrace();
                }
            }
            case "delete" -> {

                if (obj == null) {
                    return;
                }
                final String path = obj.getPath()
                        .toString().split(Pattern.quote(File.separator))[2];
                if ("sorted".equals(path)) {
                    this.main.db.removeEntryFromDatabase(obj);
                }
                obj.getPath().delete();


                ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.TEXT, obj);

            }
            case "newFile" -> {
                String title, tag, body;
                title = tag = body = "";
                if (main.settings.isTitleLocked()) {
                    title = main.rightPanel.titleTextArea.getText();
                }
                if (main.settings.isTagLocked()) {
                    tag = main.rightPanel.tagTextArea.getText();
                }
                if (main.settings.isBodyLocked()) {
                    body = main.rightPanel.bodyTextArea.getText();
                }
                final ThoughtObject tObj = new SaveNewFile(title, tag, body).save();
                l.setContentFields(tObj);
//                this.main.leftPanel.leftTabs.setSelectedIndex(0);
                ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.FOCUS_TITLE_FIELD);
            }
            default ->
                    throw new IllegalArgumentException(action + " is not a valid argument in FileActionButtonPressed.");
        }

        this.main.refreshThoughtList();

    }
}
