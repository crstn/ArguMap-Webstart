package uk.ac.leeds.ccg.geotools.projections;

public abstract class Mercator implements Projection
{
    public double[] project(double lon,double lat){
        //do magic stuff
        double p[] = new double[2];
        
        double rx = (Math.PI/180d)*lon;
        double ry = (Math.PI/180d)*lat;
        
     
        p[0] = rx;
        p[1] = Math.log(Math.tan(ry)+(1/Math.cos(ry)));
        
        
        p[0] = p[0] * 180d/Math.PI;
        p[1] = p[1] * 180d/Math.PI;
        //System.out.println(lon+","+lat+"   "+p[0]+","+p[1]);
        return p;
    }
    
    public double[] unproject(double x,double y){
        double p[] = new double[2];
        
        double rx = (Math.PI/180d)*x;
        double ry = (Math.PI/180d)*y;
        
        
        p[0] = rx;
        p[1] = 2*Math.atan(Math.pow(Math.E,ry))-(Math.PI/2d);
        
        p[0] = p[0] * 180d/Math.PI;
        p[1] = p[1] * 180d/Math.PI;
        //System.out.println(lon+","+lat+"   "+p[0]+","+p[1]);
        return p;
        
        
    }
}