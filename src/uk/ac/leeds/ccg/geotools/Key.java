package uk.ac.leeds.ccg.geotools;

import java.awt.Dimension;
import java.awt.Panel;

/**
 * The abstract base class used by all keys.<br>
 * A key is a small panel which represents the information needed
 * to interprit the colours provided by a shader.<br>
 * Each shader type should provide an implementation of this class
 * so that it can be displayed.
 */
public abstract class Key extends Panel implements ShaderChangedListener
{

 /**
  * The shader from which this key should draw information from.<br>
  * By including a ref to it in the key, any changes to the shader,
  * such as a different choice of colours can be updated automaticaly.
  **/
 Shader shader;
 
 protected Key(){
 }
 
 /**
  * The basic constructor for a key
  * @param The shader to optain colour information from
  */
 public Key(Shader s){
    setShader(s);
    s.addShaderChangedListener(this);
 }
 
 /**
  * Used to change the shader that this key is attached to.
  * Probably not a wise operation, better to grab a new key from 
  * that shader instead.
  * @param s The new shader to assosciate with
  **/
 public void setShader(Shader s){
    if(shader != null)shader.removeShaderChangedListener(this);
    shader = s;
    shader.addShaderChangedListener(this);
    //updateKey();
 }

 /**
  * updates the key to reflect any changes in the shader which it
  * is attached to.
  **/
  public void updateKey(){
    updateLabels();
    repaint();
  }
  /**
   * Called when the shader which created this key changes in some way.
   **/
  public void shaderChanged(ShaderChangedEvent scl){
    updateKey();
  }
 
 /**
  * updates the keys labels to reflect any changes in the shader which
  * is attached to.
  * called automaticaly by updateKey();
  **/
  public void updateLabels(){
    //do nothing
  }
  
  public Dimension getPreferredSize(){
      Dimension d = super.getPreferredSize();
      System.out.println("default prefered size is "+super.getPreferredSize());
      System.out.println("default min size is "+super.getMinimumSize());
      System.err.println("preferred size asked for : "+new Dimension(Math.max(180,d.width),Math.max(40,d.height)));
      return new Dimension(Math.max(180,d.width),Math.max(40,d.height));
  }
  
}
