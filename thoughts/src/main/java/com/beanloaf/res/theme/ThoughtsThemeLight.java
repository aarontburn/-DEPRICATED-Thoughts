package com.beanloaf.res.theme;

import com.formdev.flatlaf.FlatIntelliJLaf;

public class ThoughtsThemeLight
        extends FlatIntelliJLaf {
    public static final String NAME = "ThoughtsThemeLight";

    public static boolean setup() {
        return setup(new ThoughtsThemeLight());
    }

    public static void installLafInfo() {
        installLafInfo(NAME, ThoughtsThemeLight.class);
    }

    @Override
    public String getName() {
        return NAME;
    }
}
