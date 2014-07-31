package uk.ac.leeds.ccg.geotools;



import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;

public class RemoteGeoData extends SimpleGeoData implements uk.ac.leeds.ccg.geotools.GeoData, Runnable
{
	private final static boolean DEBUG=false;
	//Hashtable data;
    URL url;
    Thread thread;
    boolean running = false;
    int interval = 5000;
    
    /**
     * Construts a geodata object that retreves its data from a servlet
     * @param url the URL of the servlet that will provide the data
     */
    public RemoteGeoData(URL url){
     this(url,null);   
    }
    
    /**
     * Constructs a geodata objet that retreves its data from a servlet
     * @param url the location of the servlet to use
     * @param request A request string to pass to the servlet to setup/select the data
     */
    public RemoteGeoData(URL url,String request){
        this.url = url;
    }
    
    private void getData(){
        try{
            URLConnection uc = url.openConnection();
            uc.setDoInput(true);
            ObjectInputStream ois = new ObjectInputStream(uc.getInputStream());
            data = (Hashtable)ois.readObject();
            if(DEBUG)System.out.println(data);
        }
        catch(IOException e){
            System.err.println("Remote GeoData exception @"+url+" "+e);
        }
        catch(ClassNotFoundException e2){
            System.err.println(e2);
        }
    }
        
    public void refresh(){
        getData();
        
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
        if(DEBUG)System.out.println("Stopped");
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
        RemoteGeoData rgd = new RemoteGeoData(new URL("http://www.ccg.leeds.ac.uk:8080/servlets/james/RemoteGeoDataServlet"));
        rgd.start();
        if(DEBUG)System.out.println("done");
        System.in.read();
        rgd.stop();
    }

}
