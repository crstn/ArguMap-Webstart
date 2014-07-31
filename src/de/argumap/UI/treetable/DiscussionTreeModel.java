package de.argumap.UI.treetable;

import de.argumap.UI.ArguMapWindow;
import de.argumap.discussion.Contribution;

/**
 * FileSystemModel is a TreeTableModel representing a hierarchical file system.
 * Nodes in the FileSystemModel are FileNodes which, when they are directory
 * nodes, cache their children to avoid repeatedly querying the real file
 * system.
 * 
 * @version %I% %G%
 * 
 * @author Philip Milne
 * @author Scott Violet
 */

public class DiscussionTreeModel extends AbstractTreeTableModel implements
        TreeTableModel {

    // Names of the columns.
    static protected String[] cNames = { "Title", "Author", "Date" };

    // Types of the columns.
    static protected Class[] cTypes = { TreeTableModel.class, String.class,
            String.class };

    public DiscussionTreeModel(ArguMapWindow window) {
        //super(new ContributionNode(new Contribution()));
        super(new Contribution(window));
    }

    //
    // Some convenience methods.
    //

    protected Object[] getChildren(Object node) {
        Contribution contribution = ((Contribution) node);
        return contribution.getChildren();
    }
    
       //
    // The TreeModel interface
    //

    public int getChildCount(Object node) {
        Object[] children = getChildren(node);
        return (children == null) ? 0 : children.length;
    }

    public Object getChild(Object node, int i) {
        return getChildren(node)[i];
    }

    // The superclass's implementation would work, but this is more efficient.
    public boolean isLeaf(Object node) {
        //return getContribution(node).isFile();
        return super.isLeaf(node);
    }

    //
    //  The TreeTableNode interface.
    //

    public int getColumnCount() {
        return cNames.length;
    }

    public String getColumnName(int column) {
        return cNames[column];
    }

    public Class getColumnClass(int column) {
        return cTypes[column];
    }

    public Object getValueAt(Object node, int column) {
        Contribution contribution = (Contribution)node;
        try {
            switch (column) {
            case 0:
                return contribution.getTitle();
            case 1:
                return (contribution.getCreator().getFirstName() + " " + contribution
                        .getCreator().getLastName());
            case 2:
                return contribution.getDateInfoString();
            }

        } catch (SecurityException se) {
        }

        return null;
    }
}

