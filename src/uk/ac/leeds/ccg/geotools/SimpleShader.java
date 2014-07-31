package uk.ac.leeds.ccg.geotools;

import java.awt.Button;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import uk.ac.leeds.ccg.widgets.ColorPickLabel;
//shader stratergy

/**
 * An abstract but almost full implementation of the Shader interface.
 * This class is not much use as a shader, it is however very usefull
 * as a class to extend other more complex shaders from as
 * it can take care of the missingColor/Code aspects.
 */
public abstract class SimpleShader extends java.lang.Object implements uk.ac.leeds.ccg.geotools.Shader
{
	private final static boolean DEBUG=false;
	/**
     * The color to use when a missing value code is passed in.
     */
    protected Color missingColor = Color.white;
    
    protected int style = Shader.BOX;
    
    /**
     * The smallest value this shader is expected to work with
     */
     protected double min;

    /**
     * The largest value this shader is expected to work with
     */
     protected double max;
    /**
     * The value that represents a missing value.
     */
    protected double missingCode = -99999;

    /**
     * The Key for this shader
     */
     protected Key key;

     /**
      * All listeners connected to this shader
      */
      protected Vector listeners = new Vector();


     protected GeoData rangeData = null;

     Configure conf = new Configure();

    public SimpleShader(){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.geotools.SimpleShader created. Will identify itself as SiSh>");
		//key = new RampKey(this);
        //key.setShader(this);
    }

    /**
     * Set the color to use when the shader is passed
     * a value equaling the missingValue code to the getColor method.
     * <p> Quite often maps have features for which the value is missing,
     * in such cases the shader may want to provide a special color to mark
     * these features clearly.
     * @see getColor
     * @see setMissingValueCode
     * @param color the color used to represent missing values.
     */
    public void setMissingValueColor(Color color)
    {
        missingColor=color;
         getKey().updateKey();
    }
    
    public void setMissingValueCode(double code)
    {   
        missingCode=code;
        
                    if(rangeData != null){setRange(rangeData);}
                    if(DEBUG)System.out.println("SiSh>Missing Code Changed");
         getKey().updateKey();
    }
    
    public int getRGB(double value)
    {
        return (getColor(value).getRGB());
    }
    
    public Color getMissingValueColor()
    {
        return missingColor;
    }
    
    public double getMissingValueCode()
    {
        return missingCode;
    }
    
    public Color getColor(double value){
        return missingColor;
    }
    
     /**
     * Sets the range for this shader by checking the range of
     * the given data set.
     * this method also makes sure the GeoData's missing value code
     * and the shaders missing value code match.
     * @param d The GeoData to pull the range from
     */
    public void setRange(GeoData d){
       
        double temp = d.getMissingValueCode();
        if(temp!=missingCode){
            d.setMissingValueCode(missingCode);
            setRange(d.getMin(),d.getMax());
            d.setMissingValueCode(temp);
        }
        else{
            setRange(d.getMin(),d.getMax());
        }
        this.notifyShaderChangedListeners();
        getKey().updateKey();
         rangeData = d;
    }

    public double[] getRange(){
        double d[] = new double[2];
        d[0]=min;;
        d[1]=max;
        return d;
    }

    
    public void setRange(double min,double max){
        this.min = min;
        this.max = max;
        //System.out.println("SiSh>Range Changed! "+getRange()[1]);
        rangeData = null;
        if(key!=null)key.updateKey();
    }
    
    public synchronized void addShaderChangedListener(ShaderChangedListener scl){
        listeners.addElement(scl);
    }
    
    public synchronized void removeShaderChangedListener(ShaderChangedListener scl){
        listeners.removeElement(scl);
    }
    
    /**
     * Notify all shader change listeners of a change in this shader.
     */
    protected void notifyShaderChangedListeners(){
        Vector l;
	    ShaderChangedEvent sce = new ShaderChangedEvent(this);
	    synchronized(this) {l = (Vector)listeners.clone(); }
	    
	    for (int i = 0; i < l.size();i++) {
	        ((ShaderChangedListener)l.elementAt(i)).shaderChanged(sce);
	    }
            if(key!=null) {System.out.println("Calling validade on key");key.invalidate();key.validate();}
	    //oh and update the configurator
	    conf.update();
	}
    
    
       
    
    public Key getKey(){
        if(key==null){key = new RampKey(this);key.addMouseListener(new Clicked());}
        
        return key;
    }
    
    public void setKeyStyle(int styleCode) {
        style = styleCode;
        getKey().updateKey();
    }    
  
    public int getKeyStyle() {
        return style;
    }    
    
    class Clicked extends java.awt.event.MouseAdapter
    {
        public void mouseClicked(java.awt.event.MouseEvent e){
            if(DEBUG)System.out.println("SiSh>Someone clicked on the key "+e.getClickCount());
            if(e.getClickCount()==2){
                conf.setLocation(e.getX(),e.getY());
                conf.update();
                conf.pack();
                conf.show();
                notifyShaderChangedListeners();
            }
        }
    }
    
    class Configure extends ShaderConfigurator
    {
        ColorPickLabel cpl;
        TextField code;
        Button ok;
     
        public Configure()
        {
            super(new Frame(),true);
        
            
            addItems();  
            ok = new Button("OK");
            add(ok);
            ok.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    
                    
                    setVisible(false);
                    actionChanges();
                    if(DEBUG)System.out.println("SiSh>missing color changed");
                }
            });
            
        }
        
        public void update(){
            cpl.setPickColor(missingColor);
            code.setText(""+missingCode);
        }
        
        public void actionChanges(){
            missingColor = conf.cpl.getPickColor();
            try{
                double mc = Double.valueOf(conf.code.getText()).doubleValue();
                if(mc != missingCode){
                    setMissingValueCode(mc);
                    
                }
            }
            catch(NumberFormatException nfe){System.err.println("SiSh>Invalid value for new missing code, ignored "+nfe);}
        }
            
        
        public void addItems(){
            setLayout(new GridLayout(0,2));
            cpl = new ColorPickLabel(missingColor);
            add(new Label("Missing Color"));
            add(cpl);
            code = new TextField(""+missingCode);
            add(new Label("Missing Code"));
            add(code);
           
          
        }
            
    }
    
    
}
