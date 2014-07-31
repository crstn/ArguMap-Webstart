package de.argumap.UI;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import uk.ac.leeds.ccg.geotools.Viewer;

public class ArguMapLauncher {

    /**
     * @param args
     */
    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(
                    UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        
        SplashScreen splash = new SplashScreen();

        ArguMapWindow window = new ArguMapWindow(args);

        // initialize server-side session
        URL startURL;
        BufferedReader in;
        String sessionIDFromServer = "";
        try {
            startURL = new URL(window.getServletBase() + "start");
            in = new BufferedReader(
                    new InputStreamReader(startURL.openStream()));
            sessionIDFromServer = in.readLine();
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Session could not be initialized - Server did not answer. Shutting down.");
            System.exit(0);
        } 

        // pass session ID to the window
        window.setSessionID(sessionIDFromServer);

        // initialize client window
        window.init();
        window.start();
        
        // remove splash screen
        splash.setVisible(false);
        splash = null;
    }

}
