package com.beanloaf.view;

import com.beanloaf.input.ListTabPressed;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ListTab;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;

public class LeftPanel extends JPanel implements PropertyChangeListener {

    /**
     * Default size of each tag label.
     */
    private static final Dimension TAG_DIM = new Dimension(150, 25);

    /**
     * Number of tags on the displayed.
     *
     * Default to 2 for sorted/unsorted.
     */
    public int numTags = 2;

    private final Thoughts main;

    public ListTab unsortedListLabel, sortedListLabel;

    public final JTabbedPane leftTabs;

    public LeftPanel(final Thoughts main) {
        super(new GridBagLayout());
        this.main = main;
        main.thoughtsPCS.addPropertyChangeListener(this);

        this.setPreferredSize(new Dimension(450, 0));
        this.setMinimumSize(new Dimension(0, 0));

        this.leftTabs = new JTabbedPane(JTabbedPane.LEFT);
        this.leftTabs.setFont(TC.Fonts.h4);
        this.leftTabs.addMouseListener(new ListTabPressed(main));
        this.leftTabs.setPreferredSize(new Dimension(200, 200));
        this.leftTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.add(this.leftTabs, new GBC(0, 1, 0.2, 1).setFill(GridBagConstraints.BOTH));

        createTabs();


    }

    public void createTabs() {
        createUnsortedTab();
        createSortedTab();
    }

    private void createUnsortedTab() {
        final JPanel unsortedPanel = new JPanel(new GridBagLayout());
        this.leftTabs.add(createScrollView(unsortedPanel),
                "Unsorted");

        final JLabel tabLabel = new JLabel("Unsorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(TAG_DIM);
        leftTabs.setTabComponentAt(0, tabLabel);

        final GBC constraints = new GBC(1, 0.01).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.NORTH);

        final JLabel unsortedLabel = new JLabel("Unsorted");
        unsortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        unsortedLabel.setFont(TC.Fonts.h3);
        unsortedPanel.add(unsortedLabel, constraints.setGridY(0).setWeightY(0.01));

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        unsortedPanel.add(listContainer, constraints.setGridY(1).setWeightY(1));

        unsortedListLabel = new ListTab(main, main.unsortedThoughtList, main.unsortedListModel);
        listContainer.add(unsortedListLabel, BorderLayout.CENTER);
    }

    private void createSortedTab() {

        final JPanel sortedPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(sortedPanel), "Sorted");

        final JLabel tabLabel = new JLabel("Sorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(TAG_DIM);
        leftTabs.setTabComponentAt(1, tabLabel);

        final GBC constraints = new GBC(1, 0.01).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.NORTH);

        final JLabel sortedLabel = new JLabel("Sorted");
        sortedLabel.setHorizontalAlignment(SwingConstants.CENTER);
        sortedLabel.setFont(TC.Fonts.h3);
        sortedPanel.add(sortedLabel, constraints.setGridY(0).setWeightY(0.01));

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        sortedPanel.add(listContainer, constraints.setGridY(1).setWeightY(1));

        sortedListLabel = new ListTab(main, main.sortedThoughtList, main.sortedListModel);
        listContainer.add(sortedListLabel, BorderLayout.CENTER);

    }

    private void createTagTabs(final DefaultListModel<String> model,
                               final ArrayList<ThoughtObject> arrayList, String tagName) {

        final GBC constraints = new GBC(1, 0.01).setFill(GridBagConstraints.HORIZONTAL).setAnchor(GridBagConstraints.NORTH);

        final JPanel tagPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(tagPanel), tagName);

        final JLabel tabLabel = new JLabel(tagName, SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(TAG_DIM);
        leftTabs.setTabComponentAt(numTags, tabLabel);
        numTags++;

        final JLabel tagNameLabel = new JLabel(tagName);
        tagNameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tagNameLabel.setFont(TC.Fonts.h3);
        tagPanel.add(tagNameLabel, constraints.setGridY(0).setWeightY(0.01));

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        tagPanel.add(listContainer, constraints.setGridY(1).setWeightY(1));

        final ListTab tagListLabel = new ListTab(main, arrayList, model);
        listContainer.add(tagListLabel, BorderLayout.CENTER);

    }

    private JScrollPane createScrollView(JPanel panel) {
        final JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new Thoughts.ScrollBar());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    public void setTagModel() {
        ArrayList<String> tagNames = new ArrayList<>();
        for (File sortedFile : main.sortedFiles) {
            ThoughtObject content = main.readFileContents(sortedFile);

            if (content != null) {
                if (!tagNames.contains(content.getTag())) {
                    tagNames.add(content.getTag());
                }
            }

        }
        tagNames.sort(String::compareToIgnoreCase);
        for (String tagName : tagNames) {
            DefaultListModel<String> tempTagModel = new DefaultListModel<>();
            ArrayList<ThoughtObject> thoughtObjectList = new ArrayList<>();
            for (File sortedFile : main.sortedFiles) {
                ThoughtObject content = main.readFileContents(sortedFile);
                if (content != null && content.getTag().equals(tagName)) {
                    tempTagModel.addElement(content.getTitle());
                    thoughtObjectList.add(content);
                }
            }
            createTagTabs(tempTagModel, thoughtObjectList, tagName);
        }
    }


    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TC.Properties.SET_TAB_INDEX -> {
                final int index = (Integer) event.getNewValue();

                try {
                    leftTabs.setSelectedIndex(index);
                } catch (Exception e) {
                    leftTabs.setSelectedIndex(index - 1);
                }
            }

            default -> {
            }
        }
    }
}
