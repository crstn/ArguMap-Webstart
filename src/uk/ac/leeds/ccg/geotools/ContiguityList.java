package uk.ac.leeds.ccg.geotools;

public class ContiguityList
{
    int id;
    int[] list;
    public ContiguityList(int id,int[] list){
        this.id = id;
        this.list = list;
    }
    
    public int[] getList(){
        return list;
    }
    
    public int getID(){
        return id;
    }
        
}