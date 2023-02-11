package com.beanloaf.events;

import java.awt.Dimension;
import java.awt.Point;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.res.TC;

public class SettingsHandler {
    // Setting fields
    private boolean isDarkMode;
    private boolean isMaximized;
    private double windowWidth;
    private double windowHeight;
    private double windowX;
    private double windowY;
    private boolean lockTitle;
    private boolean lockTag;
    private boolean lockBody;
    private boolean pushOnClose;
    private boolean pullOnStartup;

    public SettingsHandler() {
        try {
            readFileContents();
        } catch (Exception e) {
            System.err.println("Error in settings.json. Regenerating file...");
            if (TC.SETTINGS_DIRECTORY.delete()) {
                System.err.println("Successfully deleted settings.json.");
            }
        }
        check();
    }

    public void check() {
        if (TC.SETTINGS_DIRECTORY.isFile()) {
            return;
        } else {
            System.err.println("Creating new settings.json");
            // Default values
            this.isDarkMode = true;
            this.isMaximized = true;
            this.windowWidth = 1650;
            this.windowHeight = 1080;
            this.windowX = 0;
            this.windowY = 0;
            this.lockTitle = false;
            this.lockTag = false;
            this.lockBody = false;
            this.pushOnClose = false;
            this.pullOnStartup = false;
            createSettingsFile();
        }
    }

    public void createSettingsFile() {
        try {
            TC.SETTINGS_DIRECTORY.createNewFile();
            FileWriter fWriter = new FileWriter(TC.SETTINGS_DIRECTORY);
            HashMap<String, Object> textContent = new HashMap<String, Object>();

            textContent.put("isDarkMode", this.isDarkMode);
            textContent.put("isMaximized", this.isMaximized);
            textContent.put("windowWidth", this.windowHeight);
            textContent.put("windowHeight", this.windowWidth);
            textContent.put("windowX", this.windowX);
            textContent.put("windowY", this.windowY);
            textContent.put("lockTitle", this.lockTitle);
            textContent.put("lockTag", this.lockTag);
            textContent.put("lockBody", this.lockBody);
            textContent.put("pushOnClose", this.pushOnClose);
            textContent.put("pullOnStartup", this.pullOnStartup);

            JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());
            fWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void changePushOnClose(boolean b) {
        this.pushOnClose = b;
        createSettingsFile();
    }

    public void changePullOnStartup(boolean b) {
        this.pullOnStartup = b;
        createSettingsFile();
    }

    public void changeWindowPosition(Point point) {
        this.windowX = point.getX();
        this.windowY = point.getY();
        createSettingsFile();
    }

    public void changeIsDarkMode(boolean b) {
        this.isDarkMode = b;
        createSettingsFile();
    }

    public void changeIsMaximized(boolean b) {
        this.isMaximized = b;
        createSettingsFile();
    }

    public void changeWindowDimension(Dimension newDimension) {
        this.windowWidth = newDimension.getWidth();
        this.windowHeight = newDimension.getHeight();
        createSettingsFile();
    }

    public void changeLockTitle(boolean b) {
        this.lockTitle = b;
        createSettingsFile();
    }

    public void changeLockTag(boolean b) {
        this.lockTag = b;
        createSettingsFile();
    }

    public void changeLockBody(boolean b) {
        this.lockBody = b;
        createSettingsFile();
    }

    public boolean isPushOnClose() {
        return this.pushOnClose;
    }

    public boolean isPullOnStartup() {
        return this.pullOnStartup;
    }

    public boolean isTitleLocked() {
        return this.lockTitle;
    }

    public boolean isTagLocked() {
        return this.lockTag;
    }

    public boolean isBodyLocked() {
        return this.lockBody;
    }

    public int getWindowX() {
        return (int) this.windowX;
    }

    public int getWindowY() {
        return (int) this.windowY;
    }

    public int getWindowWidth() {
        return (int) this.windowWidth;
    }

    public int getWindowHeight() {
        return (int) this.windowHeight;
    }

    public boolean getIsDarkMode() {
        return this.isDarkMode;
    }

    public boolean getIsMaximized() {
        return this.isMaximized;
    }

    private void readFileContents() {
        try (FileReader reader = new FileReader(TC.SETTINGS_DIRECTORY)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);

            this.isDarkMode = (boolean) json.get("isDarkMode");
            this.isMaximized = (boolean) json.get("isMaximized");
            this.windowHeight = (double) json.get("windowWidth");
            this.windowWidth = (double) json.get("windowHeight");
            this.windowX = (double) json.get("windowX");
            this.windowY = (double) json.get("windowY");
            this.lockTitle = (boolean) json.get("lockTitle");
            this.lockTag = (boolean) json.get("lockTag");
            this.lockBody = (boolean) json.get("lockBody");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
