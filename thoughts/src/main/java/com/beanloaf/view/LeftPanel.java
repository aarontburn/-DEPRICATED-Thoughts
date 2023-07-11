package com.beanloaf.view;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.tagobjects.TagListItem;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;
import com.beanloaf.textfields.SearchBar;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LeftPanel extends JPanel implements PropertyChangeListener {


    public int numTags = 2;

    private final Thoughts main;

    public TagListItem unsortedListLabel, sortedListLabel;

    public final SearchBar searchBar;

//    public final JTabbedPane leftTabs;

    public final Map<String, TagListItem> thoughtListByTag = new HashMap<>();

    public final JPanel tagList, itemList;
    public TagListItem selectedTag;



    public LeftPanel(final Thoughts main) {
        super(new GridBagLayout());
        this.main = main;
        ThoughtsPCS.getInstance().addPropertyChangeListener(this);

        this.setPreferredSize(new Dimension(450, 0));
        this.setMinimumSize(TC.ZERO_DIM);


        final GBC constraints = new GBC();

        searchBar = new SearchBar();
        this.add(searchBar, constraints.setFill(GBC.Fill.HORIZONTAL).setWeightY(0.001).setAnchor(GBC.Anchor.NORTH));


        final JPanel tagItemList = new JPanel(new GridBagLayout());
        tagItemList.setBorder(BorderFactory.createLineBorder(Color.white));
        this.add(tagItemList, constraints.increaseGridY().setFill(GBC.Fill.BOTH).setWeightY(1));


        tagList = new JPanel(new GridBagLayout());
        tagList.setBorder(BorderFactory.createLineBorder(Color.red));
        tagItemList.add(createScrollView(tagList), new GBC().setWeightX(.4).setFill(GBC.Fill.BOTH));


        itemList = new JPanel(new GridBagLayout());
        itemList.setBorder(BorderFactory.createLineBorder(Color.blue));
        tagItemList.add(createScrollView(itemList), new GBC().setGridX(1).setWeightX(.6).setFill(GBC.Fill.BOTH));


    }

    public void populateTagList() {
        final GBC constraints = new GBC().setAnchor(GBC.Anchor.NORTH).setFill(GBC.Fill.HORIZONTAL).setWeightXY(Double.MIN_VALUE, Double.MIN_VALUE);

        tagList.removeAll();


        unsortedListLabel = new TagListItem(this, "Unsorted");
        sortedListLabel = new TagListItem(this, "Sorted");

        tagList.add(unsortedListLabel, constraints);
        tagList.add(sortedListLabel, constraints.increaseGridY());

        unsortedListLabel.setList(main.unsortedThoughtList);
        sortedListLabel.setList(main.sortedThoughtList);


        for (final ThoughtObject obj : main.sortedThoughtList) {
            final String tag = obj.getTag();
            TagListItem list = thoughtListByTag.get(tag);

            if (list == null) {
                list = new TagListItem(this, tag);
                thoughtListByTag.put(tag, list);
            }
            list.add(obj);
            obj.setParent(list);
            tagList.add(list, constraints.increaseGridY());


        }

        tagList.add(new JPanel(), constraints.increaseGridX().setWeightXY(1, 1));

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

    @Override
    public void propertyChange(final PropertyChangeEvent event) {
        switch (event.getPropertyName()) {
            case TC.Properties.SET_TAB_INDEX -> {
                final int index = (Integer) event.getNewValue();

                boolean validTab = false;
                int indexMod = 0;

                do {
                    try {
//                        leftTabs.setSelectedIndex(index - indexMod);
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
