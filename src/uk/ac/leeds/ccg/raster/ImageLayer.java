// $Header: /export/cvs/incubator52n/ArguMapPrototype/Client/src/uk/ac/leeds/ccg/raster/ImageLayer.java,v 1.1 2005/09/19 10:31:32 CarstenKessler Exp $

package uk.ac.leeds.ccg.raster;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.awt.image.MemoryImageSource;
import java.awt.image.PixelGrabber;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import uk.ac.leeds.ccg.geotools.GeoGraphics;
import uk.ac.leeds.ccg.geotools.GeoPoint;
import uk.ac.leeds.ccg.geotools.GeoRectangle;
import uk.ac.leeds.ccg.geotools.Layer;
import uk.ac.leeds.ccg.geotools.LayerChangedEvent;
import uk.ac.leeds.ccg.geotools.Scaler;
import uk.ac.leeds.ccg.geotools.ShadeStyle;

import com.sun.jimi.core.Jimi;

/**
 * Class <code>ImageLayer</code> will read an image from a URL, assign
 * geographic coordinates to it, and then allow that image to be mapped as a
 * layer within <code>geotools</code>.<br>
 * You can also change the image and exent for the layer.
 * 
 * @see WMSLayer
 * 
 * @author <a href="http://www.geog.leeds.ac.uk/staff/i.turton/i.turton.html">
 *         Ian Turton </a> Centre for Computaional Geography, University of
 *         Leeds, LS2 9JT, 1998. <br>
 *         <a href="mailto:ian@geog.leeds.ac.uk">i.turton@geog.leeds.ac.uk </a>
 * @author <a href="mailto:cameron@shorter.net">Cameron Shorter </a>
 * @author <a href="mailto:kobit@users.sourceforge.net">Artur Hefczyc </a>
 */
