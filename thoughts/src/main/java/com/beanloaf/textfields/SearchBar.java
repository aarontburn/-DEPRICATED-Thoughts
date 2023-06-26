package com.beanloaf.textfields;

import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.objects.ThoughtObject;
import com.beanloaf.res.TC;

import javax.swing.JTextPane;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;
import java.awt.GridBagLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.Map;

public class SearchBar extends JTextPane {

    private final GhostText ghostText;

    private final Map<String, Integer> keywordLengths;

    private final AttributeSet defaultColor;

    public SearchBar() {
        super();
        this.setFont(TC.Fonts.h5);
        this.setLayout(new GridBagLayout());


        this.keywordLengths = new HashMap<>();
        this.keywordLengths.put("!title", "!title".length());
        this.keywordLengths.put("!tag", "!tag".length());
        this.keywordLengths.put("!date", "!date".length());
        this.keywordLengths.put("!body", "!body".length());

        this.defaultColor = StyleContext.getDefaultStyleContext().addAttribute(StyleContext.getDefaultStyleContext().getEmptySet(), StyleConstants.Foreground, this.getForeground());

        addEventListeners();

        ghostText = new GhostText("Search for...", this.getFont());
        ghostText.setDisplay(true);
        this.add(ghostText, new GBC().setAnchor(GBC.Anchor.WEST));

    }

    public static boolean searchFor(final ThoughtObject object, final String text) {
        if (text.isBlank()) {
            return true;
        }

        final String[] splitText = text.split(" ");

        switch (splitText[0]) {
            case "!title" -> {
                return splitText.length == 1
                        || object.getTitle().toLowerCase().contains(text.substring("!title".length()).toLowerCase().trim());
            }
            case "!tag" -> {
                return splitText.length == 1
                        || object.getTag().toLowerCase().contains(text.substring("!tag".length()).toLowerCase().trim());
            }

            case "!date" -> {
                return splitText.length == 1
                        || object.getDate().toLowerCase().contains(text.substring("!date".length()).toLowerCase().trim());

            }
            case "!body" -> {
                return splitText.length == 1
                        || object.getBody().toLowerCase().contains(text.substring("!body".length()).toLowerCase().trim());
            }

            default -> {
                return object.getTitle().toLowerCase().contains(text.toLowerCase().trim())
                        || object.getTag().toLowerCase().contains(text.toLowerCase().trim())
                        || object.getDate().toLowerCase().contains(text.toLowerCase().trim())
                        || object.getBody().toLowerCase().contains(text.toLowerCase().trim());
            }


        }


    }

    private void addEventListeners() {
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(final MouseEvent event) {
                if (!getText().isEmpty()) {
                    final String keyword = setHighlight();

                    setText(getText().trim().equals(keyword) || keyword.isEmpty()
                            ? ""
                            : keyword + " ");

                    ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.REFRESH);
                }
            }
        });
        this.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(final DocumentEvent event) {
            }

            public void removeUpdate(final DocumentEvent event) {
                keyTyped();
            }

            public void insertUpdate(final DocumentEvent event) {
                keyTyped();
            }

            public void keyTyped() {
                setHighlight();
                ghostText.setDisplay(getText().isEmpty());

            }
        });

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(final KeyEvent event) {
                if (event.getKeyCode() == KeyEvent.VK_ENTER) {
                    event.consume();
                    ThoughtsPCS.getInstance().firePropertyChange(TC.Properties.REFRESH);
                }
            }
        });

    }

    public String setHighlight() {
        final String[] splitText = this.getText().split(" ");

        final StyleContext cont = StyleContext.getDefaultStyleContext();
        final AttributeSet keyWordColor = cont.addAttribute(cont.getEmptySet(), StyleConstants.Foreground, Color.blue);

        final Integer keywordLength = this.keywordLengths.get(splitText[0]);
        if (keywordLength != null) {
            SwingUtilities.invokeLater(() -> {
                this.getStyledDocument().setCharacterAttributes(0, keywordLength, keyWordColor, true);
                this.getStyledDocument().setCharacterAttributes(keywordLength, getStyledDocument().getLength(), defaultColor, true);
            });
            return splitText[0];
        }
        SwingUtilities.invokeLater(() -> this.getStyledDocument().setCharacterAttributes(0, getText().length(), defaultColor, true));
        return "";
    }


}
