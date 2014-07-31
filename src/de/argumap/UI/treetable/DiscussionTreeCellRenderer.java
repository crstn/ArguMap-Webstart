package de.argumap.UI.treetable;

import java.awt.Component;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import de.argumap.discussion.Contribution;

/**
 * @author Carsten Keﬂler, carsten.kessler@uni-muenster.de
 * @version 03.11.2004
 *  
 */
public class DiscussionTreeCellRenderer extends DefaultTreeCellRenderer {

    Icon proIcon;
    Icon conIcon;
    Icon questionIcon;
    Icon suggestionIcon;
    Icon neutralIcon;
    Icon proIconWC;
    Icon conIconWC;
    Icon questionIconWC;
    Icon suggestionIconWC;
    Icon neutralIconWC;

    /**
     * @param proIcon
     * @param conIcon
     * @param questionIcon
     * @param suggestionIcon
     * @param neutralIcon
     */
    public DiscussionTreeCellRenderer(Icon proIcon, Icon conIcon,
            Icon questionIcon, Icon suggestionIcon, Icon neutralIcon,
            Icon proIconWC, Icon conIconWC, Icon questionIconWC,
            Icon suggestionIconWC, Icon neutralIconWC) {
        super();
        this.proIcon = proIcon;
        this.conIcon = conIcon;
        this.questionIcon = questionIcon;
        this.suggestionIcon = suggestionIcon;
        this.neutralIcon = neutralIcon;
        this.proIconWC = proIconWC;
        this.conIconWC = conIconWC;
        this.questionIconWC = questionIconWC;
        this.suggestionIconWC = suggestionIconWC;
        this.neutralIconWC = neutralIconWC;
    }

    public Component getTreeCellRendererComponent(JTree tree, Object value,
            boolean sel, boolean expanded, boolean leaf, int row,
            boolean hasFocus) {

        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf,
                row, hasFocus);
        if (leaf && getType(value).equals("pro")) {
            setIcon(proIcon);
        } else if (leaf && getType(value).equals("contra")) {
            setIcon(conIcon);
        } else if (leaf && getType(value).equals("question")) {
            setIcon(questionIcon);
        } else if (leaf && getType(value).equals("suggestion")) {
            setIcon(suggestionIcon);
        } else if (leaf && getType(value).equals("neutral")) {
            setIcon(neutralIcon);
        } else if (!leaf && getType(value).equals("pro")) {
            setIcon(proIconWC);
        } else if (!leaf && getType(value).equals("contra")) {
            setIcon(conIconWC);
        } else if (!leaf && getType(value).equals("question")) {
            setIcon(questionIconWC);
        } else if (!leaf && getType(value).equals("suggestion")) {
            setIcon(suggestionIconWC);
        } else if (!leaf && getType(value).equals("neutral")) {
            setIcon(neutralIconWC);
        }

        return this;
    }

    private String getType(Object value) {
        Contribution con = (Contribution) value;
        return con.getType();
    }
}