package de.argumap.UI;

import java.awt.Color;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class StreamCatcher extends OutputStream {
    JTextArea myConsole;
    JScrollPane scrollPane;
    
    StreamCatcher(int rows, int cols) {
        super();
        myConsole = new JTextArea(rows, cols);
        myConsole.setBackground(Color.BLACK);
        myConsole.setForeground(Color.WHITE);
        scrollPane = new JScrollPane(myConsole);
    }

    void log(String text) {
        //myConsole.append(text + "\n");
        // Hier muss noch evtl. eine Behandlung zum automatischen Scrollen
        // rein.
        myConsole.append(text);
        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
    }

    // Jetzt werden saemtliche "write" Methoden der Klasse OutputStream
    // ueberschrieben
    public void write(int b) throws IOException {
        log(Integer.toString(b));
    }

    public void write(byte b[]) throws IOException {
        log(new String(b));
    }

    public void write(byte b[], int off, int len) throws IOException {
        if (len == 1 && b[0] == 10)
            return; // Hier besser ein Exception ausloesen !!
        log(new String(b, off, len));
    }
    
    public JScrollPane getConsole(){
        return scrollPane;
    }

}
