package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Label;

import uk.ac.leeds.ccg.widgets.ColorPickLabel;

/**
 * By far the most straight forward of the shaders, it will shade the whole of a map one color.
 * <br> Regardless of the value passed to this shader it will always return the same preset colour.
 * @author James Macgill.
 */
public class MonoShader extends SimpleShader
{
    Color c;
    String name;

	private final static boolean DEBUG=false;
    /**
     * Constructs a new MonoShader and sets the colour to be returned.
     * @param c The color for this monoshader to use.
     * @param n A String holding the name to use for the monoshades key.
     */
    public MonoShader(Color c,String n){
        this.c = c;
        name = n;
        conf = new Configure();
    }

    /**
     * Constructs a new MonoShader and sets the colour to be returned.
     * @param c The color for this monoshader to use.
     */
    public MonoShader(Color c){
        this(c,"no name");
    }
    /**
     * Constructs a new MonoShader with the color set to Gray.
     */
    public MonoShader(){
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.geotools.MonoShader constructed. Will identify itself as MoSh>");
		c = Color.gray;
        conf = new Configure();
    }
    
    public Key getKey(){
        if (key==null){key = new MonoKey(this,name);key.addMouseListener(new Clicked());}
        //System.out.println("MoSh>Mono Key Requested");
        return key;
    }

       
    /**
     * Gets a color for the given value, in this case it will always be
     * the one specified in the constructor or setMonoColor regardless of 'value'
     * @param value a double to lookup a color for (ignored by monoshaders) 
     * @return color The color used by this monoshader for all values.
     */
    public Color getColor(double value)
    {
				if(value==missingCode) return missingColor;
        return c;
    }
    
    public void setMonoColor(Color col){
        setMonoColor(col, false);
    }

    public void setMonoColor(Color col, boolean silent){
        c=col;
        if (!silent)
            notifyShaderChangedListeners();
    }

		public double[] getRange(){double[] d ={0,1}; return d;}
		
	class Configure extends SimpleShader.Configure{
       ColorPickLabel colorLabel;
       
       public void update(){
        super.update();
        colorLabel.setPickColor(c);
       }
       
       public void actionChanges(){
        super.actionChanges();
        System.out.println("MoSh>Setting new Colors");
        setMonoColor(colorLabel.getPickColor());
       }
       
       public void addItems(){
        super.addItems();
        if(DEBUG)System.out.println("MoSh>Adding label");
        Label sc = new Label("Color");
        add(sc);
        colorLabel = new ColorPickLabel(Color.gray);
        add(colorLabel);
       }
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "Mono Shader";
    }
}
