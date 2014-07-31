package de.argumap.UI.treetable;

import java.util.Iterator;
import java.util.Vector;

import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeExpansionListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.tree.TreePath;

/**
 * This is a wrapper class takes a TreeTableModel and implements the table model
 * interface. The implementation is trivial, with all of the event dispatching
 * support provided by the superclass: the AbstractTableModel.
 * 
 * @version %I% %G%
 * 
 * @author Philip Milne
 * @author Scott Violet
 */

public class TreeTableModelAdapter extends AbstractTableModel {
    JTree tree;
    TreeTableModel treeTableModel;
    // "children" should only be accessed by getNodes()!!
    private Vector children;
    private Vector path;

    public TreeTableModelAdapter(TreeTableModel treeTableModel, JTree tree) {
        this.tree = tree;
        this.treeTableModel = treeTableModel;
        this.children = new Vector();

        tree.addTreeExpansionListener(new TreeExpansionListener() {
            // Don't use fireTableRowsInserted() here;
            // the selection model would get updated twice.
            public void treeExpanded(TreeExpansionEvent event) {
                fireTableDataChanged();
            }

            public void treeCollapsed(TreeExpansionEvent event) {
                fireTableDataChanged();
            }
        });
    }

    // Wrappers, implementing TableModel interface.

    public int getColumnCount() {
        return treeTableModel.getColumnCount();
    }

    public String getColumnName(int column) {
        return treeTableModel.getColumnName(column);
    }

    public Class getColumnClass(int column) {
        return treeTableModel.getColumnClass(column);
    }

    public int getRowCount() {
        return tree.getRowCount();
    }

    protected Object nodeForRow(int row) {
        TreePath treePath = tree.getPathForRow(row);
        return treePath.getLastPathComponent();
    }
    
    /**
     * Calculates the row of a node in the tree.
     * 
     * @param node - We are lokking for the row where node is placed.
     * @return  The row number, or -1 if the node is not in the tree.
     */
    public int rowForNode(Object node){
        int row = 0;
        while(row < getRowCount()){
//            System.out.println("Comparin 2 nodes: ");
//            System.out.println(nodeForRow(row).toString());
//            System.out.println(node.toString());
            
            if(nodeForRow(row).equals(node)){
                return row;
            }
            row++;
        }
        //if the node is not in the tree, return -1
        return -1;
    }

    public Object getValueAt(int row, int column) {
        return treeTableModel.getValueAt(nodeForRow(row), column);
    }

    public boolean isCellEditable(int row, int column) {
        return treeTableModel.isCellEditable(nodeForRow(row), column);
    }

    public void setValueAt(Object value, int row, int column) {
        treeTableModel.setValueAt(value, nodeForRow(row), column);
    }

    /*
     * Returns all nodes in this tree.
     */
    public Vector getNodes() {
        //flush the vector to be returned first - in case the method has been called before:
        children = new Vector();
        //refill the vector using the private recursive function:
        return getAllChildren(treeTableModel.getRoot());
    }

    /**
     * Returns all children of node; recursive private convenience method.
     * 
     * @param node
     * @return A Vector containing all children of node.
     */
    private Vector getAllChildren(Object node) {
        int numChildren = treeTableModel.getChildCount(node);
        for (int i = 0; i < numChildren; i++) {
            children.add(treeTableModel.getChild(node, i));
            getAllChildren(treeTableModel.getChild(node, i));
        }
        return children;
    }

    public TreePath getPathToNode(Object node) {
        path = new Vector();
        determineReversePath(node);
        path.add(treeTableModel.getRoot());
        //dann den Pfad umdrehen und in ein Object[] schreiben
        int i = path.size()-1;
        int j = 0;
        Object[] output = new Object[path.size()];
        while(j < output.length){
            output[j] = path.get(i);
            i--;
            j++;
        }
        TreePath tp = new TreePath(output);
        return tp;
    }

    public boolean isChildOf(Object parent, Object child) {
        int numChildren = treeTableModel.getChildCount(parent);
        for (int i = 0; i < numChildren; i++) {
            if ((treeTableModel.getChild(parent, i)).equals(child)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the parent object of child in the tree, or null if child is the
     * root of the tree.
     * 
     * @param child
     */
    public Object getParent(Object child) {
        Iterator iter = getNodes().iterator();
        while (iter.hasNext()) {
            Object current = iter.next();
            if (isChildOf(current, child)) {
                return current;
            }
        }
        return null;
    }
    
    private void determineReversePath(Object node){
        path.add(node);
        if(getParent(node)!=null){
            determineReversePath(getParent(node));
        }
    }
    
    //CK
    public JTree getTree(){
        return tree;
    }
    
}

