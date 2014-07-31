package uk.ac.leeds.ccg.geotools;



import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.event.ItemListener;
import java.util.Vector;

/**
 * This is an experimental wrapper used to hold shaders.<br>
 * By using this it should be possible to switch between different shading stratergies
 * in a simple manner.<br>
 */
 
public class MetaShader implements Shader, ShaderChangedListener, ItemListener
{
    Shader shaderStratergy;
    Key stratergyKey;
    ShaderConfigurator stratergyConf;
    Vector listeners = new Vector();
    ShaderConfigurator conf;   
    KeyWrap key;
    Choice options;
    
    public MetaShader(){
        options = new Choice();
        options.add("HSV Shader");
        options.add("Mono Shader");
        setHSVShader();
    }
    
    public MetaShader(Shader s){
        options = new Choice();
        options.add("HSV Shader");
        options.add("Mono Shader");
        options.addItemListener(this);
        setShaderStratergy(s);
    }
        
    
    public Key getKey(){
        if(key == null){
            key = new KeyWrap();
        }
        return key;
    }
    
    public void setHSVShader(){
      setShaderStratergy(new HSVShader());
    }
    
    public void setMonoShader(){
       setShaderStratergy(new MonoShader()); 
    }
    
    public void setShaderStratergy(Shader s){
        if(shaderStratergy != null){
            shaderStratergy.removeShaderChangedListener(this);
        }
        shaderStratergy = s;
        System.out.println("Adding self as listener "+this);
        shaderStratergy.addShaderChangedListener(this);
        stratergyKey = shaderStratergy.getKey();
      
        //System.out.println("Adding test button");
       
    }
    
    
    public String getName(){
        return shaderStratergy.getName();
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
	    //oh and update the configurator
	   // conf.update();
	}
	
	public void setRange(double min,double max){
	    shaderStratergy.setRange(min,max);
	}
	
	public void setRange(GeoData data){
	    shaderStratergy.setRange(data);
	}
	
	public void setMissingValueColor(Color c){
	    shaderStratergy.setMissingValueColor(c);
	}
	
	public Color getMissingValueColor(){
	    return shaderStratergy.getMissingValueColor();
	}
	
	public void setMissingValueCode(double c){
	    shaderStratergy.setMissingValueCode(c);
	}
	
	public double getMissingValueCode(){
	   return shaderStratergy.getMissingValueCode();
	}
	
	public double[] getRange(){
	  return  shaderStratergy.getRange();
	}
        
	
	public int getRGB(double value){
	    return shaderStratergy.getRGB(value);
	}
	
	public Color getColor(double value){
	    return shaderStratergy.getColor(value);
	}
        
        /**
         * Called by the shader which the meta shader is currently using.<br>
         * The call simply passes the notification up to users of the MetaShader.
         **/
        public void shaderChanged(ShaderChangedEvent sce) {
            this.notifyShaderChangedListeners();
        }        
    
        public void itemStateChanged(java.awt.event.ItemEvent ie) {
            System.out.println(ie);
            key.remove(stratergyKey);
            switch(options.getSelectedIndex()){
                case 1:
                      System.out.println("Chagnge to Mono");
                      setShaderStratergy(new MonoShader());
                      break;
                case 0:
                    System.out.println("Change to HSV");
                    setShaderStratergy(new HSVShader());
                    break;
            }
            key.buildKey();
             this.notifyShaderChangedListeners();
             key.validate();
        }
        
        public void setKeyStyle(int styleCode) {
            shaderStratergy.setKeyStyle(styleCode);
        }
        
        public int getKeyStyle() {
            return shaderStratergy.getKeyStyle();
        }
        
    class KeyWrap extends Key{
        public KeyWrap(){
            this.removeAll();
            setLayout(new BorderLayout());
            this.setSize(200,200);
            this.setBackground(Color.green);
            buildKey();
        }
 
        public void buildKey(){
         
           
            System.out.println("Using "+shaderStratergy.getName());
            this.add(shaderStratergy.getKey(),"Center");
            this.add(options,"South");
            
        }
            
        
        public void setShader(Shader s){
           // System.out.println("Bang!");
          
            shaderStratergy.getKey().setShader(s);
            
           
        }

         
        public void updateKey(){
            System.out.println("Key Updated");
            if(stratergyKey!=null)
            stratergyKey.updateKey();
            
            
        }
          
        public void shaderChanged(ShaderChangedEvent scl){
            if(stratergyKey!=null){
            stratergyKey.updateKey();
            }
        }
       
        
         
        public void updateLabels(){
            if(stratergyKey!=null)
            stratergyKey.updateLabels();
        }
    }

	//{{DECLARE_CONTROLS
	//}}
}