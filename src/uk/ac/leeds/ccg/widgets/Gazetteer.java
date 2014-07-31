package uk.ac.leeds.ccg.widgets;
// first import standard java packages that we will need
import java.awt.Button;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Label;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.MixedLayer;
import uk.ac.leeds.ccg.geotools.PointLayer;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.ShadeStyle;
import uk.ac.leeds.ccg.geotools.ShapefileReader;
import uk.ac.leeds.ccg.geotools.SimpleGeoData;
import uk.ac.leeds.ccg.geotools.TernaryGeoData;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.Tool;
import uk.ac.leeds.ccg.geotools.Viewer;

public class Gazetteer implements Tool,ActionListener,KeyListener
{
		SelectionManager sm = new SelectionManager();
		HighlightManager hm = new HighlightManager();
		Viewer view;
		MixedLayer master;
		TextField cityname;
   	GeoData tips; 
		TernaryGeoData names;
		ZoomSelectionList resultsList;
		Panel searchPanel;
		PointLayer pl;
		Theme theme = new Theme();
		TextArea help;
		String helptext="Type in a place name or part name in the "+
		"box above.\nAs you type the names of places that match what you have "+
		"already typed will appear in the list.\n"+
		"Selecting a name in the list will "+
		"highlight it in red on the map. Double clicking a name will centre the "+
		"map on the selected point and zoom in to that point. " ;
    
    public Gazetteer(String shapefile, String tooltip,Viewer v){
			super();
			view=v;
			try{
				loadMap(shapefile,tooltip);
			}catch(IOException e){
				System.err.println("Error loading map");

			}
			GridBagConstraints gbc= new GridBagConstraints();
			gbc.fill = gbc.NONE;
			gbc.insets = new Insets(1,1,1,1);
			gbc.gridwidth=2;
			//gbc.gridx = 0;
			gbc.gridy = 0;
			gbc.weightx = 0; 
			gbc.weighty = 0;


			searchPanel = new Panel();
			searchPanel.setLayout(new GridBagLayout());

			cityname = new TextField(12);
			cityname.addActionListener(this);
			cityname.addKeyListener(this);
			searchPanel.add(new Label("Name:"),gbc);

			//gbc.gridx = 1;
			searchPanel.add(cityname,gbc);

		
			//gbc.gridx = 0;
			gbc.gridy = 1;
			Button search = new Button("Search");
			search.addActionListener(this);
			searchPanel.add(search,gbc);
			Button clear = new Button("Clear");
			clear.addActionListener(this);
			//gbc.gridx = 1;
			searchPanel.add(clear,gbc);
			help = new TextArea(helptext,10,10,TextArea.SCROLLBARS_VERTICAL_ONLY);
			help.setEditable(false);

			gbc.gridx = 0;
			gbc.gridy=2;
			gbc.weighty = 1;
			gbc.weightx = 1;
			gbc.gridwidth=gbc.REMAINDER;
			gbc.gridheight=gbc.RELATIVE;
			gbc.fill=gbc.BOTH;
			resultsList = new ZoomSelectionList(names,view);
			resultsList.setSelectionManager(sm);
			resultsList.setHighlightManager(hm);
			searchPanel.add(resultsList,gbc);
			gbc.gridx = 0;
			gbc.gridy=3;
			gbc.weighty = 0;
			gbc.weightx = 1;
			gbc.gridwidth=gbc.REMAINDER;
			gbc.gridheight=gbc.REMAINDER;
			gbc.fill=gbc.HORIZONTAL;
			searchPanel.add(help,gbc);
		
    }
		public void setHighlightManager(HighlightManager h){
			hm=h;
			resultsList.setHighlightManager(hm);
			theme.setHighlightManager(hm);
		}
		public void setSelectionManager(SelectionManager s){
			sm=s;
			resultsList.setSelectionManager(sm);
			theme.setSelectionManager(sm);
		}
		public Cursor getCursor(){return null;}
		public String getName(){return "Gazetteer";}
		public String getDescription(){return "Gazetteer";}
		public void paint(Graphics g){}
		public void setContext(Viewer v){view=v;}
		public void update(Graphics g, int i){}
		public Theme getTheme(){return theme;}
		public Panel getPanel(){return searchPanel;}
		public void setHelpText(String text){
			help.setText(text);
		}
		public String getHelpText(){return help.getText();}

