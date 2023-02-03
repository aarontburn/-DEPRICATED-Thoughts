package com.beanloaf.shared;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class SettingsHandler {

    private final String SETTINGS_FILE_NAME = "settings.json";
    final File settingsFile = new File(SETTINGS_FILE_NAME);

    // Default values
    final boolean defaultIsDarkMode = true;
    final boolean defaultIsMaximized = true;
    final int defaultWindowWidth = 1650;
    final int defaultWindowHeight = 1080;

    public SettingsHandler() {
        createNewSettingsFile();
    }

    public void createNewSettingsFile() {
        if (settingsFile.isFile()) {
            return;
        }
        try {
            settingsFile.createNewFile();
            FileWriter fWriter = new FileWriter(settingsFile);
            HashMap<String, Object> textContent = new HashMap<String, Object>();
            textContent.put("isDarkMode", defaultIsDarkMode);
            textContent.put("isMaximized", defaultIsMaximized);
            textContent.put("windowWidth", defaultWindowWidth);
            textContent.put("windowHeight", defaultWindowHeight);
            JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());
            fWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int getWindowWidth() {
        return (int) readFileContents(Options.windowWidth);
    }

    public int getWindowHeight() {
        return (int) readFileContents(Options.windowHeight);
    }

    public boolean getIsDarkMode() {
        return (boolean) readFileContents(Options.isDarkMode);
    }

    public boolean getIsMaximized() {
        return (boolean) readFileContents(Options.isMaximized);
    }

    private Object readFileContents(Options option) {
        try (FileReader reader = new FileReader(settingsFile)) {
            JSONObject json = (JSONObject) new JSONParser().parse(reader);
            switch (option) {
                case isDarkMode:
                    return json.get("isDarkMode");

                case isMaximized:
                    return json.get("isMaximized");

                case windowHeight:
                    return json.get("windowWidth");

                case windowWidth:
                    return json.get("windowHeight");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    enum Options {
        isDarkMode,
        isMaximized,
        windowHeight,
        windowWidth
    }

}
