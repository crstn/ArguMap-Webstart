package uk.ac.leeds.ccg.geotools;

import java.awt.Checkbox;
import java.awt.Color;
import java.awt.Label;

import uk.ac.leeds.ccg.widgets.ColorPickLabel;
public class HSVShader extends RampShader
{
		private final boolean debug=false;
  //  private Color missingColor = Color.white;
    public static final int HUE=0,SAT=1,VALUE=2;
    //private double missingCode = -9999;
    protected float[] start;//={0.0f,1.0f,0.0f};
    protected float[] end;//={0.5f,1.0f,0.0f};
    protected double[] range = {0d,1d};
    //protected Color colors = new Color[2];
    protected float[] now=new float[3];
    protected float[] grad;
    protected float[] calc;
    protected boolean goShort = true;
    
   /* public HSVShader(Color first){
        if(debug)System.out.println("setting up shader");
        start = new float[3];
        end = new float[3];
        Color.RGBtoHSB(first.getRed(),first.getGreen(),first.getBlue(),start);
        //Color.RGBtoHSB(second.getRed(),second.getGreen(),second.getBlue(),end);
        end[0]=start[0];
        end[1]=start[1];
        end[2]=0.0f;
        //setRange(0,1);
        if(debug)System.out.println("From : H:"+start[0]+" S:"+start[1]+" V:"+start[2]);
        if(debug)System.out.println("To : H:"+end[0]+" S:"+end[1]+" V:"+end[2]);
        setRange(0,100);
    }*/
        
    public HSVShader(){
		this(Color.red,Color.blue);	
		}
    
    public HSVShader(Color first,Color second){
        this(first,second,true);
    }
    
    /**
     * Full constructor for a HSVShader<p>
     * this will set up a colour ramp from color one to colour two
     * by interpolating Hue, Satuarion and Brightness seperatly.<br>
     * This genaraly gives a better result than ramping with RGB.<br>
     * As hue can be represented as a circle, there are two ways to go from
     * any one colour to another colour.  For example to ramp from blue to red
     * either involves lots of shades of blue, purlpe and red OR hues of blues, greens 
     * yellows and reds.<br>
     * The shortest parameter (true by default in the other constructor) sets which
     * way around the hue circle the shader should go.  In the above example seting it to
     * true would produce the blue-purple-red option whilst false would produce a blue-cyan-green-yellow-red set.<p>
     * @param lowest sane value for this ramp (inclusive)
     * @param highest sane value for this ramp (exclusive)
     * @param first A Color to start the ramp at
     * @param second A Color to end the ramp at
     * @param shortest A boolean to switch the direction of hue selection, see above description.
     */
    public HSVShader(double low, double high,Color first,Color second, boolean shortest){
        //super(0,1);
        goShort = shortest;
        if(debug)System.out.println("setting up shader");
        start = new float[3];
        end = new float[3];
        setColors(first,second);
        //setRange(0,100);
        setRange(low,high);
        conf = new Configure();
    }
    
    /**
     * Constructor for a HSVShader<p>
     * this will set up a colour ramp from color one to colour two
     * by interpolating Hue, Satuarion and Brightness seperatly.<br>
     * This genaraly gives a better result than ramping with RGB.<br>
     * As hue can be represented as a circle, there are two ways to go from
     * any one colour to another colour.  For example to ramp from blue to red
     * either involves lots of shades of blue, purlpe and red OR hues of blues, greens 
     * yellows and reds.<br>
     * The shortest parameter (true by default in the other constructor) sets which
     * way around the hue circle the shader should go.  In the above example seting it to
     * true would produce the blue-purple-red option whilst false would produce a blue-cyan-green-yellow-red set.<p>
     * @param first A Color to start the ramp at
     * @param second A Color to end the ramp at
     * @param shortest A boolean to switch the direction of hue selection, see above description.
     */
    public HSVShader(Color first,Color second, boolean shortest){
        this(0,100,first,second,shortest);
    }
    
    public void setRange(double low,double high){
        super.setRange(low,high);
        range[0]=low;range[1]=high;
       
        grad = new float[3];
        calc = new float[3];
        
        calcGrads();
        
        getKey().updateKey();
    }
    
