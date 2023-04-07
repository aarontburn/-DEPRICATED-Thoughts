package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.tagobjects.ListItems;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.textfields.SearchBar;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class LeftPanel extends JPanel implements PropertyChangeListener {

    private static final Dimension TAB_DIM = new Dimension(150, 25);

    public int numTags = 2;

    private final Thoughts main;

    public ListItems unsortedListLabel, sortedListLabel;

    public final SearchBar searchBar;

    public final JTabbedPane leftTabs;

    public LeftPanel(final Thoughts main) {
        super(new GridBagLayout());
        this.main = main;
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);

        this.setPreferredSize(new Dimension(450, 0));
        this.setMinimumSize(TC.ZERO_DIM);


        final GBC constraints = new GBC();

        searchBar = new SearchBar();
        this.add(searchBar, constraints.setFill(GBC.Fill.HORIZONTAL).setWeightY(0.001).setAnchor(GBC.Anchor.NORTH));


        this.leftTabs = new JTabbedPane(JTabbedPane.LEFT);
        this.leftTabs.setFont(TC.Fonts.h4);
        this.leftTabs.setPreferredSize(new Dimension(200, 200));
        this.leftTabs.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);

        this.leftTabs.addChangeListener(event -> {
            if (!main.ready) {
                return;
            }
            try {
                final JScrollPane scroll = (JScrollPane) leftTabs.getSelectedComponent();
                if (scroll == null) {
                    return;
                }
                final JPanel panel = (JPanel) scroll.getViewport().getView();
                if (panel.getHeight() == 0 && panel.getWidth() == 0 || panel.getComponents().length < 2) {
                    return;
                }
                final JPanel listContainer = (JPanel) panel.getComponent(1);
                final ListItems list = (ListItems) listContainer.getComponent(0);
                if (!list.getValueIsAdjusting()) {
                    list.getMouseEvent().setContentFields(0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        });


        this.add(this.leftTabs, constraints.increaseGridY().setFill(GBC.Fill.BOTH).setWeightY(1));

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
        tabLabel.setPreferredSize(TAB_DIM);

        leftTabs.setTabComponentAt(0, tabLabel);

        final GBC constraints = new GBC().setWeightY(0.01).setFill(GBC.Fill.HORIZONTAL).setAnchor(GBC.Anchor.NORTH);

        final JLabel unsortedLabel = new JLabel("Unsorted", SwingConstants.CENTER);
        unsortedLabel.setFont(TC.Fonts.h3);
        unsortedPanel.add(unsortedLabel, constraints);

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        unsortedPanel.add(listContainer, constraints.increaseGridY().setWeightY(1));

        unsortedListLabel = new ListItems(main, main.unsortedThoughtList, main.unsortedListModel);
        listContainer.add(unsortedListLabel, BorderLayout.CENTER);
    }

    private void createSortedTab() {

        final JPanel sortedPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(sortedPanel), "Sorted");

        final JLabel tabLabel = new JLabel("Sorted", SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(TAB_DIM);

        final GBC constraints = new GBC().setWeightY(0.01).setFill(GBC.Fill.HORIZONTAL).setAnchor(GBC.Anchor.NORTH);

        final JLabel sortedLabel = new JLabel("Sorted", SwingConstants.CENTER);
        sortedLabel.setFont(TC.Fonts.h3);
        sortedPanel.add(sortedLabel, constraints);

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        sortedPanel.add(listContainer, constraints.increaseGridY().setWeightY(1));

        sortedListLabel = new ListItems(main, main.sortedThoughtList, main.sortedListModel);
        listContainer.add(sortedListLabel, BorderLayout.CENTER);

    }

    private void createTagTabs(final DefaultListModel<String> model,
                               final List<ThoughtObject> arrayList,
                               final String tagName) {

        final GBC constraints = new GBC().setWeightY(0.01).setFill(GBC.Fill.HORIZONTAL).setAnchor(GBC.Anchor.NORTH);

        final JPanel tagPanel = new JPanel(new GridBagLayout());
        leftTabs.add(createScrollView(tagPanel), tagName);

        final JLabel tabLabel = new JLabel(tagName, SwingConstants.CENTER);
        tabLabel.setFont(TC.Fonts.h4);
        tabLabel.setPreferredSize(TAB_DIM);
        numTags++;

        final JLabel tagNameLabel = new JLabel(tagName, SwingConstants.CENTER);
        tagNameLabel.setFont(TC.Fonts.h3);
        tagPanel.add(tagNameLabel, constraints);

        final JPanel listContainer = new JPanel(new BorderLayout());
        listContainer.setOpaque(false);
        tagPanel.add(listContainer, constraints.increaseGridY().setWeightY(1));

        final ListItems tagListLabel = new ListItems(main, arrayList, model);
        listContainer.add(tagListLabel, BorderLayout.CENTER);

    }

    private JScrollPane createScrollView(final JPanel panel) {
        final JScrollPane scroll = new JScrollPane(panel,
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scroll.setBorder(null);
        scroll.getVerticalScrollBar().setUI(new TC.ScrollBar());
        scroll.getVerticalScrollBar().setUnitIncrement(12);
        return scroll;
    }

    public void setTagModel() {
        final ArrayList<String> tagNames = new ArrayList<>();
        for (final File sortedFile : main.sortedFiles) {
            final ThoughtObject content = main.readFileContents(sortedFile);

            if (content != null && !tagNames.contains(content.getTag())) {
                tagNames.add(content.getTag());
            }

        }
        tagNames.sort(String::compareToIgnoreCase);
        for (final String tagName : tagNames) {
            final DefaultListModel<String> tempTagModel = new DefaultListModel<>();
            final ArrayList<ThoughtObject> thoughtObjectList = new ArrayList<>();
            for (final File sortedFile : main.sortedFiles) {
                final ThoughtObject content = main.readFileContents(sortedFile);
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

                boolean validTab = false;
                int indexMod = 0;

                do {
                    try {
                        leftTabs.setSelectedIndex(index - indexMod);
                        validTab = true;
                    } catch (final Exception e) {
                        indexMod++;
                    }
                } while (!validTab);

            }

            default -> {
            }
        }
    }
}
