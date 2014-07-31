package uk.ac.leeds.ccg.widgets;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.util.Hashtable;
import java.util.Vector;

import uk.ac.leeds.ccg.geotools.CircleLayer;
import uk.ac.leeds.ccg.geotools.ContiguityMatrix;
import uk.ac.leeds.ccg.geotools.GeoCircle;
import uk.ac.leeds.ccg.geotools.GeoData;
import uk.ac.leeds.ccg.geotools.GeoFormLabel;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoPolygon;
import uk.ac.leeds.ccg.geotools.HSVShader;
import uk.ac.leeds.ccg.geotools.HighlightManager;
import uk.ac.leeds.ccg.geotools.Key;
import uk.ac.leeds.ccg.geotools.PolygonLayer;
import uk.ac.leeds.ccg.geotools.SelectionManager;
import uk.ac.leeds.ccg.geotools.Shader;
import uk.ac.leeds.ccg.geotools.Theme;
import uk.ac.leeds.ccg.geotools.Viewer;

public class BuildCartogram

{
	private final static boolean DEBUG=false;
	int bodies,maxN;
    final double friction;
    final double ratio;

    int body,number, end_pointer, distance;
    int x[],y[];
    double radius[];
    int list[];
    Leaf tree[];
    Hashtable ht = new Hashtable();

