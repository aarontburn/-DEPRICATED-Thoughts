package com.beanloaf.shared;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beanloaf.common.TC;
import com.beanloaf.objects.ThoughtObject;

public class SaveNewFile {

    private String title;
    private String date;
    private String tag;
    private String body;
    private String fileName;

    public SaveNewFile(String title, String tag, String body) {
        this.title = title;
        this.tag = tag;
        this.body = body;
        this.date = "";
        this.fileName = "";
    }

    public SaveNewFile(String title, String tag, String body, String date) {
        this.title = title;
        this.tag = tag;
        this.body = body;
        this.date = date;
        this.fileName = "";
    }

    public SaveNewFile(String title, String tag, String body, String date, String file) {
        this.title = title;
        this.tag = tag;
        this.body = body;
        this.date = date;
        this.fileName = file;
    }

    public SaveNewFile(ThoughtObject tObj) {
        this.title = tObj.getTitle();
        this.tag = tObj.getTag();
        this.body = tObj.getBody();
        this.date = tObj.getDate();
        this.fileName = tObj.getPath().getName();
    }

    public SaveNewFile() {
        this("", "", "");
    }

    public ThoughtObject save() {
        if (title.equalsIgnoreCase(TC.DEFAULT_TITLE) && body.equalsIgnoreCase(TC.DEFAULT_BODY)) {
            return null;
        }

        final String[] dateTime = getCurrentDateTime().split("!");
        String fileDT = dateTime[0];

        if (this.date.isEmpty()) {
            this.date = dateTime[1];
        }

        final String unsortedFileName = TC.UNSORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
        final String sortedFileName = TC.SORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
        final File newFile = new File(unsortedFileName);

        try {
            if (!newFile.isFile() && !new File(sortedFileName).isFile()) {
                newFile.createNewFile();
                ThoughtObject tObj = new ThoughtObject(this.title, this.date, this.tag, this.body, newFile);
                tObj.saveFile();
                return tObj;
            }

        } catch (Exception er) {
            er.printStackTrace();
        }
        return null;
    }

    public ThoughtObject fbSave() {
        if (this.fileName.isEmpty()) {
            return null;
        }
        final String unsortedFileName = TC.UNSORTED_DIRECTORY_PATH + "/" + fileName;
        final String sortedFileName = TC.SORTED_DIRECTORY_PATH + "/" + fileName;
        final File newFile = new File(sortedFileName);

        try {
            if (!newFile.isFile() && !new File(unsortedFileName).isFile()) {
                newFile.createNewFile();
                ThoughtObject tObj = new ThoughtObject(this.title, this.date, this.tag, this.body, newFile);
                tObj.saveFile();
                return tObj;
            } else {
                System.err.println("File already exists. Skipping...");
            }

        } catch (Exception er) {
            er.printStackTrace();
        }
        return null;
    }

    private String getCurrentDateTime() {
        Date d = new Date();
        SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");
        SimpleDateFormat contentDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat contentTime = new SimpleDateFormat("HH:mm:ss");

        return String.format("%s!%s at %s",
                fileFormat.format(d), contentDate.format(d), contentTime.format(d));
    }

}
