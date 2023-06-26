package com.beanloaf.view;

import com.beanloaf.res.TC;

import javax.swing.*;
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



        container = new JPanel();
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        this.add(container);

        createGUI();
        this.setVisible(true);

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
        container.add(textContent);
        textContent.setFont(TC.Fonts.h3);
        textContent.setText(
                """
                <html>
                <head>
                
                <style>
                    p, li {
                        font-size:12px;
                    }
                    
                    li {
                        padding-bottom: 5px;
                    }
                    
                    .container {
                        padding-left: 10px;
                        padding-right: 10px;
                    
                    }
                
                
                </style>              
                
                
                </head>
                <body>
                
                    <div class="container">
                        <center>
                        <h2 style="padding: 0px; margin: 0px;">Thoughts</h2>
                        <h4>by @beanloaf</h4>
                        </center>
                        
                        
                        <h4>What is Thoughts?</h4>
                        <p>
                        Thoughts is an application where you can record quick, 
                        on-the-fly notes so you wont forget them later.
                        </p>
                        <br>
                        <h4>Features</h4>
                        
                        <ul>
                            <li>Group notes by using tags.</li>
                            <li>Search all files for text, or search for things in specific fields with
                                    !title, !tag, !date, !body</li>
                            <li>Cloud Storage: Make an account to easily store and access notes
                                from the cloud.</li>
                            <li>Export notes into plain-text.</li>
                            <li>Rich-text support.</li>
                        </ul>
                        
                        <br>
                        <p style="font-size:15px;">
                        Questions? Comments? Suggestions? Email me at beanloafmusic@gmail.com</p>
                
                    </div>
                
                
                
                </body>            
                </html>
                """
        );






    }
}
