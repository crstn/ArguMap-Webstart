package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.classification.Bin;

public class FirstPastPostShader extends uk.ac.leeds.ccg.geotools.DiscreteShader
{
    Vector groups = new Vector();
    boolean markAsMinority = false;
  //  protected Vector keys = new Vector();
     
    //the value passed in should be an id 
    public Color getColor(double id){
        int groupID =-1;
        if(id <0){
            return ((Group)groups.elementAt((int)Math.abs(id)-1)).color;
        }
       // System.out.println("Finding winner for "+id);
        double max = Double.MIN_VALUE;
        for(int i=0;i<groups.size();i++){
            double value = ((Group)groups.elementAt(i)).data.getValue((int)id);
         //   System.out.println("Value for group "+i+" "+value);
            if(value > max){
                max = value;
                groupID = i;
            }
        }
        
        
        if(groupID>=0){
            Color c = ((Group)groups.elementAt(groupID)).color;
            if(markAsMinority && isMinority(groupID,(int)id)){
                c = c.darker();
            }
            return c;
        }
        return this.missingColor;
        
    }
    
    public void setMarkMinorites(boolean flag){
        if(flag!=markAsMinority){
            markAsMinority = flag;
            this.notifyShaderChangedListeners();
        }
    }
    
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){
        return "First Past the Post Shader";
    }
    
    public boolean isMinority(int group,int id){
        double total = 0;
        for(int i=0;i<groups.size();i++){
             if(i!=group){
                total+= ((Group)groups.elementAt(i)).data.getValue(id);
             }
        }
        if(((Group)groups.elementAt(group)).data.getValue(id)<total){

            return true;
        }
        return false;
    }
        
    
    /*public Key getKey(){
            if(key==null){
                key = new LUTKey(this);
                key.addMouseListener(new Clicked());
            }
            
            return key;
     }*/
     
    
    public void addGroup(GeoData data,Color color){
        groups.addElement(new Group(data,color));
        int index = keys.size();
        index = -index;
        index--;
        RangeItem k = new RangeItem(new Bin(index,index+0.0000001),color,data.getName());
		keys.addElement(k);
        
    }

    class Group{
        public Group(GeoData d,Color c){
            data = d;
            color = c;
        }
        public GeoData data;
        public Color color;
    }

}