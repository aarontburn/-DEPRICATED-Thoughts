package com.beanloaf.objects;

import com.beanloaf.tagobjects.IdentifiableObject;

import javax.annotation.Nonnull;
import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ListDisplayView extends JPanel {


    private final List<IdentifiableObject> list = new ArrayList<>();


    public ListDisplayView(final LayoutManager layoutManager) {
        super(layoutManager);
    }


    @Override
    public Component add(final Component comp) {
        if (comp instanceof IdentifiableObject) {
            super.add(comp);

            list.add((IdentifiableObject) comp);
            this.revalidate();
            this.repaint();
        }

        return comp;
    }


    @Override
    public void add(@Nonnull final Component comp, final Object constraints) {
        if (comp instanceof IdentifiableObject) {
            super.add(comp, constraints);

            list.add((IdentifiableObject) comp);
            this.revalidate();
            this.repaint();
        }



    }

    @Override
    public Component add(final Component comp, final int index) {
        if (comp instanceof IdentifiableObject) {
            super.add(comp, index);

            list.add((IdentifiableObject) comp);
            this.revalidate();
            this.repaint();
        }

        return comp;
    }

    @Override
    public void add(final Component comp, final Object constraints, final int index) {
        if (comp instanceof IdentifiableObject) {
            super.add(comp, constraints, index);

            list.add((IdentifiableObject) comp);
            this.revalidate();
            this.repaint();
        }



    }

    @Override
    public void remove(final Component comp) {
        if (comp == null) return;

        if (comp instanceof IdentifiableObject) {
            super.remove(comp);

            list.remove((IdentifiableObject) comp);
            this.revalidate();
            this.repaint();
        }
    }


    public Component findComponentWithTag(final String tag) {
        for (final IdentifiableObject obj : list) {
            if (obj.getIdentifier().equals(tag)) {
                return (Component) obj;
            }
        }

        return null;

    }


}
