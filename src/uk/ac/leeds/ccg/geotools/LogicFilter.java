/*
 * BooleanSetFilter.java
 *
 * Created on 28 June 2001, 18:24
 */

package uk.ac.leeds.ccg.geotools;

/**
 *
 * @author  James Macgill
 * @version
 */
public class LogicFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter implements uk.ac.leeds.ccg.geotools.Filter {
    
    GeoData[] data;
    boolean flags[];
    private boolean singleFlagMode;
    boolean orMode = true;
    
    
    /** Creates new BooleanSetFilter */
    public LogicFilter(GeoData[] data, boolean[] flags) {
        this.data = data;
        this.flags = flags;
    }
    
    public void useOrMode(){
        orMode = true;
    }
    public void useAndMode(){
        //System.out.println("orMode set to false");
        orMode = false;
    }
    
    
    public String getFlagName(int index){
        return data[index].getName();
    }
    
    /**
     * Check the given id against the filter,
     * @return boolean returns as true if features with this id should be included in any displays
     */
    public boolean isVisible(int id){
        if(orMode){
            return testOrMode(id);
        }
        else{
            return testAndMode(id);
        }
    }
    public void setSingleFlagMode(boolean flag){
        singleFlagMode = flag;
    }
    public String getHeader(){
        String head = "orMode";
        for(int i=0;i<flags.length;i++){
            head+=","+data[i].getName();
        }
        return head;
    }
    
    public String getAsRow(){
        String row = ""+orMode;
        for(int i=0;i<flags.length;i++){
            row+=","+((flags[i]?1:0));
        }
        return row;
        
    }
    
    public boolean isSet(int id,int flagNo){
        if(data[flagNo].getDataType()==2){
               return data[flagNo].getValue(id)==1;
        }
        else
        {
            return data[flagNo].getText(id).equalsIgnoreCase("T");               
        }
       // return false;
    }
    
    public int[] getCountsForVisible(Filter f){
        int count[] = new int[flags.length];
        java.util.Enumeration enumer = data[0].getIds();
        while(enumer.hasMoreElements()){
            int id = ((Integer)enumer.nextElement()).intValue();
            //System.out.println("Testing "+id);
            if(f.isVisible(id)){
              //  System.out.println("Its visible");    
                for(int i=0;i<flags.length;i++){
                    if(isSet(id,i)){
                //        System.out.println("Flag "+i+" is set");
                        count[i]++;
                    }
                }
            }
                        
        }
        return count;
    }
    
    public int[] getCountsForVisible(){
        int count[] = new int[flags.length];
        java.util.Enumeration enumer = data[0].getIds();
        {
            int id = ((Integer)enumer.nextElement()).intValue();
            if(isVisible(id)){
                for(int i=0;i<flags.length;i++){
                    if(isSet(id,i)){
                        count[i]++;
                    }
                }
            }
                        
        }
        return count;
    }
    
    public boolean testAndMode(int id) {
        int count=0;
        for(int i=0;i<flags.length;i++){
           // System.out.println("Type is "+data[i].getDataType());
            if(!flags[i])continue;
            count++;
            if(data[i].getDataType()==2){
                if(data[i].getValue(id)!=1){
                    return false;
                }
            }
            else{
                if(!data[i].getText(id).equalsIgnoreCase("T")){
                    return false;
                }
            }
        }
        if(count!=0)
            return true;
        return false;
    }
    
    public void setFlagQuiet(int i,boolean flag){
        if(singleFlagMode && flag) clearFlags();
        flags[i] = flag;
    }
    
    
    public void setFlag(int i,boolean flag){
        if(singleFlagMode && flag) clearFlags();
        flags[i] = flag;
        this.notifyFilterChangedListeners(FilterChangedEvent.DATA);
    }
    
    public boolean getFlag(int i){
        return flags[i];
    }
    
    public void clearFlags(){
        for(int i=0;i<flags.length;i++){
            flags[i]=false;
        }
       // this.notifyFilterChangedListeners(FilterChangedEvent.DATA);
    }
    
    public boolean testOrMode(int id) {
        boolean found = false;
        for(int i=0;i<flags.length;i++){
            if(!flags[i])continue;
            if(data[i].getDataType()==2){
                if(data[i].getValue(id)==1){
                    return true;
                }
            }
            else
                if(data[i].getText(id).equalsIgnoreCase("T")){
                    return true;
                }
        }
        return false;
    }
    
    public int getFlagCount(){
        return flags.length;
    }
    
    public Object clone(){
        boolean copy[] = new boolean[flags.length];
        System.arraycopy(flags,0,copy,0,flags.length);
      
        LogicFilter clone = new LogicFilter(data,copy);
        if(!orMode)clone.useAndMode();
        return clone;
        
    }
    
    public String toString(){
        String out = "Filter ";
        for(int i=0;i<flags.length;i++){
            if(flags[i]){
                out+=data[i].getName()+" ";
            }
        }
        return out;
    }
    
}
