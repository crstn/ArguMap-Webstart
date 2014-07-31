/*
 * CartogramLayer.java
 *
 * Created on 22 May 2001, 22:24
 */

package uk.ac.leeds.ccg.geotools;

import java.util.Hashtable;
import java.util.Vector;


/**
 *
 * @author  James Macgill
 * @version
 */
public class CartogramLayer extends uk.ac.leeds.ccg.geotools.CircleLayer {
    private final static boolean DEBUG=false;
		final static String DBC ="CarL->";
    int bodies,maxN;
    final double friction;
    final double ratio;
    
    int body,number, end_pointer, distance;
    int x[],y[];
    double radius[];
    int list[];
    Leaf tree[];
    Hashtable ht = new Hashtable();
    GeoCircle[] circles;
    //  int itter;
    
    GeoData pop;
    int itters, done, bodys, itter;
    int nb, other, boundty;
    int people[], nbours[], nbour[][];
    double xvector[], yvector[], border[][];
    double widest, displacement, closest, perimeter[];//= new double[bodies];
    double xrepel, yrepel, xattract,yattract,scale;
    double dist,xd,yd,overlap,tRadius,tDist;
    double atrdst, repdst, xtotal, ytotal;
    
    
    
    /** Creates new CartogramLayer */
    public CartogramLayer(PolygonLayer source,GeoData population) {
        if(DEBUG)System.out.println(DBC+"setup cartogram" );
        Vector shapes =  source.getGeoShapes();
        bodies = shapes.size();
        ContiguityMatrix matrix = source.getContiguityMatrix();
        maxN = matrix.getMaxN()+1;
        bodies = source.countFeatures()+1;
        friction = 0.25;
        ratio = 0.4;
        x = new int[bodies];
        y = new int[bodies];
        radius = new double[bodies];
        list = new int[bodies];
        tree = new Leaf[bodies];
        
        people = new int[bodies]; nbours= new int[bodies]; nbour= new int[bodies][maxN];
        xvector= new double[bodies]; yvector= new double[bodies]; border= new double[bodies][maxN];
        perimeter= new double[bodies];
        
        
        if(DEBUG)System.out.println(DBC+"setup tree");
        for(int i=1;i<bodies;i++){
            tree[i] = new Leaf();
        }
        pop = population;
        
        itters = 1500;
        done = 0;
        
        tDist = 0;
        tRadius = 0;
        Vector polys = source.getGeoShapes();
        GeoPolygon gp ;
        if(DEBUG)System.out.println(DBC+"data "+pop.getSize());
        for(body = 1;body <bodies; body++){
            gp = (GeoPolygon)(polys.elementAt(body-1));
            int id = gp.getID();
            if(DEBUG)System.out.println(DBC+"added "+id);
            ht.put(new Integer(id),new Integer(body));
        }
        double size;
        size = (source.getBounds().getWidth())/Math.sqrt(bodies);
        size=size/(pop.getMax());
        if(DEBUG)System.out.println(DBC+"size "+size);
        // CircleLayer cl = new CircleLayer();
        circles = new GeoCircle[bodies];
        for(body = 1;body <bodies; body++){
            gp = (GeoPolygon)(polys.elementAt(body-1));
            int id = gp.getID();
            if(DEBUG)System.out.println(DBC+"id "+id+" "+(body-1));
            people[body] = (int)pop.getValue(id);
            GeoPoint cent = gp.getCentroidLocation();
            x[body] = (int)cent.x;
            y[body] = (int)cent.y;
            if(DEBUG)System.out.println(DBC+cent);
            radius[body] = (people[body]+1.0)*size;
            circles[body] = new GeoCircle(id,x[body],y[body],radius[body]);
            addGeoCircle(circles[body]);
            Vector neighs = gp.getContiguityList(polys);
            nbours[body] = neighs.size();
            if(DEBUG)System.out.println(DBC+nbours[body]);
            perimeter[body]=0;
            GeoPolygon temp;
            for(nb =1; nb <nbours[body]; nb++){
                temp= (GeoPolygon)(neighs.elementAt(nb-1));
                if(DEBUG)System.out.println(DBC+"temp "+temp.getID());
                nbour[body][nb] = ((Integer)ht.get(new Integer(temp.getID()))).intValue();
                border[body][nb] = temp.getContiguousLength(gp);
                perimeter[body] += border[body][nb];
                if(nbour[body][nb] >0){
                    if(nbour[body][nb] < body){
                        xd = (float)(x[body] - x[nbour[body][nb]]);
                        yd = (float)(y[body] - y[nbour[body][nb]]);
                        tDist += Math.sqrt(xd*xd+yd*yd);
                        tRadius += Math.sqrt(people[body]/Math.PI)+Math.sqrt(people[nbour[body][nb]]/Math.PI);
                    }
                }
            }
        }
        
        scale = tDist / tRadius;
        widest = 0.0;
        
        for(body = 1; body < bodies;body++){
            if(radius[body]>widest) widest = radius[body];
            xvector[body] = 0;
            yvector[body] = 0;
        }
        if(DEBUG)System.out.println(DBC+scale+" widest is "+widest);
        
        
        
    }
    
