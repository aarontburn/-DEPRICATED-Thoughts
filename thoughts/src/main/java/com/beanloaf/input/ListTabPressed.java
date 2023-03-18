package com.beanloaf.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.beanloaf.objects.ListTab;
import com.beanloaf.view.Thoughts;

public class ListTabPressed extends MouseAdapter {

    private final Thoughts main;

    public ListTabPressed(final Thoughts main) {
        super();
        this.main = main;
    }

    @Override
    public void mousePressed(final MouseEvent event) {
        JTabbedPane tabs = main.leftPanel.leftTabs;

        JScrollPane scroll = (JScrollPane) tabs.getSelectedComponent();
        JPanel panel = (JPanel) scroll.getViewport().getView();
        JPanel listContainer = (JPanel) panel.getComponent(1);
        ListTab list = (ListTab) listContainer.getComponent(0);
        list.getMouseEvent().setContentFields(0);
        tabs.setSelectedIndex(tabs.getTabCount() - 1); // This is a workaround for a weird bug.
    }
}
