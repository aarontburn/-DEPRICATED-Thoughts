package com.beanloaf.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import com.beanloaf.tagobjects.TagListItem;
import org.json.simple.JSONObject;

import com.beanloaf.res.TC;

import javax.swing.text.html.HTML;

public class ThoughtObject {

    private String title;
    private String date;
    private String tag;
    private String body;
    private File file;

    private TagListItem parent;


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
        if (file == null) {
            return;
        }

        try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(this.file.toURI()))) {
            final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
            data.put("title", this.title);
            data.put("date", this.date);
            data.put("tag", this.tag);
            data.put("body", this.body);
            fWriter.write(new JSONObject(data).toJSONString());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void exportAsText(final String exportDirectory) {
        if (exportDirectory == null) {
            return;
        }
        try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(exportDirectory + getTitle() + ".txt"))) {
            fWriter.write("Title: " + getTitle() + "\n"
                    + "Tag: " + getTag() + "\n"
                    + "Created on: " + getDate() + "\n\n\n\n"
                    + getBody());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setParent(final TagListItem parent) {
        this.parent = parent;
    }

    public TagListItem getParent() {
        return this.parent;
    }

    public void editTitle(final String newTitle) {
        this.title = newTitle;
        saveFile();
    }

    public void editTag(final String newTag) {
        this.tag = newTag;
        saveFile();
    }

    public void editBody(final String newBody) {
        this.body = newBody;
        saveFile();
    }


    public File getPath() {
        return this.file;
    }

    public String getBody() {
        return this.body.isBlank() ? TC.DEFAULT_BODY : this.body;
    }

    public void setBody(final String str) {
        this.body = str;
    }

    public String getTitle() {
        return this.title.isBlank() ? TC.DEFAULT_TITLE : this.title;
    }

    public String getTag() {
        return this.tag.isBlank() ? TC.DEFAULT_TAG : this.tag;
    }

    public void setName(final String title) {
        this.title = title;
    }

    public void setTag(final String tag) {
        this.tag = tag;
    }

    public void setDate(final String date) {
        this.date = date;
    }

    public String getDate() {
        return this.date;
    }

    @Override
    public boolean equals(final Object otherObject) {
        if (otherObject.getClass() != this.getClass()) {
            return false;
        }
        final ThoughtObject other = (ThoughtObject) otherObject;
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
