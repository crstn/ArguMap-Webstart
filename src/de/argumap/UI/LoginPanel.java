package de.argumap.UI;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import de.argumap.discussion.Participator;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 14.07.2004
 * 
 */
public class LoginPanel extends Panel implements ActionListener {

    private CardPanel container;

    private JTextField email;
    private JPasswordField password;
    private Panel holder;

    public LoginPanel(CardPanel container) {
        this.container = container;
        holder = new Panel();

        this.setLayout(new BorderLayout());
        holder.setLayout(new GridLayout(9, 2, 5, 5));

        JLabel emailLabel = new JLabel("E-mail:");
        email = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        password = new JPasswordField();
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(this);
        JButton newAccount = new JButton("Create new account");
        JButton cancel = new JButton("Cancel");
        cancel.addActionListener(this);

        newAccount.addActionListener(this);
        holder.add(new JLabel(""));
        holder.add(new JLabel(""));
        holder.add(emailLabel);
        holder.add(email);
        holder.add(passwordLabel);
        holder.add(password);
        holder.add(new JLabel(""));
        holder.add(loginButton);
        holder.add(new JLabel(""));
        holder.add(new JLabel(""));

        holder.add(new JLabel("No account yet?"));
        holder.add(newAccount);

        holder.add(new JLabel(""));
        holder.add(new JLabel(""));
        holder.add(new JLabel(""));
        holder.add(cancel);

        add(holder, "North");
    }

    public void actionPerformed(ActionEvent e) {

        if (e.getActionCommand() == "Login") {
            String enteredPass = new String(password.getPassword());
            boolean correctLogin = false;
            // check the password (using the corresponding servlet):
            URL passURL;
            String loginString = "connection to database failed";
            try {
                passURL = new URL(container.getArguMapWindow().getServletBase()
                        + "login;jsessionid="
                        + container.getArguMapWindow().getSessionID()
                        + "?email=" + email.getText() + "&pw=" + enteredPass);
                BufferedReader in = new BufferedReader(new InputStreamReader(
                        passURL.openStream()));
                loginString = in.readLine();
                in.close();
            } catch (MalformedURLException e1) {
                e1.printStackTrace();
            } catch (IOException e2) {
                e2.printStackTrace();
            }

            if (loginString.equals("connection to database failed")) {
                System.out
                        .println("Error calling servlet... database seems to be down.");
            } else if (loginString.equals("false")) {
                System.out.println("Login failed: wrong password!");
            } else {
                // read the string returned by the servlet:
                String[] participatorDataArray = loginString.split(" /// ");
                Integer m_id = new Integer(participatorDataArray[0]);
                String firstname = participatorDataArray[1];
                String lastname = participatorDataArray[2];
                // log in the newly registered participator:
                Participator loggedInParticipator = new Participator(m_id
                        .intValue(), firstname, lastname, email.getText());
                container.getArguMapWindow().setLoggedIn(loggedInParticipator);
                correctLogin = true;
            }

            if (!correctLogin) {
                // Error message:
                JOptionPane
                        .showMessageDialog(
                                container.getParent(),
                                "Wrong e-mail adress and / or password! Please try again. "
                                        + "In case you have no account yet, click 'sign up'.",
                                "error", JOptionPane.ERROR_MESSAGE);
            } else {
                CardLayout cl = (CardLayout) container.getLayout();
                cl.show(container, "arguPanel");

            }
        } else if (e.getActionCommand() == "Create new account") {
            // switch to view "new account" panel:
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "newAccountPanel");

        } else if (e.getActionCommand() == "Cancel") {
            CardLayout cl = (CardLayout) container.getLayout();
            cl.show(container, "arguPanel");
//            ArguPanel ap = (ArguPanel) container.getComponent(0);
//            ap.setDiscussEnabled(false);

        }
    }

    public void reset() {
        email.setText("");
        password.setText("");
    }
}