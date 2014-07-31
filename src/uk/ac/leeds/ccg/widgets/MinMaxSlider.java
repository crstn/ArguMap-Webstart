/*
 * MinMaxSlider.java
 *
 * Created on December 5, 2001, 10:53 AM
 */

package uk.ac.leeds.ccg.widgets;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author  jamesm
 * @version
 */
public class MinMaxSlider extends java.awt.Component implements java.io.Serializable,java.awt.event.MouseMotionListener,java.awt.event.MouseListener {
    
    private static final String PROP_MINSELECTED_PROPERTY = "MinSelected";
    private static final String PROP_MAXSELECTED_PROPERTY = "MaxSelected";
    
    private PropertyChangeSupport propertySupport;
    
    /** Holds value of property arrowHeadColor. */
    private java.awt.Color arrowHeadColor=Color.black;
    
    /** Holds value of property minValue. */
    private int minValue=0;
    
    /** Holds value of property selectionColor. */
    private java.awt.Color selectionColor=Color.pink;
    
    /** Holds value of property maxValue. */
    private int maxValue=100;
    
    /** Holds value of property barWidth. */
    private int barWidth=10;
    
    /** Holds value of property minSelected. */
    private int minSelected=20;
    
    /** Holds value of property maxSelected. */
    private int maxSelected=40;
    
    /** Holds value of property barBackgroundColor. */
    private java.awt.Color barBackgroundColor = Color.white;
    