    public void step(){
        for(body = 1;body < bodies; body++){
            tree[body].id = 0;
        }
        end_pointer = 1;
        for (body = 1;body < bodies; body++){
            addPoint(1,1);
        }
        
        displacement = 0.0;
        for (body = 1;body < bodies; body++){
            //get <number> of neighboues within <distance> inti <list[]>
            number = 0;
            distance = (int)(widest + radius[body]);
            getPoint(1,1);
            
            if(body == 1){
               // System.out.println("body 1 number "+number+" distance "+distance+" radius "+radius[body]);
            }
            
            // initalise a few vectors
            xrepel = yrepel = 0.0;
            xattract = yattract = 0.0;
            closest = widest;
            
            //work out repelling force of overlapping neighbours
            // System.out.println("Number");
            if(number > 0){
                for(nb = 1; nb <= number; nb++){
                    other = list[nb];
                    if (other != body){
                        xd = x[other]-x[body];
                        yd = y[other]-y[body];
                        dist = Math.sqrt(xd*xd+yd*yd);
                     //   System.out.println("Dist "+dist);
                        closest = Math.min(closest,dist);
                        overlap = radius[body] +radius[other] - dist;
                        if(overlap > 0 && dist > 1){
                            xrepel = xrepel - overlap*(x[other]-x[body])/dist;
                            yrepel = yrepel - overlap*(y[other]-y[body])/dist;
                        }
                    }
                }
            }
            
            // work out forces of attraction between neighbours
            for (nb = 1; nb <= nbours[body] ; nb++){
                other = nbour[body][nb];
                if(other != 0)//opt this
                {
                    xd = (x[body]-x[other]);
                    yd = (y[body]-y[other]);
                    dist = Math.sqrt(xd*xd+yd*yd);
                 //   dist = Math.max(dist,0.0001);
                    overlap = dist -radius[body] - radius[other];
                    if(overlap>0 && perimeter[body]>0 ){
                       
                        overlap = overlap * border[body][nb]/perimeter[body];
             //            System.out.println("Overlap "+overlap+" border "+border[body][nb]+ " perimiter "+perimeter[body] );
                        xattract = xattract+overlap*(x[other]-x[body])/dist;
                        yattract = yattract + overlap*(y[other]-y[body])/dist;
                    }
                }
            }
            
            //work out commbined effect of attraction and repulsion
            atrdst = Math.sqrt(xattract * xattract + yattract * yattract);
            repdst = Math.sqrt(xrepel * xrepel+ yrepel * yrepel);
            //System.out.println("repdst"+ repdst+" closest "+closest);
            if (repdst > closest){
                xrepel = closest * xrepel / (repdst +1);
                yrepel = closest * yrepel / (repdst +1);
                repdst = closest;
            }
          //  System.out.println("xattract "+xattract+" atrdst+1"+(atrdst+1));
            if(repdst > 0){
                xtotal = (1-ratio) * xrepel +ratio*(repdst*xattract/(atrdst+1));
                ytotal = (1-ratio) * yrepel +ratio*(repdst*yattract/(atrdst+1));
            }
            else{
                
                if(atrdst > closest){
                    xattract = closest *xattract/(atrdst+1);
                    yattract = closest *yattract/(atrdst+1);
                }
                xtotal = xattract;
                ytotal = yattract;
            }
            xvector[body] = friction * (xvector[body]+xtotal);
            yvector[body] = friction * (yvector[body]+ytotal);
            //System.out.println("xtotal = "+xtotal+" y "+ytotal+" preDis "+displacement);
            displacement += Math.sqrt(xtotal * xtotal +ytotal * ytotal);
            //System.out.println("post "+displacement);
        }
        
        //update positions
        for(body = 1;body < bodies;body++){
            x[body] +=xvector[body] +0.5;
            y[body] +=yvector[body] +0.5;
        }
        
        done++;
        itter++;
        //System.out.println("Dis "+displacement+" "+bodies);
        displacement = displacement / bodies;
        if(done%10==1){
            for(int i=1;i<bodies;i++){
                circles[i].setCentre(x[i],y[i]);
            }

						bbox = new GeoRectangle();
						for(int i=1;i<circles.length;i++){
							bbox.add(circles[i].getBounds());
						}
            notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
            if(DEBUG)System.out.println(DBC+"displacement is now "+displacement+" after "+done+ "itterations");
        }
        
        
        
    }
    Thread thread;
    
