package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.res.TC;

import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.SwingConstants;
import java.awt.Desktop;
import java.net.URI;
import java.util.LinkedHashMap;
import java.util.Map;

public class MenuBar extends JMenuBar {


    public MenuBar(final ThoughtsPCS pcs) {
        super();
        setMenuOperations(pcs);

    }

    private void setMenuOperations(final ThoughtsPCS pcs) {
        final Map<String, Map<String, Runnable>> menuMap = new LinkedHashMap<>();

        /* File */
        final Map<String, Runnable> fileOptions = new LinkedHashMap<>();
        fileOptions.put("New File", () -> pcs.firePropertyChange(TC.Properties.NEW_FILE));
        fileOptions.put("Sort File", () -> pcs.firePropertyChange(TC.Properties.SORT));
        fileOptions.put("Delete File", () -> pcs.firePropertyChange(TC.Properties.DELETE));
        fileOptions.put("Refresh", () -> pcs.firePropertyChange(TC.Properties.REFRESH));
        fileOptions.put(null, null);
        fileOptions.put("Exit", () -> pcs.firePropertyChange(TC.Properties.EXIT));
        menuMap.put("File", fileOptions);


        /* Edit */
        final Map<String, Runnable> editOptions = new LinkedHashMap<>();
        editOptions.put("Undo", () -> pcs.firePropertyChange(TC.Properties.UNDO));
        editOptions.put("Redo", () -> pcs.firePropertyChange(TC.Properties.REDO));
        menuMap.put("Edit", editOptions);


        /* Tools */
        final Map<String, Runnable> toolOptions = new LinkedHashMap<>();
        toolOptions.put("Export", () -> System.out.println("placeholder"));
        toolOptions.put("Settings", () -> pcs.firePropertyChange(TC.Properties.OPEN_SETTINGS_WINDOW));
        menuMap.put("Tools", toolOptions);


        /* Cloud*/
        final Map<String, Runnable> cloudOptions = new LinkedHashMap<>();
        cloudOptions.put("Push Files", () -> pcs.firePropertyChange(TC.Properties.PUSH));
        cloudOptions.put("Pull Files", () -> pcs.firePropertyChange(TC.Properties.PULL));
        cloudOptions.put(null, null);
        cloudOptions.put("Cloud Settings", () -> pcs.firePropertyChange(TC.Properties.CLOUD_SETTINGS));
        menuMap.put("Cloud", cloudOptions);


        /* Help */
        final Map<String, Runnable> helpOptions = new LinkedHashMap<>();
        helpOptions.put("Credits", () -> System.out.println("placeholder"));
        helpOptions.put("GitHub", () -> {
            if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/beanloaf/Thoughts"));
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });
        menuMap.put("Help", helpOptions);

        createMenuUI(menuMap);

    }

    private void createMenuUI(final Map<String, Map<String, Runnable>> menuMap) {
        for (final String menuName : menuMap.keySet()) {
            final JMenu menu = new JMenu(menuName);
            for (final String menuOption : menuMap.get(menuName).keySet()) {
                if (menuOption == null) {
                    menu.addSeparator();
                    continue;
                }
                final JMenuItem menuItem = new JMenuItem(menuOption);
                menuItem.setHorizontalTextPosition(SwingConstants.LEFT);

                menuItem.addActionListener(event -> menuMap.get(menuName).get(menuOption).run());
                menu.add(menuItem);
            }
            this.add(menu);
        }
    }


}
