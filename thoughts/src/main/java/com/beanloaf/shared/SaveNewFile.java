package com.beanloaf.shared;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beanloaf.common.TC;
import com.beanloaf.objects.ThoughtObject;

public class SaveNewFile {

    private final String title;
    private final String tag;
    private final String body;

    public SaveNewFile(String title, String tag, String body) {
        this.title = title;
        this.tag = tag;
        this.body = body;
    }

    public SaveNewFile(String type, String input) {
        switch (type) {
            case "title":
                this.title = input;
                this.tag = TC.DEFAULT_TAG;
                this.body = TC.DEFAULT_BODY;
                break;

            case "tag":
                this.title = TC.DEFAULT_TITLE;
                this.tag = input;
                this.body = TC.DEFAULT_BODY;
                break;

            case "body":
                this.title = TC.DEFAULT_TITLE;
                this.tag = TC.DEFAULT_TAG;
                this.body = input;
                break;

            default:
                throw new IllegalArgumentException();
        }
    }

    public SaveNewFile() {
        this.title = "";
        this.tag = "";
        this.body = "";
    }

    public ThoughtObject save() {

        if (title.equalsIgnoreCase(TC.DEFAULT_TITLE) && body.equalsIgnoreCase(TC.DEFAULT_BODY)) {
            return null;
        }
        String[] dateTime = getCurrentDateTime().split("!");
        String fileDT = dateTime[0];
        String contentDT = dateTime[1];
        String unsortedFileName = TC.UNSORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
        File newFile = new File(unsortedFileName);
        try {
            if (newFile.createNewFile()) {
                ThoughtObject tObj = new ThoughtObject(title, contentDT, tag, body, newFile);
                tObj.saveFile();
                return tObj;
            }
        } catch (IOException er) {
            er.printStackTrace();
        }


        return null;
    }

    // public ThoughtObject save() {
    // if (title.equalsIgnoreCase(TC.DEFAULT_TITLE)
    // && body.equalsIgnoreCase(TC.DEFAULT_BODY)) {
    // return null;
    // }
    // String fileDT = getCurrentDateTime().split("!")[0];
    // String contentDT = getCurrentDateTime().split("!")[1];
    // try {
    // String unsortedFileName = TC.UNSORTED_DIRECTORY_PATH + "/" + fileDT +
    // ".json";
    // File newFile = new File(unsortedFileName);
    // if (newFile.createNewFile()) {
    // ThoughtObject tObj = new ThoughtObject(
    // title,
    // contentDT,
    // tag,
    // body,
    // newFile);

    // tObj.saveFile();
    // return tObj;
    // }
    // } catch (IOException er) {
    // er.printStackTrace();
    // }
    // return null;
    // }

    private String getCurrentDateTime() {
        Date d = new Date();
        SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");
        SimpleDateFormat contentDate = new SimpleDateFormat("MM/dd/yyyy");
        SimpleDateFormat contentTime = new SimpleDateFormat("HH:mm:ss");

        return String.format("%s!%s at %s",
                fileFormat.format(d), contentDate.format(d), contentTime.format(d));
    }

}