    public BuildCartogram(Theme t,GeoData data) {
		if(DEBUG)System.out.println("---->uk.ac.leeds.ccg.widgets.BuildCartogram constructed. Will identify itself as BC-->");
		PolygonLayer pl = (PolygonLayer)t.getLayer();
        
        Vector shapes =  pl.getGeoShapes();
        bodies = shapes.size();
        ContiguityMatrix matrix = pl.getContiguityMatrix();
        maxN = matrix.getMaxN()+1;
        bodies = pl.countFeatures()+1;
        friction = 0.25;
        ratio = 0.4;
        x = new int[bodies];
        y = new int[bodies];
        radius = new double[bodies];
        list = new int[bodies];
        tree = new Leaf[bodies];
        for(int i=1;i<bodies;i++){
            tree[i] = new Leaf();
        }
        GeoData pop = t.getGeoData();
        //find files and load data.
        Frame f = new Frame();
        //FileDialog fd = new FileDialog(f,"Open data file");
        //fd.setFile("carto.in");
        //fd.show();
        //File inFile = new File(fd.getDirectory(),fd.getFile());
        //BufferedReader reader = new BufferedReader(new FileReader(inFile));
        
        int itters, done, bodys, itter;
        int nb, other, boundty;
        int people[] = new int[bodies], nbours[]= new int[bodies], nbour[][]= new int[bodies][maxN];
        double xvector[]= new double[bodies], yvector[]= new double[bodies], border[][]= new double[bodies][maxN];
        double widest, displacement, closest, perimeter[]= new double[bodies];
        double xrepel, yrepel, xattract,yattract,scale;
        double dist,xd,yd,overlap,tRadius,tDist;
        double atrdst, repdst, xtotal, ytotal;
        
        itters = 500;
        done = 0;
        
        tDist = 0;
        tRadius = 0;
				Vector polys = pl.getShapes();
				GeoPolygon gp ;
				if(DEBUG)System.out.println("BC-->data "+pop.getSize());
        for(body = 1;body <bodies; body++){
					gp = (GeoPolygon)(polys.elementAt(body-1));
					int id = gp.getID();
					ht.put(new Integer(id),new Integer(body));
				}
				double size;
				size = (pl.getBounds().getWidth())/Math.sqrt(bodies);
				size=size/(pop.getMax());
				if(DEBUG)System.out.println("BC-->size "+size);
				CircleLayer cl = new CircleLayer();
				GeoCircle circles[] = new GeoCircle[bodies];
        for(body = 1;body <bodies; body++){
						gp = (GeoPolygon)(polys.elementAt(body-1));
						int id = gp.getID();
						//System.out.println("id "+id+" "+(body-1));
            people[body] = (int)pop.getValue(id);
            GeoPoint cent = gp.getCentroidLocation();
            x[body] = (int)cent.x;
            y[body] = (int)cent.y;
            if(DEBUG)System.out.println("BC-->"+cent);
            radius[body] = (people[body]+1.0)*size;
            circles[body] = new GeoCircle(id,x[body],y[body],radius[body]);
            cl.addGeoCircle(circles[body]);
            Vector neighs = gp.getContiguityList(polys);
            nbours[body] = neighs.size();
            if(DEBUG)System.out.println("BC-->"+nbours[body]);
            perimeter[body]=0;
						GeoPolygon temp;
            for(nb =1; nb <nbours[body]; nb++){
								temp= (GeoPolygon)(neighs.elementAt(nb-1));
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
        if(DEBUG)System.out.println("BC-->Scaling by "+scale+" widest is "+widest);
        
       // set up the visualization stuff...
        Frame fr = new Frame();
       Viewer rview = new Viewer();
        Viewer lview = new Viewer();
        
				Theme rt = new Theme(pl);
				Theme lt = new Theme(cl);
				rt.getShadeStyle().setIsOutlined(false);
				lt.getShadeStyle().setIsOutlined(false);
				Shader ss = new HSVShader(Color.green,Color.yellow);
				ss.setRange(pop);
				//ss.setMissingValueCode(errors.getMissingValueCode());
				//ss.setMissingValueColor(Color.white);
				Key k = ss.getKey();
				k.setSize(60,200);
				rt.setGeoData(pop);
				lt.setGeoData(pop);
				rt.setShader(ss);
				lt.setShader(ss);


        lview.addTheme(lt);
        rview.addTheme(rt);
				HighlightManager he = new HighlightManager();
				SelectionManager se = new SelectionManager();
				rt.setTipData(pop);
				lt.setTipData(pop);
				rt.setHighlightManager(he);
				rt.setSelectionManager(se);
				lt.setHighlightManager(he);
				lt.setSelectionManager(se);

        //fr.setSize(600,600);
				Panel p = new Panel();
				lview.setSize(200,200);
				rview.setSize(200,200);
        p.add(lview);
				p.add(k);
        p.add(rview);
				fr.setLayout(new BorderLayout());
				Panel p2 = new Panel();
				p2.setLayout(new GridLayout(1,3));
				p2.add(new ToolBar(lview,true));
				GeoFormLabel gf = new GeoFormLabel();
				gf.addHighlightManager(he,pop);
				p2.add(gf);
				p2.add(new ToolBar(rview,true));
				fr.add(p,"Center");
				fr.add(p2,"South");
				fr.pack();
        fr.show();
        lview.setMapExtentFull();
        rview.setMapExtentFull();
        
         
        //start the big loop, creating the tree each iter
        for(itter = 1;itter <= itters; itter++){
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
                
               /* if(body == 1){
                    System.out.println("body 1 number "+number+" distance "+distance+" radius "+radius[body]);
                }*/
                
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
                        overlap = dist -radius[body] - radius[other];
                        if(overlap>0){
                            overlap = overlap * border[body][nb]/perimeter[body];
                            xattract = xattract+overlap*(x[other]-x[body])/dist;
                            yattract = yattract + overlap*(y[other]-y[body])/dist;
                        }
                    }
                }
                
                //work out commbined effect of attraction and repulsion
                atrdst = Math.sqrt(xattract * xattract + yattract * yattract);
                repdst = Math.sqrt(xrepel * xrepel+ yrepel * yrepel);
                if (repdst > closest){
                    xrepel = closest * xrepel / (repdst +1);
                    yrepel = closest * yrepel / (repdst +1);
                    repdst = closest;
                }
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
                displacement += Math.sqrt(xtotal * xtotal +ytotal * ytotal);
            }
                
                //update positions
                for(body = 1;body < bodies;body++){
                    x[body] +=xvector[body] +0.5;
                    y[body] +=yvector[body] +0.5;
                }
                
                done++;
                displacement = displacement / bodies;
                if(done%100==1){
									for(int i=1;i<bodies;i++){
											circles[i].setCentre(x[i],y[i]);
									}
									cl.notifyLayerChangedListeners(1);
                    if(DEBUG)System.out.println("BC-->displacement is now "+displacement+" after "+done+ "itterations");
                }
                 
    
            
        }
               
    }
        
        //lets see what we have got
        
       
            
            
        
    
       
    
    
    public void addPoint(int pointer,int axis){
        if(tree[pointer].id == 0)
        {
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
                else
                {
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
    
    

       
        
   
    
    class Leaf
{
    int id;
    int xpos;
    int ypos;
    int left;
    int right;
}
    
                        
                        
}
