package com.beanloaf.shared;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.json.simple.JSONObject;

public class CheckForSettings {

    private final String SETTINGS_FILE_NAME = "settings.json";
    final File settingsFile = new File(SETTINGS_FILE_NAME);

    /* Setting Fields */
    private boolean isDarkMode;
    private boolean isMaximized;
    private boolean saveOnEdit;
    private int windowWidth;
    private int windowHeight;

    public void check() {
        try {
            if (!settingsFile.isFile()) {
                settingsFile.createNewFile();
            }
            FileWriter fWriter = new FileWriter(settingsFile);
            HashMap<String, Object> textContent = new HashMap<String, Object>();

            textContent.put("isDarkMode", true);
            textContent.put("isMaximized", true);
            textContent.put("saveOnEdit", true);
            textContent.put("windowWidth", 100);
            textContent.put("windowHeight", 100);

            JSONObject objJson = new JSONObject(textContent);
            fWriter.write(objJson.toJSONString());
            fWriter.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
