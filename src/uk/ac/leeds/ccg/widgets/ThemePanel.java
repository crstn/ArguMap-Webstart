package uk.ac.leeds.ccg.widgets;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.CompositionChangedEvent;
import uk.ac.leeds.ccg.geotools.CompositionChangedListener;
import uk.ac.leeds.ccg.geotools.Layer;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.ThemeChangedEvent;
import uk.ac.leeds.ccg.geotools.ThemeChangedListener;
import uk.ac.leeds.ccg.geotools.Viewer;

public class ThemePanel extends java.awt.Panel implements
CompositionChangedListener, ItemListener, ThemeChangedListener {
    private final static boolean DEBUG=false;
    public static String cvsid = "$Id: ThemePanel.java,v 1.1 2005/09/19 10:31:29 CarstenKessler Exp $";
    Vector themes;
    Hashtable switches;
    Viewer view;
    Panel p ;
    
    private static Color background = new Color(255,255,255);
    private static Color foreground = new Color(0,0,0);
    
    public ThemePanel(Viewer view) {
        this(new Vector(1),view,100,100);
    }
    public ThemePanel(Vector t,Viewer view) {
        this(t,view,100,100);
    }
    
    public ThemePanel(Vector t,Viewer view, int width, int height){
        this(t,view,width,height,Color.black,Color.white);
    }
    public ThemePanel(Vector t,Viewer view, int width, int height, Color
    foreground, Color background) {
        if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.ThemePanel constructed. Will identify itself as ThP->");
        this.view = view;
        this.setSize(width,height);
        this.foreground = foreground;
        this.background = background;
        
        
        if(t!=null) setThemes(t);

  
    }
    int next = 0;
    public void addTheme(Theme t){
        Checkbox c = new Checkbox(t.getName(),view.isThemeVisible(t));
        c.addItemListener(this);
        switches.put(c,t);
        addComponent(p,c,0,next++,1,1,0.0,0.0);
        c.setVisible(true);
        c.addMouseMotionListener(new MouseMove());
        c.addMouseListener(new MouseRelease());
        t.addThemeChangedListener(this);
        //Button conf = new Button("Config");
        //conf.addActionListener(this);
    }
    
    public void setThemes(Vector th){
        
        if(DEBUG)System.out.println("Thp->setThemes with "+th.size()+" themes");
        if(switches !=null){
            Enumeration en = switches.keys();
            while(en.hasMoreElements()){
                ((Checkbox)en.nextElement()).removeItemListener(this);
            }
        }
        switches = null;
        switches = new Hashtable();
        
        if(p!=null) {
            if(DEBUG)System.out.println("Thp->Removing panel");
            remove(p);
        }
        p = new Panel();
        GridBagLayout gridbag = new GridBagLayout();
        p.setLayout(gridbag);
        if(DEBUG)System.out.println("Thp->Setting background: "+background.toString());
        p.setBackground(background);
        if(DEBUG)System.out.println("Thp->Setting foreground: "+foreground.toString());
        p.setForeground(foreground);
        //System.out.println("Thp->Setting bounds: "+String.valueOf(width)+" by "+String.valueOf(height));
        //p.setBounds(x,y,width,height);
        themes = th;
        //Enumeration e = themes.elements();
        
        next=0;
        //while(e.hasMoreElements()){
        for(int i=themes.size()-1;i>=0;i--){
            Theme t = (Theme)themes.elementAt(i);
            if(DEBUG)System.out.println("Thp->Adding view switch for "+t.getName());
            addTheme(t);
        }
        add(p);
        p.setVisible(true);
        //p.invalidate();
        
        this.validate();
        
    }
    
    //
    // Add component to applet using gridbaglayout
    //
    private void addComponent(Container cont, Component comp, int x, int y,
    int w, int h, double weightx, double weighty) {
        GridBagLayout gbl = (GridBagLayout)cont.getLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill	   = GridBagConstraints.BOTH ;
        c.gridx	   = x;
        c.gridy	   = y;
        c.gridwidth  = w;
        c.gridheight = h;;
        c.weightx	 = weightx;
        c.weighty	 = weighty;
        c.insets = new Insets(1,1,1,1);
        cont.add(comp);
        gbl.setConstraints(comp, c);
    }
    
    public void updateSwitches(){
        if(switches !=null){
            if(DEBUG)System.out.println("Thp->updating switches");
            Enumeration en = switches.keys();
            while(en.hasMoreElements()){
                Checkbox c = (Checkbox)en.nextElement();
                Theme t = (Theme)switches.get(c);
                c.setState(view.isThemeVisible(t));
            }
        }
    }
    
    
    public void itemStateChanged(ItemEvent e){
        Checkbox c = (Checkbox)e.getSource();
        Theme t = (Theme)switches.get(c);
        if(t!=null)
            view.setThemeIsVisible(t,c.getState());
    }
    
    public void compositionChanged(CompositionChangedEvent cce){
        Viewer v = (Viewer)cce.getSource();
        if(v.equals(view)) {
            if(cce.getReason()==cce.VISIBILITY) {
                updateSwitches();
            } else {
                setThemes(view.getThemes());
            }
        } else {
            System.err.println("Thp->Erk! - ThemePanel");
        }
    }
    
    public void setBackground(Color c){
        background=c;
        p.setBackground(c);
    }
    
    public void themeChanged(ThemeChangedEvent tce) {
        if(tce.getReason()==tce.GEOGRAPHY){
            Enumeration e = switches.keys();
            while(e.hasMoreElements()){
                Checkbox cb = (Checkbox)e.nextElement();
                Theme t= (Theme)switches.get(cb);
                cb.setEnabled(t.getLayer().getStatus()==Layer.COMPLETED);
                cb.setLabel(t.getName());
            }
        }
    }    
    
    class MouseRelease extends java.awt.event.MouseAdapter{
        public void mouseReleased(MouseEvent e){
            e.translatePoint(0,(int)e.getComponent().getLocation().y);
            Component c = p.getComponentAt(e.getX(),e.getY());
            Theme t2 = (Theme)switches.get(e.getSource());
            if(c==null){
                view.setThemeToBottom(t2);
                return;
            }
            //System.out.println("RELEASE on "+c+" from "+e.getSource());
            Theme t1 = (Theme)switches.get(c);
            
            
                                   
            Graphics g = getGraphics();
            g.setColor(getBackground());
            g.fillRect(0,0,getSize().width,getSize().height);
            
            if(t1!=null && t2!=null && t1!=t2){
                view.setThemeWaighting(t2,view.getThemeWaighting(t1));
            }
            
        }
    }
    class MouseMove extends java.awt.event.MouseMotionAdapter{
        public void mouseDragged(MouseEvent e){
            //System.out.println(e.getComponent());
            e.translatePoint(0,e.getComponent().getLocation().y);
            Component c = p.getComponentAt(e.getX(),e.getY());
            int y;
            if(c==null) y = p.getSize().height;
            else{
                y = c.getLocation().y;
            }
            if(c==p) y = e.getY();
            //repaint();
           
            Graphics g = getGraphics();
            g.setColor(getBackground());
            g.fillRect(0,0,getSize().width,getSize().width);
            
            g.setColor(Color.red);

            g.fillRect(0,y,getSize().width,4);
            
            

            
        }
    }
    
    
    
    
    
    
    
    
    
    //{{DECLARE_CONTROLS
    //}}
    
}
