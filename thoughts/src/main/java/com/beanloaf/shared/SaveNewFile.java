package com.beanloaf.shared;

import java.io.File;
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

    public SaveNewFile() {
        this("", "", "");
    }

    public ThoughtObject save() {
        if (title.equalsIgnoreCase(TC.DEFAULT_TITLE) && body.equalsIgnoreCase(TC.DEFAULT_BODY)) {
            return null;
        }
        final String[] dateTime = getCurrentDateTime().split("!");
        final String fileDT = dateTime[0];
        final String contentDT = dateTime[1];
        final String unsortedFileName = TC.UNSORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
        final String sortedFileName = TC.SORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
        final File newFile = new File(unsortedFileName);

        try {
            if (!newFile.isFile() && !new File(sortedFileName).isFile()) {
                newFile.createNewFile();
                ThoughtObject tObj = new ThoughtObject(title, contentDT, tag, body, newFile);
                tObj.saveFile();
                return tObj;
            } else {

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
