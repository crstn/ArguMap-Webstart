package de.argumap.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JProgressBar;

public class SplashScreen extends Frame {

    public SplashScreen(){
        FlowLayout layout = new FlowLayout();
        layout.setHgap(0);
        layout.setVgap(0);
        setLayout(layout);
        
        JLabel spacer = new JLabel("");
        spacer.setPreferredSize(new Dimension(400,1));
        spacer.setBackground(Color.darkGray);
        add(spacer);
        
        JLabel splashLogo =new JLabel(createImageIcon("img/splashlogo.jpg", "Reset"));
        add(splashLogo);
        
        JProgressBar progressBar = new JProgressBar();
        //when the task of unknown length begins:
        progressBar.setPreferredSize(new Dimension(400, 30));
        progressBar.setIndeterminate(true);
        progressBar.setBorder(BorderFactory.createLineBorder(Color.white));
        progressBar.setBackground(Color.white);
        progressBar.setForeground(new Color(228, 228, 228));
        add(progressBar);
        
        setPreferredSize(new Dimension(402,255));
        Dimension screenSize =
            Toolkit.getDefaultToolkit().getScreenSize();
        int xPos = (screenSize.width / 2) - (getPreferredSize().width / 2);
        int yPos = (screenSize.height / 2) - (getPreferredSize().height / 2);
        setLocation(xPos,yPos);
        setEnabled(false);
        setResizable(false);
        setUndecorated(true);
        setBackground(Color.darkGray);
        pack();
        setVisible(true);
        
    }
    
    /** Returns an ImageIcon, or null if the path was invalid. */
    protected static ImageIcon createImageIcon(String path,
                                               String description) {
        java.net.URL imgURL = SplashScreen.class.getResource(path);
        if (imgURL != null) {
            return new ImageIcon(imgURL, description);
        } else {
            System.err.println("Couldn't find file: " + path);
            return null;
        }
    }
}
