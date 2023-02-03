package com.beanloaf.objects;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;

import com.beanloaf.common.TC;

public class ThoughtObject {

    private String title;
    private String date;
    private String tag;
    private String body;
    private File file;

    public ThoughtObject(String title, String date, String tag, String body, File file) {
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.body = body;
        this.file = file;
    }
    

    public void saveFile() {
        try {
            FileWriter fWriter = new FileWriter(this.file);
            HashMap<String, String> textContent = new HashMap<String, String>();
            textContent.put("title", this.title);
            textContent.put("date", this.date);
            textContent.put("tag", this.tag);
            textContent.put("body", this.body);
            JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());
            fWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void editTitle(String newTitle) {
        this.title = newTitle;
        saveFile();
    }

    public void editTag(String newTag) {
        this.tag = newTag;
        saveFile();
    }

    public void editBody(String newBody) {
        this.body = newBody;
        saveFile();
    }

    public File getPath() {
        return this.file;
    }

    public void setPath(File str) {
        this.file = str;
    }

    public String getBody() {

        if (!this.body.isBlank()) {
            return this.body;
        }
        return TC.DEFAULT_BODY;
    }

    public void setBody(String str) {
        this.body = str;
    }

    public String getTitle() {
        if (!this.title.isBlank()) {
            return this.title;
        }
        return TC.DEFAULT_TITLE;
    }

    public String getTag() {
        if (!this.tag.isBlank()) {
            return this.tag;
        }
        return TC.DEFAULT_TAG;
    }

    public void setName(String title) {
        this.title = title;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    @Override
    public String toString() {
        return String.format("<%s> <%s> <%s>", this.title, this.tag, this.body);
    }

}