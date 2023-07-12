package com.beanloaf.events;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

public class SaveNewFile {
//
//    private final String title;
//    private String date;
//    private final String tag;
//    private final String body;
//    private final String fileName;
//
//
//    public SaveNewFile(final String title,
//                       final String tag,
//                       final String body) {
//        this(title, tag, body, "", "");
//    }
//
//    public SaveNewFile(final String title,
//                       final String tag,
//                       final String body,
//                       final String date,
//                       final String file) {
//        this.title = title;
//        this.tag = tag;
//        this.body = body;
//        this.date = date;
//        this.fileName = file;
//
//    }
//
//    public SaveNewFile(final ThoughtObject tObj) {
//        this.title = tObj.getTitle();
//        this.tag = tObj.getTag();
//        this.body = tObj.getBody();
//        this.date = tObj.getDate();
////        this.fileName = tObj.getFile().getName();
//
//
//    }
//
//    public ThoughtObject save() {
//        if (title.equalsIgnoreCase(TC.DEFAULT_TITLE) && body.equalsIgnoreCase(TC.DEFAULT_BODY)) {
//            return null;
//        }
//
//        final String[] dateTime = getCurrentDateTime().split("!");
//        final String fileDT = dateTime[0];
//
//        if (this.date.isEmpty()) {
//            this.date = dateTime[1];
//        }
//
//        final String unsortedFileName = TC.Paths.UNSORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
//        final String sortedFileName = TC.Paths.SORTED_DIRECTORY_PATH + "/" + fileDT + ".json";
//        final File newFile = new File(unsortedFileName);
//
//        try {
//            if (!newFile.isFile() && !new File(sortedFileName).isFile()) {
//                newFile.createNewFile();
//                final ThoughtObject tObj = new ThoughtObject(this.title, this.date, this.tag, this.body, newFile);
//                tObj.saveFile();
//                return tObj;
//            }
//
//        } catch (Exception er) {
//            er.printStackTrace();
//        }
//        return null;
//    }
//
//    public ThoughtObject fbSave() {
//        if (this.fileName.isEmpty()) {
//            return null;
//        }
//        final String unsortedFileName = TC.Paths.UNSORTED_DIRECTORY_PATH + "/" + fileName;
//        final String sortedFileName = TC.Paths.SORTED_DIRECTORY_PATH + "/" + fileName;
//        final File newFile = new File(sortedFileName);
//
//        try {
//            if (!newFile.isFile() && !new File(unsortedFileName).isFile()) {
//                newFile.createNewFile();
//                final ThoughtObject tObj = new ThoughtObject(this.title, this.date, this.tag, this.body, newFile);
//                tObj.saveFile();
//                return tObj;
//            } else {
//                System.err.println("File already exists. Skipping...");
//            }
//
//        } catch (Exception er) {
//            er.printStackTrace();
//        }
//        return null;
//    }
//
//    private String getCurrentDateTime() {
//        final Date d = new Date();
//        final SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");
//        final SimpleDateFormat contentDate = new SimpleDateFormat("MM/dd/yyyy");
//        final SimpleDateFormat contentTime = new SimpleDateFormat("HH:mm:ss");
//
//        return String.format("%s!%s at %s",
//                fileFormat.format(d), contentDate.format(d), contentTime.format(d));
//    }

}