    /** Creates new MinMaxSlider */
    public MinMaxSlider() {
        propertySupport = new PropertyChangeSupport( this );
        addMouseMotionListener(this);
        addMouseListener(this);
    }
    
    
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.addPropertyChangeListener(listener);
    }
    
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertySupport.removePropertyChangeListener(listener);
    }
    
    /** Getter for property arrowHeadColor.
     * @return Value of property arrowHeadColor.
     */
    public java.awt.Color getArrowHeadColor() {
        return arrowHeadColor;
    }
    
    /** Setter for property arrowHeadColor.
     * @param arrowHeadColor New value of property arrowHeadColor.
     */
    public void setArrowHeadColor(java.awt.Color arrowHeadColor) {
        this.arrowHeadColor = arrowHeadColor;
    }
    
    /** Getter for property minValue.
     * @return Value of property minValue.
     */
    public int getMinValue() {
        return minValue;
    }
    
    /** Setter for property minValue.
     * @param minValue New value of property minValue.
     */
    public void setMinValue(int minValue) {
        this.minValue = minValue;
        setMinSelected(minValue);
        repaint();
    }
    
    /** Getter for property selectionColor.
     * @return Value of property selectionColor.
     */
    public java.awt.Color getSelectionColor() {
        return selectionColor;
    }
    
    /** Setter for property selectionColor.
     * @param selectionColor New value of property selectionColor.
     */
    public void setSelectionColor(java.awt.Color selectionColor) {
        this.selectionColor = selectionColor;
    }
    Image screenBuffer;
    
    
    
    public void update(Graphics g){
        paint(g);
    }
    
    public void paint(java.awt.Graphics g) {
        int width = getSize().width;
        int height = getSize().height;
        Graphics bg;
        if(screenBuffer == null){
            screenBuffer = this.createImage(width,height);
        }
        if(screenBuffer == null)bg=g;
        else bg = screenBuffer.getGraphics();
        bg.setFont(getFont());
        bg.setColor(getBackground());
        bg.fillRect(0,0,width,height);
        
        bg.setColor(getForeground());
        bg.fillRect(width-barWidth,0,barWidth,height);
        bg.setColor(getBarBackgroundColor());
        bg.fillRect(width-barWidth+2,2,barWidth-4,height-4);
        
        int [] p = getLimits();
        bg.setColor(getSelectionColor());
        bg.fillRect(width-barWidth+2,p[0],barWidth-4,p[1]-p[0]);
        
        bg.setColor(getArrowHeadColor());
        
        //System.out.println(""+p[0]+","+p[1]);
        bg.fillPolygon(new int[]{width-barWidth+1,width-1,width-(barWidth/2),width-barWidth+1},new int[]{p[0],p[0],p[0]+(barWidth/2),p[0]},4);
        
        bg.fillPolygon(new int[]{width-barWidth+1,width-1,width-(barWidth/2),width-barWidth+1},new int[]{p[1],p[1],p[1]-(barWidth/2),p[1]},4);
        
        bg.setColor(getForeground());
        
        
        String min = asLabel(getMinSelected());
        String max = asLabel(getMaxSelected());
        FontMetrics fm = this.getFontMetrics(getFont());
        int len = fm.stringWidth(asLabel(getMinSelected()));
        bg.drawString(min,width-barWidth-len,p[0]);
        len = fm.stringWidth(asLabel(getMaxSelected()));
        bg.drawString(max,width-barWidth-len,p[1]);
        if(screenBuffer!=null){
            g.drawImage(screenBuffer, 0,0,this);
        }
    }
    
    public String asLabel(int value){
        String result = ""+value;
        if(value<0){
            result = result.substring(1);
            result = result+" "+negativeSuffix;
        }
        else{
            result = result+" "+positiveSuffex;
        }
        return result;
    }
    
    protected int[] getLimits(){
        double valRange = maxValue-minValue;
        double pixRange = getSize().height;
        double scale = pixRange/valRange;
        int top = valToPix(minSelected);
        int bottom = valToPix(maxSelected);
        return new int[]{top,bottom};
    }
    
    protected int pixToVal(int pix){
        double valRange = maxValue-minValue;
        double pixRange = getSize().height;
        double scale = pixRange/valRange;
        return (int)((pix/scale)+minValue);
    }
    
    protected int valToPix(int val){
        double valRange = maxValue-minValue;
        double pixRange = getSize().height;
        double scale = pixRange/valRange;
        return (int)((val-minValue)*scale);
    }
    
    /** Getter for property maxValue.
     * @return Value of property maxValue.
     */
    public int getMaxValue() {
        return maxValue;
    }
    
    /** Setter for property maxValue.
     * @param maxValue New value of property maxValue.
     */
    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
        this.setMaxSelected(maxValue);
         repaint();
    }
    
    /** Getter for property barWidth.
     * @return Value of property barWidth.
     */
    public int getBarWidth() {
        return barWidth;
    }
    
    /** Setter for property barWidth.
     * @param barWidth New value of property barWidth.
     */
    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;
    }
    
    /** Getter for property minSelected.
     * @return Value of property minSelected.
     */
    public int getMinSelected() {
        return minSelected;
    }
    
    /** Setter for property minSelected.
     * @param minSelected New value of property minSelected.
     */
    public void setMinSelected(int minSelected) {
        if(minSelected>getMaxSelected())minSelected = getMaxSelected()-1;
        int old = this.minSelected;
        this.minSelected = minSelected;
        //this.propertySupport.firePropertyChange(this.PROP_MINSELECTED_PROPERTY,new Integer(old),new Integer(minSelected));
        repaint();
    }
    
    /** Getter for property maxSelected.
     * @return Value of property maxSelected.
     */
    public int getMaxSelected() {
        return maxSelected;
    }
    
    /** Setter for property maxSelected.
     * @param maxSelected New value of property maxSelected.
     */
    public void setMaxSelected(int maxSelected) {
        if(maxSelected<=getMinSelected())maxSelected = getMinSelected()+1;
        int old = maxSelected;
        this.maxSelected = maxSelected;
        //this.propertySupport.firePropertyChange(this.PROP_MAXSELECTED_PROPERTY,new Integer(old),new Integer(maxSelected));
        repaint();
    }
    
    public void mouseDragged(java.awt.event.MouseEvent mouseEvent) {
        
        int value = pixToVal(mouseEvent.getY());
        //System.out.println("start "+startValue+" now "+value+" diff "+(value-startValue));
        switch(whatToMove){
            case TOP:
                setMinSelected(value);
                break;
            case BOTTOM:
                setMaxSelected(value);
                break;
            case MID:
                setMinSelected(getMinSelected()+value-startValue);
                setMaxSelected(getMaxSelected()+value-startValue);
                startValue=value;
                break;
            default:
                return;
        }
        
    }
    
    public void mouseMoved(java.awt.event.MouseEvent mouseEvent) {
    }
    
    public void mouseExited(java.awt.event.MouseEvent mouseEvent) {
    }
    
    public void mouseReleased(java.awt.event.MouseEvent mouseEvent) {
        this.propertySupport.firePropertyChange("Value Changed",null,null);
    }
    static final int NOTHING=1,TOP=2,MID=3,BOTTOM=4;
    int whatToMove;
    int startValue;
    
    /** Holds value of property negativeSuffix. */
    private String negativeSuffix = "BCE";    
    
    /** Holds value of property positiveSuffex. */
    private String positiveSuffex = "CE";    
    
    public void mousePressed(java.awt.event.MouseEvent mouseEvent) {
        int y = mouseEvent.getY();
        int p[] = getLimits();
        if (y<p[0]) whatToMove = NOTHING;
        else if (y<p[0]+5) whatToMove = TOP;
        else if(y<p[1]-5)whatToMove = MID;
        else if(y<p[1]) whatToMove = BOTTOM;
        else whatToMove = NOTHING;
        startValue = pixToVal(y);
        //System.out.println("Presed "+whatToMove);
    }
    
    public void mouseClicked(java.awt.event.MouseEvent mouseEvent) {
    }
    
    public void mouseEntered(java.awt.event.MouseEvent mouseEvent) {
    }
    
    /** Getter for property barBackgroundColor.
     * @return Value of property barBackgroundColor.
     */
    public java.awt.Color getBarBackgroundColor() {
        return barBackgroundColor;
    }
    
    /** Setter for property barBackgroundColor.
     * @param barBackgroundColor New value of property barBackgroundColor.
     */
    public void setBarBackgroundColor(java.awt.Color barBackgroundColor) {
        this.barBackgroundColor = barBackgroundColor;
    }
    
    /** Getter for property negativeSuffix.
     * @return Value of property negativeSuffix.
     */
    public String getNegativeSuffix() {
        return negativeSuffix;
    }
    
    /** Setter for property negativeSuffix.
     * @param negativeSuffix New value of property negativeSuffix.
     */
    public void setNegativeSuffix(String negativeSuffix) {
        this.negativeSuffix = negativeSuffix;
    }
    
    /** Getter for property positiveSuffex.
     * @return Value of property positiveSuffex.
     */
    public String getPositiveSuffex() {
        return positiveSuffex;
    }
    
    /** Setter for property positiveSuffex.
     * @param positiveSuffex New value of property positiveSuffex.
     */
    public void setPositiveSuffex(String positiveSuffex) {
        this.positiveSuffex = positiveSuffex;
    }
    
}
