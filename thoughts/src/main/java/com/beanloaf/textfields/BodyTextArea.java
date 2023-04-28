package com.beanloaf.textfields;

import com.beanloaf.events.TextAreaFocusListener;
import com.beanloaf.events.ThoughtsPCS;
import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;
import com.beanloaf.view.Thoughts;

import javax.swing.JTextPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import javax.swing.undo.UndoManager;
import java.awt.Color;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.*;

public class BodyTextArea extends JTextPane implements DocumentListener {

    public final Thoughts main;
    public final UndoManager undoManager;
    private final GhostText ghostText;
    private boolean isItalic;
    private boolean isBold;
    private boolean isUnderlined;

    public BodyTextArea(final Thoughts main, final UndoManager undoManager) {
        super();


        this.main = main;
        this.undoManager = undoManager;

        this.setFont(TC.Fonts.p);
        this.setName("bodyTextArea");
        this.setEditorKit(new WrapEditorKit());

        defaultDocumentSettings();

        ghostText = new GhostText(TC.DEFAULT_BODY, TC.Fonts.p);
        this.add(ghostText, new GBC().setAnchor(GBC.Anchor.NORTHWEST));

        this.setBackground(main.settings.isLightMode()
                ? Color.LIGHT_GRAY
                : new Color(32, 32, 32));


    }


    private void defaultDocumentSettings() {
        this.getDocument().addDocumentListener(this);
        this.getDocument().putProperty("labelType", this);
        this.getDocument().addUndoableEditListener(undoManager);
        this.addFocusListener(new TextAreaFocusListener(this.main));

        this.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(final KeyEvent event) {
                final String currentText = getText();

                final char eventChar = event.getKeyChar();
                final Character endChar = TC.SURROUND_CHARS.get(eventChar);
                if (endChar != null && getSelectedText() != null) {
                    event.consume();
                    final int selectionStart = getSelectionStart();
                    final int selectionEnd = getSelectionEnd();
                    String selectedText = getSelectedText();

                    if (selectedText.charAt(selectedText.length() - 1) == ' ') {
                        selectedText = eventChar + selectedText.substring(0, selectedText.length() - 1) + endChar + " ";
                    } else {
                        selectedText = eventChar + selectedText + endChar;
                    }


                    setText(currentText.substring(0, selectionStart)
                            + selectedText
                            + currentText.substring(selectionEnd));

                    setCaretPosition(selectionEnd);


                }
            }
        });


        this.addCaretListener(event -> {
            if (getSelectedText() != null) {
                return;
            }

            final AttributeSet set = getStyledDocument().getCharacterElement(getCaretPosition()).getAttributes();

            final Enumeration<?> e = set.getAttributeNames();

            boolean foundBold = false;
            boolean foundUnderline = false;
            boolean foundItalic = false;
            while (e.hasMoreElements()) {
                final Object key = e.nextElement();
                switch (key.toString()) {
                    case "underline" -> foundUnderline = (Boolean) set.getAttribute(key);
                    case "bold" -> foundBold = (Boolean) set.getAttribute(key);
                    case "italic" -> foundItalic = (Boolean) set.getAttribute(key);
                    default -> throw new IllegalArgumentException();
                }
            }
            isBold = foundBold;
            isUnderlined = foundUnderline;
            isItalic = foundItalic;

            setTextDecoration(false);
        });

    }

    public void toggleUnderline() {
        isUnderlined = !isUnderlined;
        setTextDecoration(true);
    }

    public void toggleBold() {
        isBold = !isBold;
        setTextDecoration(true);
    }

    public void toggleItalic() {
        isItalic = !isItalic;
        setTextDecoration(true);
    }

    private void setTextDecoration(final boolean replace) {
        final SimpleAttributeSet set = new SimpleAttributeSet();
        StyleConstants.setBold(set, isBold);
        StyleConstants.setItalic(set, isItalic);
        StyleConstants.setUnderline(set, isUnderlined);

        this.setCharacterAttributes(set, replace);

    }


    private void textChanged() {
        if (this.main.ready) {
            ghostText.setDisplay(this.getText().isBlank());
            if (this.main.selectedFile != null) {
                this.main.selectedFile.editBody(this.getStyledDocument(), this.getText());
            }

        }


    }

    @Override
    public void insertUpdate(final DocumentEvent event) {
        textChanged();
    }

    @Override
    public void removeUpdate(final DocumentEvent event) {
        textChanged();
    }

    @Override
    public void changedUpdate(final DocumentEvent event) {
        textChanged();
    }

    private static class WrapEditorKit extends StyledEditorKit {
        final ViewFactory defaultFactory = new WrapColumnFactory();

        public ViewFactory getViewFactory() {
            return defaultFactory;
        }
    }

    private static class WrapColumnFactory implements ViewFactory {
        public View create(final Element elem) {
            final String kind = elem.getName();
            if (kind != null) {
                switch (kind) {
                    case AbstractDocument.ContentElementName -> {
                        return new WrapLabelView(elem);
                    }
                    case AbstractDocument.ParagraphElementName -> {
                        return new ParagraphView(elem);
                    }
                    case AbstractDocument.SectionElementName -> {
                        return new BoxView(elem, View.Y_AXIS);
                    }
                    case StyleConstants.ComponentElementName -> {
                        return new ComponentView(elem);
                    }
                    case StyleConstants.IconElementName -> {
                        return new IconView(elem);
                    }
                    default -> {
                    }
                }
            }
            return new LabelView(elem);
        }
    }

    private static class WrapLabelView extends LabelView {
        WrapLabelView(final Element elem) {
            super(elem);
        }

        public float getMinimumSpan(final int axis) {
            switch (axis) {
                case View.X_AXIS -> {
                    return 0;
                }
                case View.Y_AXIS -> {
                    return super.getMinimumSpan(axis);
                }
                default -> throw new IllegalArgumentException("Invalid axis: " + axis);
            }
        }
    }


}
