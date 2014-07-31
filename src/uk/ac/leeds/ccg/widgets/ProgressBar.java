package uk.ac.leeds.ccg.widgets;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;

public class ProgressBar extends Canvas implements ProgressEventListener{
	double completed=0.0;
	double max=100.0;

	public ProgressBar(double max,int w,int h){
		super();
		setSize(w,h);
		this.max=max;
	}
	public ProgressBar(double max){
		this(max,100,10);
	}
	public void setMax(double d){
		max=d;
	}
	final public void setCompleted(double d){
		completed=d;
		repaint();
	}
	public void progressChanged(ProgressChangedEvent e){
		setCompleted(e.getProgress());
	}

	public void paint(Graphics g){
		int l = getBounds().width;// getWidth();
		int h = getBounds().height;// getHeight();
		g.setColor(Color.red);
		g.fillRect(0,0,(int)(l*(completed/max)),h);
		g.setColor(Color.green);
		g.fillRect((int)(l*(completed/max)),0,(int)(l-l*(completed/max)),h);
	}
}
