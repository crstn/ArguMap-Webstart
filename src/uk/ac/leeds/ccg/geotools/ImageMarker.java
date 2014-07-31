package uk.ac.leeds.ccg.geotools;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
/**
 * ImageMarker creates a marker icon out of images passed in as parameters.
 * The (x,y) point provided is used as the center point of the icon.
 * @author Peter.Pan@comsat.com 
 * @author <a href="mailto:cameron@shorter.net">Cameron Shorter</a>
 **/
public class ImageMarker implements
    uk.ac.leeds.ccg.geotools.Marker,
    ImageObserver
{
    boolean DEBUG=false;
    // image[0] = normal image
    // image[1] = highlighted image
    Image marker[]=new Image[2];
    ImageObserver imgObserver;
    Layer layer=null;
    boolean layerNotified[]={false,false};
    
    /**
     * Initialise the class, providing an ImageObserver to notify if the icons
     * have not been loaded when we first try to draw it.
     * @param normalImage Icon to use normally
     * @param highlightedImage Icon to use during hover-over mode
     */
    public ImageMarker(Image normalImage, Image highlightedImage, ImageObserver imgOb){
        this.marker[0]=normalImage;   
        this.marker[1]=highlightedImage;
        this.imgObserver=imgOb;
        if(DEBUG)System.out.println("ImageMarker1 created");
    }
    
    /**
     * Initialise the class, if the icons are not ready to be drawn when first
     * called, a notifyLayerChangedListeners event is sent when the icons are
     * loaded.
     * @param normalImage Icon to use normally
     * @param highlightedImage Icon to use during hover-over mode
     */
    public ImageMarker(Image normalImage, Image highlightedImage, Layer layer){
        this.marker[0]=normalImage;   
        this.marker[1]=highlightedImage;
        this.imgObserver=this;
        this.layer=layer;
        if(DEBUG)System.out.println("ImageMarker2 created for layer="+layer);
    }
    
    /**
     * Draw the normal icon at the location specified.
     */
    public void paintScaled(GeoGraphics gg,GeoPoint p,int size) {
        Scaler s = gg.getScale();
        int mid[] = s.toGraphics(p);
        Graphics g = gg.getGraphics();        
        int h = marker[0].getHeight(imgObserver);
        int w = marker[0].getWidth(imgObserver);
        int x = mid[0]-w/2;
        int y = mid[1]-h/2;     
        g.drawImage(marker[0],x,y,imgObserver);
    }

    /**
     * Draw the highlighted icon at the location specified.
     */
    public void paintHighlight(Graphics g,GeoPoint p,int size,Scaler scale,ShadeStyle style) {
        int mid[] = scale.toGraphics(p);
        int h = marker[1].getHeight(imgObserver);
        int w = marker[1].getWidth(imgObserver);
        int x = mid[0]-w/2;
        int y = mid[1]-h/2;     
        g.drawImage(marker[1],x,y,imgObserver);
    }

    /**
     * If we requested to draw this image and it was not ready, then this method
     * will be called - sends a LayerChangedEvent if layer has been initialised.
     */
    public boolean imageUpdate(
        Image img,
        int infoflags,
        int x,
        int y,
        int width,
        int height)
    {
        int imageId=0;
        for (int i=0;i<=1;i++){
            if(img==marker[i]){
                imageId=i;
                break;
            }
        }

        boolean done = ((infoflags & (ERROR | FRAMEBITS | ALLBITS)) != 0);
        if (done && (layer!=null)&&(!layerNotified[imageId])){
            if(DEBUG)System.out.println(
                "ImageMarker: Send notifyLayerChangedListeners for layer="+layer);
            layerNotified[imageId]=true;
            layer.notifyLayerChangedListeners(LayerChangedEvent.DATA);
        }
        return !layerNotified[imageId];
    }
}
