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
 * @author Carsten Keßler, carsten.kessler@uni-muenster.de
 * @version 15.07.2004
 *  
 */
public class NewAccountPanel extends Panel implements ActionListener {

    private CardPanel container;
    private JTextField first;
    private JTextField last;
    private JTextField email;
    private JPasswordField pass;
    private JPasswordField confirm;

    public NewAccountPanel(CardPanel container) {
        this.container = container;
        setLayout(new BorderLayout());

        Panel holder = new Panel();
        holder.setLayout(new GridLayout(6, 2, 5, 5));

        //gui-elemente anlegen:
        JLabel fristLabel = new JLabel("Frist name:");
        JLabel lastLabel = new JLabel("Last name:");
        JLabel emailLabel = new JLabel("E-mail address:");
        JLabel passLabel = new JLabel("Password:");
        JLabel confirmLabel = new JLabel("Confirm password:");
        JLabel empty = new JLabel(" ");

        first = new JTextField();
        last = new JTextField();
        email = new JTextField();

        pass = new JPasswordField();
        confirm = new JPasswordField();

        JButton submit = new JButton("Create new account");
        submit.addActionListener(this);

        holder.add(fristLabel);
        holder.add(first);
        holder.add(lastLabel);
        holder.add(last);
        holder.add(emailLabel);
        holder.add(email);
        holder.add(passLabel);
        holder.add(pass);
        holder.add(confirmLabel);
        holder.add(confirm);
        holder.add(empty);
        holder.add(submit);

        add(holder, "North");

    }

    public void actionPerformed(ActionEvent e) {
        //checken, ob alles ausgefüllt wurde:
        if (first.getText().trim().length() == 0) {
            errorMessage("Please fill in your first name.");
            return;
        }
        if (last.getText().trim().length() == 0) {
            errorMessage("Please fill in your last name.");
            return;
        }
        if (email.getText().trim().length() == 0) {
            errorMessage("Please fill in your e-mail address.");
            return;
        }
        // checken, ob die e-mail adresse auch eine sein kann
        //- d.h. ob sie irgendwas in der form bla@domain.com ist
        if (email.getText().trim().indexOf("@") == -1) {
            errorMessage("The e-mail address you entered does not seem to be valid.");
            return;
        }
        if (email.getText().trim().indexOf(".") == -1) {
            errorMessage("The e-mail address you entered does not seem to be valid.");
            return;
        }
        
        String passString = new String(pass.getPassword());
        String confirmString = new String(confirm.getPassword());
        if (passString.trim().length() == 0) {
            errorMessage("Please choose a password.");
            return;
        }
        if (confirmString.trim().length() == 0) {
            errorMessage("Please re-enter your password for validation.");
            return;
        }
        //checken, ob die beiden passwörter übereinstimmen:
        if (!passString.equals(confirmString)) {
            pass.setText("");
            confirm.setText("");
            errorMessage("The password you entered was not the same you entered for validation. "
                    + "Please fill these fields in again.");
            return;
        }

        //wenn man hier ankommt, scheint alles ok zu sein.
        //neuer eintrag in der datenbank:
        try {
            URL newAccountURL = new URL(container.getArguMapWindow().getServletBase()
                    + "newaccount;jsessionid="+container.getArguMapWindow().getSessionID()+"?firstname=" + first.getText() + "&lastname="
                    + last.getText() + "&email=" + email.getText()
                    + "&password=" + passString);
            System.out.println(newAccountURL);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    newAccountURL.openStream()));
            String okay = in.readLine();
            System.out.println("antwort vom server beim versuch, neuen user zu registrieren: "+okay);
            in.close();
        } catch (IOException ew) {
            ew.printStackTrace();
        }

        //jetzt den neu registrierten user einloggen:
        URL passURL;
        String loginString = "keine DB Verbindung";
        try {
            passURL = new URL(container.getArguMapWindow().getServletBase()
                    + "login;jsessionid="+container.getArguMapWindow().getSessionID()+"?email=" + email.getText() + "&pw=" + passString);
            BufferedReader in = new BufferedReader(new InputStreamReader(
                    passURL.openStream()));
            loginString = in.readLine();
            in.close();
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e2) {
            e2.printStackTrace();
        }

        if (loginString.equals("keine DB Verbindung")) {
            System.out.println("Servlet konnte nicht aufgerufen werden!");
        } else if (loginString.equals("false")) {
            System.out.println("falsches passwort");
        } else {
            //vom servlet zurückgegebenen string auslesen:
            String[] participatorDataArray = loginString.split(" /// ");
            Integer m_id = new Integer(participatorDataArray[0]);
            String firstname = participatorDataArray[1];
            String lastname = participatorDataArray[2];
            //neues participator-objekt anlegen und als logged in registrieren
            Participator loggedInParticipator = new Participator(m_id
                    .intValue(), firstname, lastname, email.getText());
            container.getArguMapWindow().setLoggedIn(loggedInParticipator);

        }

        // zur Anzeige der Diskussion:
        CardLayout cl = (CardLayout) container.getLayout();
        cl.show(container, "arguPanel");

    }

    private void errorMessage(String message) {
        JOptionPane.showMessageDialog(container.getParent(), message, "error",
                JOptionPane.ERROR_MESSAGE);
    }

}