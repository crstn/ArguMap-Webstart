package uk.ac.leeds.ccg.widgets;

import java.awt.Canvas;
import java.awt.Graphics;

import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.ScaleChangedEvent;
import uk.ac.leeds.ccg.geotools.ScaleChangedListener;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.Viewer;
import uk.ac.leeds.ccg.geotools.misc.FormatedString;
import uk.ac.leeds.ccg.geotools.projections.ElipsoidalMercator;

public class ScaleBar extends Canvas implements ScaleChangedListener{
	private final static boolean DEBUG=false;
	private final static String DBC="ScB:";
	Scaler s;
	String units = "m";
	boolean isSI = true;
	boolean isProjected = false;
	int height = 40;
	int width = 170;
	ElipsoidalMercator proj;

	public ScaleBar(Viewer v,boolean projf){
		this(v);
		isProjected=projf;
		proj = new ElipsoidalMercator();
	}
	public ScaleBar(Viewer v){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.ScaleBar constructed. Will identify itself as "+DBC);
		s = v.getScale();
		s.addScaleChangedListener(this);
		setSize(width,height);
	}
	public void setUnits(String u){
		units = u;

	}
	public void setSI(boolean f){
		isSI=f;
	}
	public boolean getSI(){
		return isSI;
	}
	public void setProjected(boolean f){
		isProjected=f;
	}
	public boolean getProjected(){
		return isProjected;
	}

	int ticklen = 5; // how long the ticks are
	int inset =5; // distance from either end to the bar;
	int dp;
	public void paint(Graphics g){
		int len = 100;
		int h = getSize().height;
		int w = getSize().width;
		int lh = 2*h/3;
		len = w - 2*inset;
		if(DEBUG)System.out.println(DBC+"->len = "+len+" w "+w);
		double scaledLen;
		if(!isProjected){
			scaledLen = s.toMap(len);
		}else{
			GeoRectangle r = s.getMapExtent();
			double slen = s.toMap(len);
			double p1[] = proj.project(r.x,r.y+r.height/2.0);
			double p2[] = proj.project(r.x+slen,r.y+r.height/2.0);
			if(DEBUG){
				System.out.println(DBC+" in "+r.x+","+(r.y+r.height/2.0)+" -> "+
					(r.x+slen)+","+(r.y+r.height/2.0));
				System.out.println(DBC+" out "+p1[0]+","+p1[1]+" "+p2[0]+","+p2[1]);
			}

			scaledLen = (p2[0]-p1[0])*1000; // proj is in km
		}

		if(Double.isNaN(scaledLen)) return; // no scale in map
		double fac = Math.log(scaledLen)/Math.log(10.0);
		if(DEBUG)System.out.println(DBC+"->len = "+scaledLen+" "+fac);
		// can't just truncate for lengths less than 1!
		double size = Math.pow(10.0,Math.floor(fac)); 
		len=s.toGraphics(size);
		if(DEBUG)System.out.println(DBC+" size "+size+" len "+len+" fac "+fac);
		int fudge=0;
		while(len>w){
			size = Math.pow(10.0,Math.floor(--fac)); 
			fudge++;
			len=s.toGraphics(size);
			if(DEBUG)System.out.println(DBC+" size "+size+" len "+len+" fac "+fac);
		}
		int ticks=0;
		while(len<w-2*inset){
			ticks++;
			len=s.toGraphics(size*ticks);
		}
		ticks--;
		if(ticks==1){
			ticks*=10;
			size/=10;
		}
		dp = 1;
		if(fac<1) dp = -(int)Math.floor(fac)+1;
		len=s.toGraphics(size*ticks);
		scaledLen = size*ticks;
		if(DEBUG)System.out.println(DBC+"->len = "+len+" slen "+scaledLen+" size "+size);
		String tunits=units;

		if(isProjected&&fudge>0){
			if(DEBUG)System.out.println(DBC+"fudge = "+fudge+" size " + size +
			"slen "+scaledLen);
			while(fudge-->0){
				size*=10.0;
				scaledLen*=10.0;
			}
			if(DEBUG)System.out.println(DBC+"fudge = "+fudge+" size " + size +
			"slen "+scaledLen);
		}
		if(isSI&&scaledLen>1000.0){
			size/=1000.0;
			scaledLen/=1000.0;
			tunits ="k"+units;
		}		

		g.drawLine(inset,lh,inset+len,lh);
		double tlen = len/(double)ticks;
		if(DEBUG)System.out.println(DBC+"->ticks "+ticks+" "+tlen);
		int step =1;
		if(ticks>=6) step =3;
		if(ticks>=8) step = 4;
		if(ticks>=10) step = 5;
		for(int i=0;i<=ticks;i++){
			g.drawLine(inset+(int)Math.round(tlen*i),lh,inset+(int)Math.round(tlen*i),lh+ticklen);
			if(i%step==0||i==ticks){
				// should work out length of string (size*i) and centre label
				g.drawString(""+size*i,(int)(tlen*i),h);
				g.drawLine((int)Math.round(inset+tlen*i),lh-ticklen,(int)Math.round(inset+tlen*i),lh);
			}
		}
		//g.drawLine(inset+len,lh,inset+len,lh+ticklen);
		g.drawString(FormatedString.format(""+scaledLen,dp)+" "+tunits,w/2,h/2);
	}
	public void scaleChanged(ScaleChangedEvent sce){
		repaint();
	}
}

		
