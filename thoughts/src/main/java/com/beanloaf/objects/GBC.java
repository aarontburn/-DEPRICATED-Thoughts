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
        this.gridy = gridY;
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

    public GBC increaseGridX() {
        this.gridx++;
        return this;
    }

    public GBC increaseGridY() {
        this.gridy++;
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

    public GBC setInsets(final int inset) {
        return setInsets(inset, inset, inset, inset);
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


    public static class Fill {
        public static final int NONE = GridBagConstraints.NONE;
        public static final int HORIZONTAL = GridBagConstraints.HORIZONTAL;
        public static final int BOTH = GridBagConstraints.BOTH;
        public static final int VERTICAL = GridBagConstraints.VERTICAL;

    }

    public static class Anchor {
        public static final int ABOVE_BASE_LINE = GridBagConstraints.ABOVE_BASELINE;
        public static final int ABOVE_BASELINE_LEADING = GridBagConstraints.ABOVE_BASELINE_LEADING;
        public static final int ABOVE_BASELINE_TRAILING = GridBagConstraints.ABOVE_BASELINE_TRAILING;
        public static final int BELOW_BASELINE = GridBagConstraints.BELOW_BASELINE;
        public static final int BELOW_BASELINE_LEADING = GridBagConstraints.BELOW_BASELINE_LEADING;
        public static final int BELOW_BASELINE_TRAILING = GridBagConstraints.BELOW_BASELINE_TRAILING;
        public static final int PAGE_START = GridBagConstraints.PAGE_START;
        public static final int PAGE_END = GridBagConstraints.PAGE_END;
        public static final int LINE_START = GridBagConstraints.LINE_START;
        public static final int LINE_END = GridBagConstraints.LINE_END;
        public static final int FIRST_LINE_START = GridBagConstraints.FIRST_LINE_START;
        public static final int FIRST_LINE_END = GridBagConstraints.FIRST_LINE_END;
        public static final int LAST_LINE_END = GridBagConstraints.LAST_LINE_END;
        public static final int LAST_LINE_START = GridBagConstraints.LAST_LINE_START;
        public static final int NORTH = GridBagConstraints.NORTH;
        public static final int NORTHEAST = GridBagConstraints.NORTHEAST;
        public static final int EAST = GridBagConstraints.EAST;
        public static final int SOUTHEAST = GridBagConstraints.SOUTHEAST;
        public static final int SOUTHWEST = GridBagConstraints.SOUTHWEST;
        public static final int WEST = GridBagConstraints.WEST;
        public static final int NORTHWEST = GridBagConstraints.NORTHWEST;
        public static final int CENTER = GridBagConstraints.CENTER;

    }

    public static class Grid {
        public static final int REMAINDER = GridBagConstraints.REMAINDER;
        public static final int RELATIVE = GridBagConstraints.RELATIVE;
    }





}
