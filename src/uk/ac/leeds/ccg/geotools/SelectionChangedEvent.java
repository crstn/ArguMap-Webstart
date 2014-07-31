package uk.ac.leeds.ccg.geotools;

public class SelectionChangedEvent extends java.util.EventObject
{
    private int selected[];
    private int count;
    
    public SelectionChangedEvent(java.awt.Component source, int s[]){
        super(source);
        count = s.length;
        selected = new int[count];
        System.arraycopy(s,0,selected,0,count);//copying is safer
    }
    
    public int[] getSelection(){
        return selected;
    }
    
    public final boolean isSelected(int id){
        for(int i=0;i<count;i++){
            if(selected[i] == id){return true;}
        }
        return false;
    }
    
    public int getCount(){
        return count;
    }
    
    private String header = "~Zone DEsign System (ZDES) - Version 3.1 \n"+
                            "Designed by S.Openshaw and L.Rao, March 1994 \n"+
                            "Redesigned by S. Alvanides, March 1997 ";
                            
    private String bits = "                        ~zd3set.zdf	:  Zoning option file for...z_cgrid	:  Output zoning system from...cgrid	:  Input zoning system.Date of run	: 14 Oct 99, 2:01 PM~";
        
}