    public void loadMap(String shapefile,String tooltip) throws IOException{
        
				URL url = new URL(shapefile);
        ShapefileReader sfr = new ShapefileReader(url,0);
        master  = (MixedLayer)sfr.readPoints();
				SimpleGeoData n = (SimpleGeoData)sfr.readData(tooltip);
			  names = new TernaryGeoData(n);
       	theme.setSelectionManager(sm);
       	theme.setHighlightManager(hm);
       	ShadeStyle xx = theme.getShadeStyle();
       	xx.setIsFilled(false);
       	xx.setIsOutlined(false);
       	theme.setStyle(xx);
				//master.setDefaultSize(5);
                                theme.getShadeStyle().setLineWidth(5);
				theme.setLayer(master);
				ShadeStyle x = theme.getSelectionShadeStyle();
				x.setLineWidth(4);
				x.setFillColor(Color.green);
        x.setLineColor(Color.green);
				theme.setSelectionStyle(x);
				theme.getHighlightShadeStyle().setLineWidth(3);
        
        //The GeoData created above is now used as the Tip Data for the theme.
        theme.setTipData(names);
    }
    

	public void keyPressed(KeyEvent e){}
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){
		if(cityname.getText().length()>1){
			actionPerformed(new ActionEvent(cityname,1,"Search"));	
		}
	}
	public void actionPerformed(ActionEvent e){
		int [] ida = new int[1];
		ida[0]=0;
		hm.setHighlight(0);
		sm.clearSelection();
		
    if(e.getActionCommand().equals("Clear")){
			sm.clearSelection();
			cityname.setText("");
			hm.setHighlight(0);
      return;
    }
		// assume search button sent us an event
		// grab what ever is in the textfield name
		String find=null;
    int [] idx;
    
    find = cityname.getText().trim();
    
    if(find==null) return;
		
		idx = search(find);
		if(idx==null) return;
		pl = new PointLayer();
		pl.setDefaultSize(5);
		for(int i=0;i<idx.length;i++){
			pl.addGeoPoint((GeoPoint)master.getGeoShape(idx[i]));
		}
		theme.setLayer(pl);
		sm.setSelection(idx);
	}
	public int[] search(String find){
		if(find.equals(""))return null;
    int [] idx;
		Vector found = new Vector();
    char [] c = find.toCharArray();
    c[0] = Character.toUpperCase(c[0]);
    find = new String(c);

		int id=0;
		
		id = names.getID(find);
		if(id!=-1)found.addElement(new Integer(id));
		Vector fnames =null;
		fnames = names.matchPrefix(find);
		Iterator n = fnames.iterator();
		while(n.hasNext()){
			String nout = (String)n.next();
			id = names.getID(nout);
			found.addElement(new Integer(id));
		}

		if(found.size()==0){
			return null;
		}
	  idx = new int[found.size()];
		for(int i =0;i<found.size();i++){
      idx[i]=((Integer)found.elementAt(i)).intValue();
    }
		return idx;	
	}
	class ZoomSelectionList extends SelectionList{
		Viewer v;
		public ZoomSelectionList(GeoData d,Viewer v){
			super(d);
			this.v=v;
		}
		public void actionPerformed(ActionEvent e){
			int id = ((Integer)idx.elementAt(this.getSelectedIndex())).intValue();
			v.zoomInOnPoint((GeoPoint)pl.getGeoShape(id),700);
		}
	}
}
