package de.argumap.UI;

import java.awt.CardLayout;
import javax.swing.JPanel;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 14.07.2004
 *  
 */
public class CardPanel extends JPanel {

    private ArguPanel arguPanel;
    private LoginPanel loginPanel;
    private SendPanel sendPanel;
    private NewAccountPanel newAccountPanel;
    private ArguMapWindow window;

    public CardPanel(ArguMapWindow window) {
        this.window = window;

        CardLayout cardLayout = new CardLayout();
        setLayout(cardLayout);

        loginPanel = new LoginPanel(this);
        sendPanel = new SendPanel(this);
        arguPanel = new ArguPanel(this);
        newAccountPanel = new NewAccountPanel(this);
                
        add("arguPanel", arguPanel);
        add("loginPanel", loginPanel);
        add("sendPanel", sendPanel);
        add("newAccountPanel", newAccountPanel);
    }

    /**
     * @return Returns the arguPanel.
     */
    public ArguPanel getArguPanel() {
        return arguPanel;
    }

    /**
     * @param arguPanel
     *            The arguPanel to set.
     */
    public void setArguPanel(ArguPanel arguPanel) {
        remove(this.arguPanel);
        this.arguPanel = arguPanel;
        add("arguPanel", this.arguPanel);
    }

    /**
     * @return Returns the loginPanel.
     */
    public LoginPanel getLoginPanel() {
        return loginPanel;
    }

    /**
     * @param loginPanel
     *            The loginPanel to set.
     */
    public void setLoginPanel(LoginPanel loginPanel) {
        this.loginPanel = loginPanel;
    }

    /**
     * @return Returns the sendPanel.
     */
    public SendPanel getSendPanel() {
        return sendPanel;
    }

    /**
     * @param sendPanel
     *            The sendPanel to set.
     */
    public void setSendPanel(SendPanel sendPanel) {
        this.sendPanel = sendPanel;
    }

    /**
     * @return Returns the newAccountPanel.
     */
    public NewAccountPanel getNewAccountPanel() {
        return newAccountPanel;
    }

    /**
     * @param newAccountPanel
     *            The newAccountPanel to set.
     */
    public void setNewAccountPanel(NewAccountPanel newAccountPanel) {
        this.newAccountPanel = newAccountPanel;
    }

    /**
     * @return Returns the applet.
     */
    public ArguMapWindow getArguMapWindow() {
        return window;
    }
}