package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class RemoteObjectLayer extends uk.ac.leeds.ccg.geotools.SimpleLayer implements Runnable
{
	private final static boolean DEBUG=false;    
    GeoCircle car = new GeoCircle(1, 214905+((344383-214905)/2),636767+((713865-636767)/2), 20);
    Thread thread;
    boolean running = false;
    int interval = 5000;
    
    URL url;
    URLConnection uc;
    BufferedReader br;
    
    public RemoteObjectLayer(URL url){
        this.url = url;
    }
    
    private void refresh(){
        try{
            uc = url.openConnection();
            uc.setDoInput(true);
            br = new BufferedReader(new InputStreamReader(uc.getInputStream()));
            String line = br.readLine();
            if(DEBUG)System.out.println(line);
            int sep = line.indexOf(',');
            double x = new Double(line.substring(0,sep)).doubleValue();
            double y = new Double(line.substring(sep+1,line.length())).doubleValue();
            GeoPoint p1 = new GeoPoint(x,y);
            if(!p1.equals(car.getCentroid())){
                if(DEBUG)System.out.println("Position changed");
                car.setCentre(p1);
                notifyLayerChangedListeners(LayerChangedEvent.ANIMATION);
            }
            p1=null;
        }
        catch(IOException e){
            System.err.println("Remote GeoData exception @"+url+" "+e);
        }
        
    }
    public void paintScaled(GeoGraphics g){
        paintScaled(g.getGraphics(),g.getScale(),g.getShade(),g.getData(),g.getStyle());
    }
    
    public void paintScaled(Graphics g,Scaler scale, Shader shade,GeoData data,ShadeStyle style){
        if(DEBUG)System.out.println("painting "+car);
        GeoCircle temp = car;
        int r=20;
		int p[];
			p = scale.toGraphics(temp.getX(),temp.getY());
			
			//Add thematic colour here
			double value = data.getValue(temp.getID());
			g.setColor(shade.getColor(value));
			if(style.isFilled()){
			  g.fillOval(p[0]-r,p[1]-r,2*r+1,2*r+1);
			}
			if(style.isOutlined()){
			    if(!style.isLineColorFromShader()){
			        g.setColor(style.getLineColor());
			    }
			    g.drawOval(p[0]-r,p[1]-r,2*r,2*r);
			}
			
	}
    
    
    
    public GeoRectangle getBounds()
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public GeoRectangle getBoundsOf(int id[])
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public GeoRectangle getBoundsOf(int id)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public int getID(GeoPoint p)
    {
        if(car.contains(p)){return car.getID();}
        return -1;
    }

    public int getID(double x, double y)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return getID(new GeoPoint(x,y));
    }

    public void paintHighlight(Graphics g, Scaler scale, int id, ShadeStyle style)
    {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
    }

    public void run(){
        if(DEBUG)System.out.println("Run called");
        try{
        while(running){
            refresh();
            thread.sleep(interval);
        }
        }
        catch(InterruptedException e){
            if(DEBUG)System.out.println("interupted");
        }
        if(DEBUG)System.out.println("Stoped");
    }

    public void stop(){
        running = false;
        thread=null;
    }

    public void start(){
        stop();
        if(DEBUG)System.out.println("Try to start");
        if(!running && thread==null){
            running = true;

            thread = new Thread(this);
            thread.start();
        }
    }

    public void finalize(){
        if(DEBUG)System.out.println("stop?");
        if(running){
            if(DEBUG)System.out.println("yes");
            running = false;
            thread=null;
        }
    }

    public static void main(String args[])throws MalformedURLException, IOException {
        RemoteObjectLayer rgd = new RemoteObjectLayer(new URL("http://www.ccg.leeds.ac.uk:8080/servlets/james/ObjectPosition"));
        rgd.start();
        if(DEBUG)System.out.println("done");
        System.in.read();
        rgd.stop();
    }



}
