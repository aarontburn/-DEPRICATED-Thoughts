package com.beanloaf.view;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyledDocument;
import java.awt.*;

public class test extends JFrame {


    public static void main(String[] args) throws BadLocationException {
        new test();



    }


    public test() throws BadLocationException {

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setFocusable(true);
        this.setSize(700, 615);
        this.setLocationRelativeTo(null);
        this.setVisible(true);

        final JPanel container = new JPanel();
//        container.setBorder(BorderFactory.createLineBorder(Color.blue, 5));
        container.setLayout(new GridBagLayout());
        this.add(container);


        final JTextPane textPane = new JTextPane();
        textPane.setBorder(BorderFactory.createLineBorder(Color.red));
        textPane.setFont(TC.Fonts.h3);
        textPane.setPreferredSize(new Dimension(200, 200));


        container.add(textPane);


        StyledDocument doc = textPane.getStyledDocument();

        doc.insertString(doc.getLength(), " ", null);





    }



}
