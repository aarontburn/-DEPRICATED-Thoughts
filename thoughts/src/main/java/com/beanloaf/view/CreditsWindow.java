package com.beanloaf.view;

import com.beanloaf.objects.GBC;
import com.beanloaf.res.TC;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CreditsWindow extends JFrame {


    public static CreditsWindow instance;

    private final JPanel container;

    public static CreditsWindow getInstance() {
        if (instance == null) {
            instance = new CreditsWindow();
        } else {
            instance.toFront();
            instance.setExtendedState(JFrame.NORMAL);
        }
        return instance;
    }


    private CreditsWindow() {
        super("Credits");
        this.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.setFocusable(true);
        this.setSize(700, 615);
        this.setLocationRelativeTo(null);

        this.setVisible(true);


        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        this.add(container);

        createGUI();
    }

    private void createGUI() {
        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(final WindowEvent event) {
                super.windowClosing(event);
                instance = null;
            }
        });

        final JLabel textContent = new JLabel();
        textContent.setHorizontalAlignment(SwingConstants.CENTER);
        textContent.setBorder(BorderFactory.createLineBorder(Color.red));
        container.add(textContent);
        textContent.setFont(TC.Fonts.h3);
        textContent.setText(
                """
                <html>
                <head>
                
                <style>
                    p {
                        font-size:15px;
                    }
                
                
                </style>              
                
                
                </head>
                <body>
                
                    <h2>Thoughts</h2>
                    <h4>by @beanloaf</h4>
                    <br>
                    
                    
                    <h4>What is Thoughts?</h4>
                    <p>Thoughts is an application where you can record quick, on-the-fly notes so you wont forget them later.</p>
                    
                
                
                
                
                
                </body>            
                </html>
                """
        );






    }
}
