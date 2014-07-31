package uk.ac.leeds.ccg.geotools;
import java.awt.Color;
import java.awt.GridLayout;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.net.URL;
import java.util.Enumeration;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.classification.Bin;

/**
 * Unlike rampshaders, descrete shaders have a set number of catagories
 * This class is the base from which percentile, quantile and look-up
 * are extended.
 * Their biggest distiction will come from the way they produce keys.
 * This shader is constructed using a set of ranges, colours and names
 * to be used as the classifications.  These can either be supplied in a
 * key file referenced via a URL or as a set of three vectors.
 */
public class DiscreteShader extends uk.ac.leeds.ccg.geotools.SimpleShader {
    protected Vector keys = new Vector();
    public DiscreteShader(){
        //only for use by decendents
    }
    /**
     * Constructs a DiscreteShader which allows almost total control over the classifications used when
     * shading.<p>
     * The key file format is of the format...<P>
     *
     * integer,'#XXXXXX','discription'<P>
     * for each line
     * where the quote marks are necessary, the integer represents the value
     * looked checked against the value in getColor, the discription
     * will be used to label the key, and #XXXXXX is a hexidecimal
     * color code.<P>
     * e.g.<p>
     * 1,'#ff3333',A-Roads<br>
     * 2,'#993333',B-Roads<br>
     * 3,'#ffff33',C-Roads<br>
     * 4,'#cccccc',Unclassified<p>
     * If a - is used in the value column then a range is assumed<p>
     * e.g.<p>
     * 0-49,'#0000ff',Cold<br>
     * 50-100,'#ff0000',Hot<br>
     *
     * TODO Bug?  How are negative values handled in ranges?
     * @author James Macgill
     * @param keyFile a URL reference to a key file.
     */
    
    public DiscreteShader(URL keyFile) throws IOException{
        Reader reader = new InputStreamReader(keyFile.openStream());
        StreamTokenizer st = new StreamTokenizer(reader);
        st.whitespaceChars(',',',');
        st.wordChars('-','-');
        st.wordChars('*','*');
        st.wordChars(' ',' ');
        st.parseNumbers();
        int token;
        int code;
        Color color;
        String name;
        Bin value;
        
        token = st.nextToken();
        while(token!=st.TT_EOF){
            if(st.sval!=null) {
                //System.out.println("Using Range key");
                String range = st.sval;
                int split = range.indexOf('-');
                String low = range.substring(0, split);
                String high = range.substring(split+1,range.length());
                //System.out.println("range "+range);
                int a,b;
                if(low.equals("*")){
                    a = Integer.MIN_VALUE;
                }
                else{
                    a = (new Integer(low)).intValue();
                }
                if(high.equals("*")){
                    b = Integer.MAX_VALUE;
                }
                else{
                    b = (new Integer(high)).intValue();
                }
                value = new Bin(a,b);
            }
            else {
                code=(int)st.nval;
                //value = new Bin(code,code+Double.MIN_VALUE);
                value = new Bin(code,code+0.5);
            }
            st.nextToken();
            //System.out.println("ds "+st.sval);
            color = Color.decode(st.sval);
            st.nextToken();
            name = st.sval;
            token = st.nextToken();
            RangeItem k = new RangeItem(value,color,name);
            keys.addElement(k);
            //  System.out.println(k);
        }
    }
    
    /**
     * An alternative contsrutor for building a DiscreteShader that does not rely on
     * the use of a key file.  Instead the values, colours and descriptions are supplied in the
     * form of three vectors.
     *
     * @author Ian Turton
     * @param values A vector of Integer objects which represent the value for each classification
     * @param colours A vector of Color objects which represent the colour for each classification
     * @param descriptions A vector of Strings with witch to label each of the classifications.
     */
    public DiscreteShader(Vector values,Vector colours,Vector descriptions){
        Bin value;
        int code;
        for(int i=0;i<values.size();i++){
            code=((Integer)values.elementAt(i)).intValue();
            value = new Bin(code,code+Double.MIN_VALUE);
            RangeItem k = new RangeItem(value,
            (Color)colours.elementAt(i),(String)descriptions.elementAt(i));
            keys.addElement(k);
        }
    }
    public Key getKey(){
        if(key==null){
            key = new LUTKey(this);
            key.addMouseListener(new Clicked());
        }
        
        return key;
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "Discrete Shader";
    }
    
