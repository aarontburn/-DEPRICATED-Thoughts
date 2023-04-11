package com.beanloaf.events;

import java.awt.Dimension;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.res.TC;

public class SettingsHandler {
    // Setting fields
    private boolean isLightMode;
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
        readFileContents();
        check();
    }

    public void check() {
        if (TC.Paths.SETTINGS_DIRECTORY.isFile()) {
            return;
        } else {
            System.err.println("Creating new settings.json...");
            // Default values
            this.isLightMode = false;
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
        try (BufferedWriter fWriter = Files.newBufferedWriter(Paths.get(TC.Paths.SETTINGS_DIRECTORY.toURI()))) {
            TC.Paths.SETTINGS_DIRECTORY.createNewFile();

            final HashMap<String, Object> textContent = new HashMap<>();

            textContent.put("isLightMode", this.isLightMode);
            textContent.put("isMaximized", this.isMaximized);
            textContent.put("windowWidth", this.windowWidth);
            textContent.put("windowHeight", this.windowHeight);
            textContent.put("windowX", this.windowX);
            textContent.put("windowY", this.windowY);
            textContent.put("lockTitle", this.lockTitle);
            textContent.put("lockTag", this.lockTag);
            textContent.put("lockBody", this.lockBody);
            textContent.put("pushOnClose", this.pushOnClose);
            textContent.put("pullOnStartup", this.pullOnStartup);

            final JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void readFileContents() {
        try (BufferedReader reader = Files.newBufferedReader(Paths.get(TC.Paths.SETTINGS_DIRECTORY.toURI()))) {
            final JSONObject json = (JSONObject) new JSONParser().parse(reader);

            this.isLightMode = (boolean) json.get("isLightMode");
            this.isMaximized = (boolean) json.get("isMaximized");
            this.windowHeight = (double) json.get("windowHeight");
            this.windowWidth = (double) json.get("windowWidth");
            this.windowX = (double) json.get("windowX");
            this.windowY = (double) json.get("windowY");
            this.lockTitle = (boolean) json.get("lockTitle");
            this.lockTag = (boolean) json.get("lockTag");
            this.lockBody = (boolean) json.get("lockBody");
            this.pushOnClose = (boolean) json.get("pushOnClose");
            this.pullOnStartup = (boolean) json.get("pullOnStartup");

        } catch (Exception e) {
            System.err.println("Error in settings.json. Regenerating file...");
            if (TC.Paths.SETTINGS_DIRECTORY.delete()) {
                System.err.println("Successfully deleted settings.json.");
            }
        }
    }

    public void changePushOnClose(final boolean b) {
        this.pushOnClose = b;
        createSettingsFile();
    }

    public void changePullOnStartup(final boolean b) {
        this.pullOnStartup = b;
        createSettingsFile();
    }

    public void changeWindowPosition(final Point point) {
        this.windowX = point.getX();
        this.windowY = point.getY();
        createSettingsFile();
    }

    public void changeIsLightMode(final boolean b) {
        this.isLightMode = b;
        createSettingsFile();
    }

    public void changeIsMaximized(final boolean b) {
        this.isMaximized = b;
        createSettingsFile();
    }

    public void changeWindowDimension(final Dimension newDimension) {
        this.windowWidth = newDimension.getWidth();
        this.windowHeight = newDimension.getHeight();
        createSettingsFile();
    }

    public void changeLockTitle(final boolean b) {
        this.lockTitle = b;
        createSettingsFile();
    }

    public void changeLockTag(final boolean b) {
        this.lockTag = b;
        createSettingsFile();
    }

    public void changeLockBody(final boolean b) {
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

    public boolean isLightMode() {
        return this.isLightMode;
    }

    public boolean isMaximized() {
        return this.isMaximized;
    }

}