    public void go(){
        Runnable runner = new Runnable(){
            public void run(){
                while(itter<itters){
                    step();
                    Thread.yield();
                }
                
            }
            
        };
        thread = new Thread(runner,"CartogramThread");
        //try{
        thread.start();
        //}
        //catch
    }
    
    public void halt(){
        if(thread!=null){
            thread.stop();
        }
    }
    
    public void morphPopulation(GeoData newpop){
        
    }
    
    
    
    
    
    /**
     * Adds the specified GeoPolygon to the GeoMap
     * @param polygon The GeoCircle to be added
     */
    public void addGeoCircle(GeoCircle circle) {
        super.addGeoCircle(circle);
    }
    
    
    public void addPoint(int pointer,int axis){
        if(tree[pointer].id == 0) {
            tree[pointer].id = body;
            tree[pointer].left = 0;
            tree[pointer].right = 0;
            tree[pointer].xpos = x[body];
            tree[pointer].ypos = y[body];
        }
        else{
            if(axis == 1){
                if (x[body] >= tree[pointer].xpos){
                    if(tree[pointer].left == 0){
                        end_pointer +=1;
                        tree[pointer].left = end_pointer;
                    }
                    addPoint(tree[pointer].left,3-axis);
                }
                else {
                    if (tree[pointer].right == 0){
                        end_pointer+=1;
                        tree[pointer].right = end_pointer;
                    }
                    addPoint(tree[pointer].right,3-axis);
                }
            }
            else{
                if(y[body] >= tree[pointer].ypos){
                    if(tree[pointer].left == 0){
                        end_pointer +=1;
                        tree[pointer].left = end_pointer;
                    }
                    addPoint(tree[pointer].left,3-axis);
                }
                else{
                    if(tree[pointer].right == 0){
                        end_pointer+=1;
                        tree[pointer].right = end_pointer;
                    }
                    addPoint(tree[pointer].right, 3-axis);
                }
            }
        }
    }
    
    public void getPoint(int pointer, int axis){
        if(pointer > 0){
            if(tree[pointer].id > 0){
                if (axis == 1){
                    if(x[body]-distance < tree[pointer].xpos){
                        getPoint(tree[pointer].right,3-axis);
                    }
                    if(x[body]+distance >= tree[pointer].xpos){
                        getPoint(tree[pointer].left,3-axis);
                    }
                }
                if(axis == 2){
                    if(y[body]-distance < tree[pointer].ypos){
                        getPoint(tree[pointer].right,3-axis);
                    }
                    if(y[body]+distance >= tree[pointer].ypos){
                        getPoint(tree[pointer].left,3-axis);
                    }
                }
                if ((x[body]-distance < tree[pointer].xpos) &&
                (x[body]+distance>= tree[pointer].xpos)){
                    if((y[body]-distance < tree[pointer].ypos) &&
                    (y[body]+distance>= tree[pointer].ypos)){
                        number ++;
                        list[number] = tree[pointer].id;
                    }
                }
            }
        }
        
        
    }
    
    
    
    class Leaf {
        int id;
        int xpos;
        int ypos;
        int left;
        int right;
    }
    
}