    public Color getColor(double value) {
        if(value == missingCode){return missingColor;}
        for(Enumeration e = keys.elements();e.hasMoreElements();){
            RangeItem key = (RangeItem)e.nextElement();
            if(key.contains(value)){return key.color;}
        }
        return missingColor;
        
    }
    
    public class RangeItem{
        Bin code;
        Color color;
        String name;
        
        public RangeItem(Bin bin,Color color,String name){
            this.code = bin;
            this.color = color;
            this.name = name;
        }
        
        public String toString(){
            return ("Code: "+code+" Color: "+color+" Name: "+name);
        }
        public boolean contains(double v){
            return code.contains(v);
        }
        
        
        
    }
    
    
    
    public class LUTKey extends Key{
        
        LUTKey(Shader s){
            super(s);
        //    System.out.println("Key Request");
            setLayout(new GridLayout(keys.size(),1));
            for(Enumeration e = keys.elements();e.hasMoreElements();){
                RangeItem item = (RangeItem)e.nextElement();
                /*System.out.println("Building keybox ");
                System.out.println("Item "+item);
                System.out.println("Code "+item.code);
                System.out.println(item.code.getLowerInclusion());
                System.out.println(item.name);*/
                KeyBox box=null;
               // System.out.println("Key type is "+s.getKeyStyle());
                switch(s.getKeyStyle()){
                    case Shader.BOX:
                        box = new KeyBox(s,item.code.getLowerInclusion(),item.name);
                        System.out.println("Box Key");
                        break;
                    case Shader.POINT:
                        box = new PointKeyBox(s,item.code.getLowerInclusion(),item.name);
                        System.out.println("Point Key");
                        break;
                    case Shader.LINE:
                        box = new LineKeyBox(s,item.code.getLowerInclusion(),item.name);
                        System.out.println("Line Key");
                        break;
                    default:
                        box = new KeyBox(s,item.code.getLowerInclusion(),item.name);
                }
                
                
                
                //KeyBox box = new KeyBox(s,item.code.getLowerInclusion(),item.name);
                add(box);
            }
            
        }
        /**
         * updates the key to reflect any changes in the shader which it
         * is attached to.
         **/
        public synchronized void updateKey(){
            removeAll();
            setLayout(new GridLayout(keys.size(),1));
            for(Enumeration e = keys.elements();e.hasMoreElements();){
                RangeItem item = (RangeItem)e.nextElement();
                //System.out.println("Code "+item.code.min+" "+item.name);
                KeyBox box;
                switch(shader.getKeyStyle()){
                    case Shader.BOX:
                        box = new KeyBox(shader,item.code.getLowerInclusion(),item.name);
                        System.out.println("Box Key");
                        break;
                    case Shader.POINT:
                        box = new PointKeyBox(shader,item.code.getLowerInclusion(),item.name);
                        System.out.println("Point Key");
                        break;
                    case Shader.LINE:
                        box = new LineKeyBox(shader,item.code.getLowerInclusion(),item.name);
                        System.out.println("Line Key");
                        break;
                    default:
                        box = new KeyBox(shader,item.code.getLowerInclusion(),item.name);
                }
                //KeyBox box = new KeyBox(shader,item.code.getLowerInclusion(),item.name);
                add(box);
            }
            updateLabels();
            this.resize(this.getPreferredSize());
            invalidate();
            validate();
            repaint();
        }
        
    }
    
    
    
}
