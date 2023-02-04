package com.beanloaf.shared;

import java.awt.Dimension;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.beanloaf.common.TC;

public class SettingsHandler {
    // Default values
    private final boolean defaultIsDarkMode = true;
    private final boolean defaultIsMaximized = true;
    private final double defaultWindowWidth = 1650;
    private final double defaultWindowHeight = 1080;

    // Setting fields
    private boolean isDarkMode;
    private boolean isMaximized;
    private double windowWidth;
    private double windowHeight;

    public SettingsHandler() {
        readFileContents();
        check();
    }

    public void check() {
        if (TC.SETTINGS_DIRECTORY.isFile()) {
            return;
        } else {
            this.isDarkMode = this.defaultIsDarkMode;
            this.isMaximized = this.defaultIsMaximized;
            this.windowWidth = this.defaultWindowWidth;
            this.windowHeight = this.defaultWindowHeight;
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
            JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());
            fWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
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

    public void changeWindowHeight(int newHeight) {
        this.windowHeight = newHeight;
        createSettingsFile();
    }

    public void changeWindowWidth(int newWidth) {
        this.windowWidth = newWidth;
        createSettingsFile();
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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
