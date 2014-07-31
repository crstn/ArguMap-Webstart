package uk.ac.leeds.ccg.geotools;

public class IDChangedEvent extends java.util.EventObject
{
    private int id;


    public IDChangedEvent(java.awt.Component source, int id){
        super(source);

        this.id = id;

    }

    public int getID(){
        return id;
    }

}