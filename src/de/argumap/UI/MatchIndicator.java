package de.argumap.UI;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import de.argumap.UI.treetable.TreeTableModelAdapter;
import de.argumap.discussion.Contribution;

public class MatchIndicator extends JPanel implements MouseListener {

    private ArguPanel arguPanel;

    public MatchIndicator(ArguPanel arguPanel) {
        this.arguPanel = arguPanel;
        // we need no LayoutManager because we position all indicators directly
        // via setBounds()
        setLayout(null);
        setPreferredSize(new Dimension(12, getHeight()));

    }

    public void update(Contribution[] cons) {
        // remove all existing indicators
        removeAll();

        // add a indicator for every contribution
        for (int i = 0; i < cons.length; i++) {
            addIndicator(cons[i]);
        }
        repaint();
    }

    private void addIndicator(Contribution con) {
        int position = calculatePosition(con);

        Indicator indicator = new Indicator();

        indicator.setToolTipText(con.getTitle());
        indicator.setActionCommand((new Integer(con.getContributionID()))
                .toString());
        indicator.addMouseListener(this);
        indicator.setBounds(0, position, 12, 5);
        // setPreferredSize(new Dimension(12, getHeight()));
        add(indicator);
    }

    /**
     * Calculates the position for an indicator icon.
     * 
     */
    private int calculatePosition(Contribution con) {
        // determine the row where the contribution is located in the tree
        TreeTableModelAdapter ttma = arguPanel.getTreeTable()
                .getTreeTableModelAdapter();
        double row = ttma.rowForNode(con);
        // determine the total number of rows
        double allRows = ttma.getRowCount();

        // calculate the relative position and transfer this to the height of
        // the MatchIndicator
        double ratio = row / allRows;

        // TODO: this might result in 2 indicators ending up in the same layout
        // cell!!
        double numCells = getHeight();
        double position = numCells * ratio;
        return (int) position;
    }

    public void mouseClicked(MouseEvent e) {
        JButton source = (JButton) e.getSource();
        int id = (new Integer(source.getActionCommand())).intValue();
        Contribution con = arguPanel.getContributionByID(id);
        TreeTableModelAdapter ttma = arguPanel.getTreeTable()
                .getTreeTableModelAdapter();
        arguPanel.scrollToPath(ttma.getPathToNode(con));

    }

    // Overriding JButton to
    // - use tooltips, action commands, etc. but
    // - get a custom painting:
    private class Indicator extends JButton {
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2D = (Graphics2D) g;

            // draw a 1px border:
            g2D.setPaint(Color.GRAY);
            g2D.drawRect(0, 0, getWidth(), getHeight());

            // draw green box inside:
            g2D.setPaint(Color.green);
            g2D.fillRect(1, 1, getWidth() - 2, getHeight() - 2);

        }
    }

    /*
     * THESE DO NOTHING... JUST TO IMPLEMENT MOUSELISTENER
     */
    public void mousePressed(MouseEvent e) {
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

}
