package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.awt.Frame;

public class MonoKey extends uk.ac.leeds.ccg.geotools.Key
{
    KeyBox box;
    
    MonoKey(Shader s, String name){
        super(s);
        //System.out.println("MonoKey Constructed");
        switch(s.getKeyStyle()){
            case Shader.BOX:
                        box = new KeyBox(s,100,name);
                        break;
            case Shader.POINT:
                        box = new PointKeyBox(s,100,name);
                        break;
            case Shader.LINE:
                        box = new LineKeyBox(s,100,name);
        }

        box.setShader(s);
        add(box);
        //setShader(s);
    }
    
    MonoKey(Shader s){
        this(s,"unnamed");
    }
        
    
    
    
    public void updateLabels(){
        
    }
    
    public static void main(String args[]) throws Exception{
        Frame f = new Frame("Test");
        Shader s = new MonoShader(Color.green);
        //f.add(new Button("hello"));
        f.add(s.getKey());
        f.pack();
        f.show();
    }
    
    

}