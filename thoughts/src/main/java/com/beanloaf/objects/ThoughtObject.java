package com.beanloaf.objects;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

import com.beanloaf.tagobjects.TagListItem;
import org.json.simple.JSONObject;

import com.beanloaf.res.TC;


public class ThoughtObject implements Comparable<ThoughtObject> {


    private String dir;
    private String file;

    private String title;
    private String date;
    private String tag;
    private String body;

    private TagListItem parent;

    private boolean isSorted;


    public ThoughtObject(final boolean isSorted,
                         final String title,
                         final String date,
                         final String tag,
                         final String body,
                         final File file) {

        this.isSorted = isSorted;
        this.title = title;
        this.tag = tag;
        this.date = date;
        this.body = body;
        this.file = file == null ? null : file.getName();
        this.dir = isSorted ? TC.Paths.SORTED_DIRECTORY_PATH.toString() : TC.Paths.UNSORTED_DIRECTORY_PATH.toString();
    }

    public ThoughtObject(final String title,
                         final String tag,
                         final String body) {

        this.isSorted = false;
        this.title = title;
        this.tag = tag;
        this.date = getDisplayDateTime();
        this.body = body;
        this.file = createFileName();
        this.dir = isSorted ? TC.Paths.SORTED_DIRECTORY_PATH.toString() : TC.Paths.UNSORTED_DIRECTORY_PATH.toString();
    }




    public void saveFile() {
        if (file == null) {
            return;
        }


        try {
            final File file = new File(this.dir, this.file);
            new File(this.dir).mkdir();

            file.createNewFile();

            try (FileOutputStream fWriter = new FileOutputStream(file)) {

                final ConcurrentHashMap<String, Object> data = new ConcurrentHashMap<>();
                data.put("title", this.title);
                data.put("date", this.date);
                data.put("tag", this.tag);
                data.put("body", this.body);
                fWriter.write(new JSONObject(data).toJSONString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
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


    public void sort() {
        if (file == null) {
            return;
        }

        final String[] path = this.dir.split(Pattern.quote(File.separator));

        final File f = new File(this.dir, this.file);

        System.out.println(f.toString());


        switch (path[path.length - 1]) {
            case "unsorted": // unsorted -> sorted
                this.dir = this.dir.replace("unsorted", "sorted");

                f.renameTo(new File(this.dir, this.file));
                this.isSorted = true;

                break;
            case "sorted": // sorted -> unsorted
                this.dir = this.dir.replace("sorted", "unsorted");
                f.renameTo(new File(this.dir, this.file));
                this.isSorted = false;

                break;
            default:
                throw new RuntimeException("Attempting to sort an invalid file path..." + this.dir);

        }

    }

    public void delete() {
        if (file == null) {
            return;
        }
        new File(this.dir, this.file).delete();

    }


    /* Mutators */

    public void setParent(final TagListItem parent) {
        this.parent = parent;
    }


    public void setTitle(final String title) {
        this.title = title.isEmpty() ? TC.DEFAULT_TITLE : title;
        saveFile();
    }

    public void setTag(final String tag) {
        this.tag = tag.isEmpty() ? TC.DEFAULT_TAG : tag;
        saveFile();

    }

    public void setBody(final String body) {
        this.body = body.isEmpty() ? TC.DEFAULT_BODY : body;
        saveFile();
    }
    /* --------------------------- */




    /* Accessors */
    public String getTitle() {
        return this.title.isBlank() ? TC.DEFAULT_TITLE : this.title;
    }

    public String getBody() {
        return this.body.isBlank() ? TC.DEFAULT_BODY : this.body;
    }

    public String getTag() {
        return this.tag.isBlank() ? TC.DEFAULT_TAG : this.tag;
    }

    public String getDate() {
        return this.date;
    }

    public boolean isSorted() {
        return this.isSorted;
    }

    public String getFile() {
        return this.file;
    }

    public TagListItem getParent() {
        return this.parent;
    }
    /* -------------------- */

    private String getDisplayDateTime() {
        final Date d = new Date();
        final SimpleDateFormat contentDate = new SimpleDateFormat("MM/dd/yyyy");
        final SimpleDateFormat contentTime = new SimpleDateFormat("HH:mm:ss");

        return String.format("%s at %s", contentDate.format(d), contentTime.format(d));
    }

    private String createFileName() {
        final Date d = new Date();
        final SimpleDateFormat fileFormat = new SimpleDateFormat("MM-dd-yyyy HH-mm-ss");

        return fileFormat.format(d) + ".json";
    }



    @Override
    public String toString() {
        return "[Title: " + title + " Tag: " + tag + " Body: " + body + " Date/Time: " + date + " File: " + file + "]";
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        final ThoughtObject that = (ThoughtObject) obj;
        return Objects.equals(dir, that.dir)
                && Objects.equals(file, that.file)
                && Objects.equals(title, that.title)
                && Objects.equals(tag, that.tag)
                && Objects.equals(body, that.body)
                && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dir, file, title, tag, body, date);
    }



    @Override
    public int compareTo(final ThoughtObject thoughtObject) {
        return this.title.compareToIgnoreCase(thoughtObject.getTitle());
    }
}
