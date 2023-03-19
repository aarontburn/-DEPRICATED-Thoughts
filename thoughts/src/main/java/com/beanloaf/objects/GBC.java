package com.beanloaf.objects;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Class that makes implementing GridBagConstraints a bit easier.
 *
 * @author beanloaf
 */
public class GBC extends GridBagConstraints {

    public GBC() {
        this(0, 0);
    }
    public GBC(final int gridX, final int gridY) {
        this(gridX, gridY, 0.1, 0.1);
    }

    public GBC(final double weightX, final double weightY) {
        this(0, 0, weightX, weightY);
    }

    public GBC(final int gridX, final int gridY, final double weightX, final double weightY) {
        super();
        this.gridx = gridX;
        this.gridy = gridY;
        this.weightx = weightX;
        this.weighty = weightY;
    }

    public GBC setGridXY(final int gridX, final int gridY) {
        this.gridx = gridX;
        this.gridy = gridy;
        return this;
    }

    public GBC setGridX(final int gridX) {
        this.gridx = gridX;
        return this;
    }

    public GBC setGridY(final int gridY) {
        this.gridy = gridY;
        return this;
    }

    public GBC setWeightXY(final double weightX, final double weightY) {
        this.weightx = weightX;
        this.weighty = weightY;
        return this;
    }

    public GBC setWeightX(final double weightX) {
        this.weightx = weightX;
        return this;
    }

    public GBC setWeightY(final double weightY) {
        this.weighty = weightY;
        return this;
    }


    public GBC setAnchor(final int anchor) {
        this.anchor = anchor;
        return this;
    }

    public GBC setInsets(final int top, final int left, final int bottom, final int right) {
        this.insets = new Insets(top, left, bottom, right);
        return this;
    }

    public GBC setFill(final int fill) {
        this.fill = fill;
        return this;
    }

    public GBC setGridWidth(final int width) {
        this.gridwidth = width;
        return this;
    }

    public GBC setGridHeight(final int height) {
        this.gridheight = height;
        return this;
    }

    public GBC setGridSize(final int width, final int height) {
        this.gridwidth = width;
        this.gridheight = height;
        return this;
    }

    public GBC setIPadX(final int padX) {
        this.ipadx = padX;
        return this;
    }

    public GBC setIPadY(final int padY) {
        this.ipady = padY;
        return this;
    }

    public GBC setIPadXY(final int padX, final int padY) {
        this.ipadx = padX;
        this.ipady = padY;
        return this;
    }


}
