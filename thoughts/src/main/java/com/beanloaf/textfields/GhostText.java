package com.beanloaf.textfields;

import javax.swing.JLabel;
import java.awt.Font;

public class GhostText extends JLabel {

    private final String displayText;


    public GhostText(final String displayText, final Font font) {
        super("");
        this.displayText = displayText;
        this.setFont(font);
        this.setOpaque(false);
        this.setEnabled(false);
    }

    public void setDisplay(final boolean isDisplaying) {
        this.setText(isDisplaying ? displayText : "");

    }

}