    public void calcGrads(){
        for(int i =0;i<3;i++){
            grad[i] = (float)((end[i]-start[i])/(range[1]-range[0]));
            calc[i] = (float)(-range[0]*grad[i]+start[i]);
        }
    }
        
    
    public double[] getRange(){
        double d[] = new double[2];
        d[0]=range[0];
        d[1]=range[1];
        return d;
    }
    
    /*public void setGoShort(boolean flag){
        goShort = flag;
        setRange(low,high);
    }*/
    
    public void testValue(double v){
        
        for(int i=0;i<3;i++){
            now[i] = (float)(grad[i]*v+calc[i]);
        }
        if(debug)System.out.println("now H:"+now[0]+" S:"+now[1]+" V:"+now[2]);
    }
    
    public void setColors(Color first,Color second){
        Color.RGBtoHSB(first.getRed(),first.getGreen(),first.getBlue(),start);
        Color.RGBtoHSB(second.getRed(),second.getGreen(),second.getBlue(),end);
        boolean backShort = (Math.abs((end[0]+1-start[0]))<Math.abs(start[0]-end[0]));
        System.out.println("Start hue "+start[0]+" end "+end[0]);
        if(backShort && goShort){
            //System.out.println("Start hue "+start[0]+" end "+end[0]);
            System.out.println("Going backwards");
            //quicker to go backwards
            end[0] +=1f;
            System.out.println("fixed Start hue "+end[0]+" mod "+end[0]%1f);
        }
        
        //setRange(0,1);
        //if(debug)System.out.println("From : H:"+start[0]+" S:"+start[1]+" V:"+start[2]);
        //if(debug)System.out.println("To : H:"+end[0]+" S:"+end[1]+" V:"+end[2]);
    }
    
    public void setFirstColor(Color first){
        Color.RGBtoHSB(first.getRed(),first.getGreen(),first.getBlue(),start);
    }
    
    public void setSecondColor(Color second){
                Color.RGBtoHSB(second.getRed(),second.getGreen(),second.getBlue(),end);
    }
        
    
    public Color getFirstColor(){
        return Color.getHSBColor(start[0],start[1],start[2]) ;
    }
    
    public Color getSecondColor(){
        return Color.getHSBColor(end[0]%1f,end[1],end[2]) ;
    }
    
    public void setMissingValueColor(Color color)
    {
        missingColor=color;
    }
    
    /*public void setMissingValueCode(double code)
    {   
        missingCode=code;
    }*/
    
    public int getRGB(double value)
    {
        return (getColor(value).getRGB());
    }
    
   /* public Color getMissingValueColor()
    {
        return missingColor;
    }*/
    
   /* public double getMissingValueCode()
    {
        return missingCode;
    }*/
    
    public Color getColor(double value){
        if(value!=missingCode){
            //System.out.println((float)(grad[0]*value+calc[0])+"  "+((float)(grad[0]*value+calc[0]))%1f);
            for(int i=0;i<3;i++){
                now[i] = ((float)(grad[i]*value+calc[i]));
//								if(debug)System.out.println(""+value+" - >("+grad[i]+","+calc[i]+") = "+now[i]);
            }
            now[0] = now[0]%1f;
            if(now[0]<0) now[0]=0;
           // System.out.println(now[0]+","+now[1]+","+now[2]);
            return Color.getHSBColor(now[0],now[1],now[2]);
        }
        return missingColor;
    }
    
    class Configure extends SimpleShader.Configure{
       ColorPickLabel startLabel,endLabel;
       Checkbox direct;
       
       public void update(){
        super.update();
        startLabel.setPickColor(Color.getHSBColor(start[0],start[1],start[2]));
        endLabel.setPickColor(Color.getHSBColor(end[0],end[1],end[2]));
        direct.setState(goShort);
       }
       
       public void actionChanges(){
        super.actionChanges();
        System.out.println("Setting new Colors");
        goShort = direct.getState();
        setColors(startLabel.getPickColor(),endLabel.getPickColor());
        calcGrads();
       }
       
       public void addItems(){
        super.addItems();
        System.out.println("Adding label");
        Label sc = new Label("Start Color");
        add(sc);
        startLabel = new ColorPickLabel(Color.gray);
        add(startLabel);
        Label ec = new Label("End Color");
        add(ec);
        endLabel = new ColorPickLabel(Color.gray);
        add(endLabel);
        direct = new Checkbox("Direct",true);
        add(direct);
       }
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "HSV Shader";
    }
}
