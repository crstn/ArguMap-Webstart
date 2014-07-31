package uk.ac.leeds.ccg.geotools;

import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;

public class TernaryGeoData extends SimpleGeoData {
    //TernarySearchTree tree = new TernarySearchTree();
    HashMap hash = new HashMap();

    public TernaryGeoData() {
    }

    public TernaryGeoData(GeoData d) {
        java.util.Enumeration e = d.getIds();
        Vector v = new Vector();
        Integer I;
        int count = 0;
        while (e.hasMoreElements()) {
            I = (Integer) e.nextElement();
            v.addElement(I);
            if (count % 1000 == 0 && count > 900) {
                Collections.shuffle(v);
                for (int i = 0; i < v.size(); i++) {
                    I = (Integer) v.elementAt(i);
                    setText(I, d.getText(I.intValue()).trim());
                }
                v = new Vector(1000);
            }
        }

        Collections.shuffle(v);
        for (int i = 0; i < v.size(); i++) {
            I = (Integer) v.elementAt(i);
            setText(I, d.getText(I.intValue()).trim());
        }
    }

    public String setText(Integer id, String s) {
       // tree.put(s, id);
        return (String) hash.put(id, s);
    }

    public String setText(int id, String s) {
        Integer i = new Integer(id);
        //tree.put(s, i);
        return (String) hash.put(i, s);
    }

    public String getText(int id) {
        return (String) hash.get(new Integer(id));
    }

    public int getID(String s) {
//        Integer I = (Integer) tree.get(s);
//        if (I == null)
//            return -1;
//        return I.intValue();
        return 0; //CK added
    }

    public Vector matchPrefix(String start) {
    //        DoublyLinkedList d = tree.matchPrefix(start);
    //        Vector found = new Vector(d.size());
    //        DoublyLinkedList.DLLIterator n = d.iterator();
    //        while (n.hasNext()) {
    //            String nout = (String) n.next();
    //            found.addElement(nout);
    //        }
    //        return found;
        return new Vector(0); //CK added
    }

    public Vector matchAlmost(String start) {
//        tree.setMatchAlmostDiff(1);
//        DoublyLinkedList d1 = tree.matchAlmost(start);
//        tree.setMatchAlmostDiff(2);
//        DoublyLinkedList d2 = tree.matchAlmost(start);
//        Vector found = new Vector(d1.size() + d2.size());
//        DoublyLinkedList.DLLIterator n = d1.iterator();
//        while (n.hasNext()) {
//            String nout = (String) n.next();
//            found.addElement(nout);
//        }
//        DoublyLinkedList.DLLIterator n2 = d2.iterator();
//        while (n2.hasNext()) {
//            String nout = (String) n2.next();
//            found.addElement(nout);
//        }
//        return found;
        return new Vector (0); //CK added

    }
}