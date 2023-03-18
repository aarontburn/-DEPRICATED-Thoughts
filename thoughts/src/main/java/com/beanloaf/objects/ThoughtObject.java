package com.beanloaf.objects;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import org.json.simple.JSONObject;

import com.beanloaf.res.TC;

public class ThoughtObject {

    private String title;
    private String date;
    private String tag;
    private String body;
    private File file;

    public ThoughtObject(final String title,
                         final String date,
                         final String tag,
                         final String body,
                         final File file) {
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.body = body;
        this.file = file;
    }

    public void saveFile() {
        if (this.file != null) {
            try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(this.file.toURI()))) {
                HashMap<String, String> textContent = new HashMap<>();
                textContent.put("title", this.title);
                textContent.put("date", this.date);
                textContent.put("tag", this.tag);
                textContent.put("body", this.body);
                fWriter.write(new JSONObject(textContent).toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("here");
        }

    }

    public void editTitle(final String newTitle) {
        this.title = newTitle;
        saveFile();
    }

    public void editTag(final String newTag) {
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
    public boolean equals(Object otherObject) {
        if (otherObject.getClass() != this.getClass() || otherObject == null) {
            return false;
        }
        ThoughtObject other = (ThoughtObject) otherObject;
        return this.getTitle().equals(other.getTitle())
                && this.getDate().equals(other.getDate())
                && this.getBody().equals(other.getBody())
                && this.getTag().equals(other.getTag())
                && this.getPath().equals(other.getPath());

    }

    @Override
    public int hashCode() {
        return Objects.hash(this.title, this.date, this.tag, this.body, this.file);
    }

    @Override
    public String toString() {
        return String.format("<%s> <%s>", this.title, this.tag);
    }

}