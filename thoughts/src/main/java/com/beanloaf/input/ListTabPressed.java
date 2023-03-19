package com.beanloaf.input;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.beanloaf.objects.ListTab;
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
        tabs.setSelectedIndex(tabs.getTabCount() - 1); // This is a workaround for a weird bug.

        final JScrollPane scroll = (JScrollPane) tabs.getSelectedComponent();
        final JPanel panel = (JPanel) scroll.getViewport().getView();
        final JPanel listContainer = (JPanel) panel.getComponent(1);
        final ListTab list = (ListTab) listContainer.getComponent(0);

        list.getMouseEvent().setContentFields(0);



    }
}
