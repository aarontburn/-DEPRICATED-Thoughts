package com.beanloaf.tMainEventHandlers;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

import com.beanloaf.main.ThoughtsMain;
import com.beanloaf.objects.ListTab;

public class ListTabPressed extends MouseAdapter {

    private ThoughtsMain main;

    public ListTabPressed(ThoughtsMain main) {
        this.main = main;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        JTabbedPane tabs = main.leftTabs;
        JScrollPane scroll = (JScrollPane) tabs.getSelectedComponent();
        System.out.println(scroll);

        JPanel panel = (JPanel) scroll.getViewport().getView();
        JPanel listContainer = (JPanel) panel.getComponent(1);
        ListTab list = (ListTab) listContainer.getComponent(0);
        list.getMouseEvent().setContentFields(0);
        main.refreshThoughtList();
        new KeyChange(main).checkEmpty();
    }
}