public class ImageLayer extends uk.ac.leeds.ccg.geotools.SimpleLayer implements
        Runnable {
    /* Stores all informatation about an image and it's geographic reference */
    private Image image = null;
    private GeoRectangle bbox = null;
    private int myID = 1;
    private final static boolean DEBUG = false;
    private Canvas obs = new Canvas();
    private MediaTracker tracker = new MediaTracker(obs);
    private URL src = null;
    protected java.io.InputStream src_stream = null;
    private Thread thread;
    private MemoryImageSource source;
    private int[] imdata;
    private int imwidth;

    public ImageLayer(URL source, GeoRectangle bounds) {
        if (DEBUG)
            System.out.println("IL-(" + this + ") constructor - source bounds");
        if (DEBUG)
            System.out
                    .println("---->uk.ac.leeds.ccg.raster.ImageLayer constructed. Will identify itself as IL-->");
        changeImage(source, bounds);
    }

    public ImageLayer(java.io.InputStream in, GeoRectangle bounds) {
        changeImage(in, bounds);
    }

    public ImageLayer(Image image, GeoRectangle bounds) {
        if (DEBUG)
            System.out.println("IL-(" + this + ") constructor - Image bounds");
        bbox = bounds;
        this.image = image;
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /** Change the image and extent for the layer. */
    public void changeImage(URL source, GeoRectangle bounds) {
        notifyLayerChangedListeners(222);
        if (DEBUG)
            System.out.println("IL-(" + this + ") changeImage(source,bounds)");
        this.src = source;
        this.src_stream = null;
        if (DEBUG)
            System.out.println("ImageLayer.changeImage= " + src);
        changeImage(bounds);
    }

    public void changeImage(java.io.InputStream in, GeoRectangle bounds) {
        notifyLayerChangedListeners(222);
        this.src_stream = in;
        this.src = null;
        changeImage(bounds);
    }

    public void changeImage(GeoRectangle bounds) {
        notifyLayerChangedListeners(222);
        if (DEBUG)
            System.out.println("IL-(" + this + ") changeImage(Bounds)");
        this.bbox = bounds;
        setStatus(Layer.LOADING);
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    /** Change the image and but not the extent. */
    public void changeImage(URL source) {
        notifyLayerChangedListeners(222);
        if (DEBUG)
            System.out.println("IL-(" + this + ") changeImage(source)");
        changeImage(source, this.bbox);
    }

    /** Change the extent of the image */
    public void changeExtent(GeoRectangle bounds) {
        notifyLayerChangedListeners(222);
        this.bbox = bounds;
        this.notifyLayerChangedListeners(LayerChangedEvent.GEOGRAPHY);
    }

    public GeoRectangle getBounds() {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return this.bbox;
    }

    public GeoRectangle getBoundsOf(int id[]) {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public GeoRectangle getBoundsOf(int id) {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return null;
    }

    public int getID(GeoPoint p) {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return 0;
    }

    public int getID(double x, double y) {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
        return 0;
    }

    public void paintHighlight(Graphics g, Scaler scale, int id,
            ShadeStyle style) {
        // This method is derived from interface uk.ac.leeds.ccg.geotools.Layer
        // to do: code goes here
    }

    public void paintScaled(GeoGraphics gg) {
        if (DEBUG)
            System.out.println("Painting imagelayer");
        double cellSizeX = 0;
        double cellSizeY = 0;
        boolean drawn;
        Graphics g = gg.getGraphics();
        Scaler scale = gg.getScale();
        GeoRectangle gr = scale.getMapExtent();
        // if(DEBUG)System.out.println("IL-->gr "+gr);
        GeoRectangle me = getBounds();
        // if(DEBUG)System.out.println("IL-->me "+me);

        GeoRectangle out = gr.createIntersect(me);
        if (DEBUG)
            System.out.println("IL-->getStatus=" + this.getStatus());
        int gh, gw;
        if (DEBUG)
            System.out.println("IL (" + this + ") -->0");
        if (out != null) {
            if (DEBUG)
                System.out.println("IL (" + this + ") -->1");

            if (out.equals(me)) {
                // if(DEBUG)System.out.println("IL ("+this+") -->2 ");
                if (DEBUG)
                    System.out.println("IL-->No clip (" + this + ")");
                // GeoRectangle out = bbox;
                int origin[] = scale.toGraphics(out.x, out.y);
                gh = scale.toGraphics(out.height);
                gw = scale.toGraphics(out.width);
                if (getStatus() != Layer.COMPLETED) {
                    if (DEBUG)
                        System.out.println("Not ready returning");
                    g.setColor(Color.red);
                    g.drawRect(origin[0] + 1, origin[1] - gh - 1, gw - 1,
                            gh - 1);
                    return;
                }
                if (this.image != null) {
                    drawn = g.drawImage(this.image, origin[0], origin[1] - gh,
                            gw, gh, null);
                    if (DEBUG)
                        System.out.println("IL (" + this + ") -->3 drawn="
                                + drawn + " " + this.image);
                    
                }
            } else { // clipping required
                if (DEBUG)
                    System.out.println("IL (" + this + ") -->5 ");
                if ((this.getStatus() & Layer.COMPLETED) != Layer.COMPLETED) {
                    if (DEBUG)
                        System.out.println("IL (" + this
                                + ") -->Layer not loaded");
                    return;
                }
                // complex clip case
                cellSizeX = me.width / this.image.getWidth(obs);
                cellSizeY = me.height / this.image.getHeight(obs);
                int x, y, width, height;
                if (out.y % cellSizeY > 0) {
                    out.y = out.y + cellSizeY - (out.y % cellSizeY);
                    out.height += cellSizeY;
                }
                x = (int) Math.floor(((out.x - me.x) / cellSizeX));
                y = (int) Math
                        .ceil(((me.y - out.y + me.height - out.height) / cellSizeY));
                width = (int) Math.ceil((out.width / cellSizeX));
                height = (int) Math.ceil(((out.height) / cellSizeY));
                x = (int) Math.floor(((out.x - me.x) / cellSizeX));
                y = (int) Math
                        .ceil(((me.y - out.y + me.height - out.height) / cellSizeY));
                if (DEBUG)
                    System.out.println("IL-->start " + x + " " + y);
                width = (int) Math.ceil((out.width / cellSizeX));
                height = (int) Math.ceil(((out.height) / cellSizeY));
                if (DEBUG)
                    System.out.println("IL-->w/h " + width + " " + height);
                y = Math.max(y, 0);
                int origin[] = scale.toGraphics(out.x, out.y);
                gh = scale.toGraphics(out.height);
                gw = scale.toGraphics(out.width);
                if ((this.getStatus() & Layer.COMPLETED) != Layer.COMPLETED) {
                    if (DEBUG)
                        System.out.println("IL (" + this + ") -->6 ");
                    g.setColor(Color.red);
                    g.drawRect(origin[0], origin[1] - gh, gw, gh);
                    return;
                }
                int[] data = new int[(height * width)];
                if (DEBUG)
                    System.out.println("copy " + x + " " + y);
                if (DEBUG)
                    System.out.println("copy " + imdata.length + " "
                            + data.length);
                if (DEBUG)
                    System.out.println("copy " + (y * imwidth + x) + " "
                            + ((y + height) * imwidth + x));
                if (((y + height) * imwidth + x) > imdata.length)
                    height--;
                for (int i = 0; i < height; i++) {

                    // if(DEBUG)System.out.println(""+((y+i)*imwidth+x)+","+(i*width)+","+(width));
                    System.arraycopy(imdata, (y + i) * imwidth + x, data, i
                            * width, width);
                }
                /*
                 * PixelGrabber pg = new PixelGrabber( this.image, x, y, width,
                 * height, data, 0, width);
                 * if(DEBUG)System.out.println("IL-->about to grab"); try {
                 * pg.grabPixels(); } catch (InterruptedException e) {
                 * System.err.println("IL-->interrupted waiting for pixels!");
                 * return; } if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                 * System.err.println("IL-->image fetch aborted or errored,
                 * status="+pg.getStatus()); return ; }
                 */
                source = new MemoryImageSource(width, height, data, 0, width);
                Image image2 = obs.createImage(source);
                drawn = g.drawImage(image2, origin[0], origin[1] - gh, gw, gh,
                        obs);
                if (DEBUG)
                    System.out.println("IL (" + this + ") -->5.1 drawn="
                            + drawn + " " + this.image);
                image2 = null;
            }
        }
        
        notifyLayerChangedListeners(111);
    }

    protected byte[] getImageData(java.io.InputStream instr) {
        byte[] res = null;
        try {
            java.io.ByteArrayOutputStream outstr = new java.io.ByteArrayOutputStream();
            byte[] buff = new byte[16000];
            int cnt = instr.read(buff);
            while (cnt != -1) {
                outstr.write(buff, 0, cnt);
                cnt = instr.read(buff);
            } // end of while (cnt != -1)
            // instr.close();
            res = outstr.toByteArray();
            outstr.close();
        } catch (java.io.IOException e) {
            res = null;
        } // end of try-catch

        return res;
    }

    public void run() {
        setStatus(Layer.PENDING);
        if (DEBUG)
            System.out.println("IL--> Request image " + this.src + " " + bbox);

        // Start getting the image. If the image is .gif or .jpg then
        // use default java tools otherwise use Jimi class
        String type = "";
        URLConnection con;
        Toolkit tk = Toolkit.getDefaultToolkit();
        if (src != null) {
            try {
                con = this.src.openConnection();
                type = con.getContentType();
            } catch (java.io.IOException ioe) {
                System.err.println("IL--> problem opening connection");
                return;
            }

            if ((type != null) && type.startsWith("image")) {
                if (type.trim().endsWith("gif") || type.trim().endsWith("jpeg")) {
                    this.image = tk.getImage(this.src);
                    if (DEBUG)
                        System.out.println("New fetch ?");
                } else {
                    this.image = Jimi.getImage(this.src, type);
                }
            } else {
                if (type.startsWith("text")) {
                    try {
                        String line;
                        BufferedReader in = new BufferedReader(
                                new InputStreamReader(con.getInputStream()));
                        while ((line = in.readLine()) != null) {
                            System.err.println("IL(" + this + "):" + line);
                            setStatus(Layer.ERRORED);
                        }
                        return;
                    } catch (IOException e) {
                        System.err.println("IL(" + this + "):" + e);
                        setStatus(Layer.ERRORED);
                        return;
                    }
                }
            }
        } // end of if (src != null)
        if (src_stream != null) {
            System.out.println("going with stream ");
            byte[] img_data = getImageData(src_stream);
            // here we can put condition for creating
            // image or not for some reason
            this.image = tk.createImage(img_data);
        } // end of if (src_stream != null)

        // if(DEBUG)System.out.println(
        // "IL--> Image Width="+this.image.getWidth(obs));
        if (DEBUG)
            System.out.println("adding image to tracker " + myID);
        tracker.addImage(this.image, ++myID);
        try {
            if (DEBUG)
                System.out.println("IL (" + this + ")-->Waiting ");
            while ((tracker.statusID(myID, true) & tracker.LOADING) != 0) {
                if (getStatus() == Layer.ABORTED) {
                    if (DEBUG)
                        System.out.println("Abort - removing image");
                    tracker.removeImage(this.image, myID);
                    break;
                }
                tracker.waitForID(myID, 500);
                if (DEBUG)
                    System.out.println("IL (" + this
                            + ")-->status loop, status="
                            + tracker.statusID(myID, true) + " myID=" + myID);

            }
            int state = tracker.statusID(myID, true);
            if (DEBUG)
                System.out.println("finished load status " + state);
            if (state == tracker.COMPLETE)
                System.out.println("Il: Complete");
            if (state == tracker.ABORTED)
                System.out.println("Il: ABORTED");
            if (state == tracker.ERRORED)
                System.out.println("Il: ERRORED");
            if (state == tracker.LOADING)
                System.out.println("Il: LOADING");
            if ((state & tracker.COMPLETE) == tracker.COMPLETE) {
                int height = image.getHeight(obs);
                int width = image.getWidth(obs);
                imwidth = width;
                imdata = new int[(height * width)];
                PixelGrabber pg = new PixelGrabber(this.image, 0, 0, width,
                        height, imdata, 0, width);
                if (DEBUG)
                    System.out.println("IL-->about to grab");
                try {
                    pg.grabPixels();
                } catch (InterruptedException e) {
                    System.err.println("IL-->interrupted waiting for pixels!");
                    return;
                }
                if ((pg.getStatus() & ImageObserver.ABORT) != 0) {
                    System.err
                            .println("IL-->image fetch aborted or errored, status="
                                    + pg.getStatus());
                    setStatus(Layer.ABORTED);
                    return;
                }
                source = new MemoryImageSource(width, height, imdata, 0, width);
                image = obs.createImage(source);
                if (DEBUG)
                    System.out.println("IL (" + this + ")-->Image Ready ");
                setStatus(Layer.COMPLETED);
                // setStatus() triggers a LayerChangedEvent.
            } else if (state == tracker.ABORTED) {
                setStatus(Layer.ABORTED);
                if (DEBUG)
                    System.out.println("IL (" + this + ")-->Image Aborted ");
            } else {
                Exception e = new Exception();
                e.printStackTrace();
                setStatus(Layer.ERRORED);
                if (DEBUG)
                    System.out.println("IL (" + this + ")-->Image Error ");
            }

        } catch (InterruptedException e) {
            System.out.println("IL (" + this + ")-->Interupted ");
            setStatus(Layer.ERRORED);
            return;
        }
        // } else { // not image
        // if (DEBUG) System.out.println("Image unavailable, con type= "+type);
        // }
        // setStatus(Layer.ERRORED);
    }
}
/*
 * Changes in file:
 * 
 * $Log: ImageLayer.java,v $ Revision 1.1 2005/09/19 10:31:32 CarstenKessler
 * Carsten Keﬂler: First Version submitted to CVS. Revision 1.34.4.1 2002/01/15
 * 17:10:15 ianturton fixed bug (503887) where Image was deleted before it
 * became loaded
 * 
 * Revision 1.34 2001/12/20 15:27:57 ianturton various fixes and small mods to
 * make WMSExample actually run. Can now display an image if you remember to
 * choose gif or jpg as output. Still crashes if you zoom in.
 * 
 * Revision 1.33 2001/12/11 11:35:01 ianturton improved painting of cliped
 * images.
 * 
 * Revision 1.32 2001/12/11 10:35:00 ianturton readded support of png/tiff
 * images
 * 
 * Revision 1.30 2001/12/07 11:01:33 ianturton Removed unnecessary calls for
 * updates in paint method
 * 
 * Revision 1.29 2001/11/22 11:00:01 kobit Separated image data loading from
 * image creating and some comments added
 * 
 * Revision 1.28 2001/11/21 13:48:19 kobit Added support for creating ImageLayer
 * with InputStream as image data source and full support for downaloding map
 * data from WMS with jprotocols package
 * 
 * 
 */
