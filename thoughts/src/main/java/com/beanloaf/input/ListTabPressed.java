package com.beanloaf.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.beanloaf.tagobjects.ListItems;
import com.beanloaf.view.LeftPanel;

public class ListTabPressed extends MouseAdapter {

    private final LeftPanel leftPanel;

    public ListTabPressed(final LeftPanel leftPanel) {
        super();
        this.leftPanel = leftPanel;
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        final JTabbedPane tabs = leftPanel.leftTabs;

        final JScrollPane scroll = (JScrollPane) tabs.getSelectedComponent();
        final JPanel panel = (JPanel) scroll.getViewport().getView();
        final JPanel listContainer = (JPanel) panel.getComponent(1);
        final ListItems list = (ListItems) listContainer.getComponent(0);

        list.getMouseEvent().setContentFields(0);
        tabs.setSelectedIndex(tabs.getTabCount() - 1); // This is a workaround for a weird bug.




    }
}
