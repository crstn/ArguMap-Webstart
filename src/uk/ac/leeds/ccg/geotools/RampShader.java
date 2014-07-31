package uk.ac.leeds.ccg.geotools;

import java.awt.Color;
import java.io.Serializable;

/**
 * @author <a href="http://www.geog.leeds.ac.uk/staff/i.turton/i.turton.html">
 *         Ian Turton</a> Centre for Computaional Geography, University of
 *         Leeds, LS2 9JT, 1998.<br>
 *         <a href="mailto:ian@geog.leeds.ac.uk">i.turton@geog.leeds.ac.uk</a>
 */

public class RampShader extends SimpleShader implements
        uk.ac.leeds.ccg.geotools.Shader, Serializable {
    // private Color lo=Color.white;
    // private Color hi=Color.red;

    private boolean rangeSet = false;
    private double grad, inter;

    // private double l,h;

    public RampShader() {
        conf = new Configure();
    }

    public RampShader(double low, double hi) {
        this();
        setRange(low, hi);
    }

    public void setRange(double low, double hi) {
        super.setRange(low, hi);
        if (min == max)
            max += 1.0d;
        grad = 255.0 / (max - min);
        inter = 255.0 - (grad * max);

        rangeSet = true;
    }

    public void extendRange(double low, double hi) {
        if (!rangeSet) {
            setRange(low, hi);
        }
        setRange(Math.min(low, min), Math.max(hi, max));
    }

    /*
     * public void setMissingValueColor(Color color) { missingColor=color; }
     * 
     * public void setMissingValueCode(double code) { missingCode=code; }
     */

    public int getRGB(double value) {
        return (getColor(value).getRGB());
    }

    /*
     * public Color getMissingValueColor() { return missingColor; }
     * 
     * public double getMissingValueCode() { return missingCode; }
     */
    public double getIntercept() {
        return inter;
    }

    public double getGradient() {
        return grad;
    }

    public Color getColor(double value) {
        if (value == missingCode)
            return missingColor;
        if (value > max || value < min)
            return missingColor;
        int c = (int) (value * grad + inter);
        // System.out.println(value+" -> "+c);
        c = (int) Math.min(255.0, c);
        // System.out.println(value+" -> "+c);
        c = (int) Math.max(0.0, c);
        // System.out.println(value+" -> "+c);
        Color ret = new Color(c, 0, 255 - c);
        return ret;
    }

    /**
     * Gets a descriptive name for this shader type
     */
    public String getName() {
        return "Ramp Shader";
    }

}
