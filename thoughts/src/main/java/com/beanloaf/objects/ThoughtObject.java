package com.beanloaf.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.beanloaf.res.TC;

import javax.swing.text.AttributeSet;
import javax.swing.text.StyledDocument;

public class ThoughtObject {

    private String title;
    private String date;
    private String tag;
    private String body;
    private File file;

    private String stylesList;


    public ThoughtObject(final String title,
                         final String date,
                         final String tag,
                         final String body,
                         final File file) {
        this(title, date, tag, body, file, "");
    }

    public ThoughtObject(final String title,
                         final String date,
                         final String tag,
                         final String body,
                         final File file,
                         final String stylesList) {
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.body = body;
        this.file = file;
        this.stylesList = stylesList;
    }

    public void saveFile() {
        if (this.file != null) {
            try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(this.file.toURI()))) {

                final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
                data.put("title", this.title);
                data.put("date", this.date);
                data.put("tag", this.tag);
                data.put("body", this.body);
                data.put("styles", this.stylesList);
                fWriter.write(new JSONObject(data).toJSONString());
            } catch (IOException e) {
                e.printStackTrace();
            }
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

    public void editTitle(final String newTitle) {
        this.title = newTitle;
        saveFile();
    }

    public void editTag(final String newTag) {
        this.tag = newTag;
        saveFile();
    }

    public void editBody(final StyledDocument doc, final String newBody) {
        this.body = newBody;

        final List<JSONObject> styles = new ArrayList<>();
        for (int i = 0; i < doc.getLength(); i++) {
            final AttributeSet attrs = doc.getCharacterElement(i).getAttributes();
            final ConcurrentHashMap<String, String> obj = new ConcurrentHashMap<>();
            final Enumeration<?> e = attrs.getAttributeNames();
            while (e.hasMoreElements()) {
                final Object key = e.nextElement();

                if ((Boolean) attrs.getAttribute(key)) {
                    obj.put(key.toString().substring(0, 1), "t");
                }

            }
            styles.add(new JSONObject(obj));
        }

        this.stylesList = JSONArray.toJSONString(styles);
        saveFile();
    }


    public File getPath() {
        return this.file;
    }

    public void setPath(final File str) {
        this.file = str;
    }

    public String getBody() {

        if (!this.body.isBlank()) {
            return this.body;
        }
        return TC.DEFAULT_BODY;
    }

    public void setBody(final String str) {
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

    public String getStylesList() {
        return this.stylesList;
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
