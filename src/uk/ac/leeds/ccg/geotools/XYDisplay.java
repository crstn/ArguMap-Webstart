package uk.ac.leeds.ccg.geotools;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Label;
import java.awt.Panel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.Serializable;

public class XYDisplay extends Panel implements MouseMotionListener,
	MouseListener,Serializable{

	Label x;
	Label y;
	public Dimension pd;
	String s;
	int dp=2;
	boolean showProjected = false;
	public XYDisplay(){
		super();
		setLayout(new GridLayout(1,2));
		x=new Label("00000000",Label.RIGHT);
		x.setText("00000000");
		y=new Label("00000000",Label.RIGHT);
		setSize(205,22);
		//x.setSize(100,x.getSize().height);
		//y.setSize(100,y.getSize().height);
		add(x);
		add(y);
		pd=new Dimension(210,x.getSize().height);
	}
	//public Dimension getPreferredSize(){
		//System.out.println("Asked for pref "+pd);
		//System.out.println("X "+x.getSize());
		//pd=new Dimension(210,x.getSize().height);
		//return pd;
	//}
	//public Dimension getMinimumSize(){
		//System.out.println("Asked for min "+pd);
		//pd=new Dimension(210,x.getSize().height);
		//return pd;
	//}
	public final void mouseDragged(MouseEvent e){}
	public final void mouseMoved(MouseEvent e){
		double p[]; 
		
		if(showProjected==false){
			p= ((Viewer)e.getSource()).getMapPoint();	
		}else{
			p= ((Viewer)e.getSource()).getProjPoint();
		}
		{s=(new Double(p[0])).toString();
    int i = s.lastIndexOf('.');
    if(i!=-1&&dp>0){
      String dec;
      if((i+dp+1)<s.length()){
        dec=s.substring(i,i+dp+1);
      }else{
        dec=s.substring(i);
      }
      String num=s.substring(0,i);
      //System.out.println(s+" "+num+" "+dec);
      x.setText(num+dec);
    }else{
      x.setText(s);
    }
}
		{s=(new Double(p[1])).toString();
    int i = s.lastIndexOf('.');
    if(i!=-1&&dp>0){
      String dec;
      if((i+dp+1)<s.length()){
        dec=s.substring(i,i+dp+1);
      }else{
        dec=s.substring(i);
      }
      String num=s.substring(0,i);
      //System.out.println(s+" "+num+" "+dec);
      y.setText(num+dec);
    }else{
      y.setText(s);
    }
		}

		//x.setText(""+p[0]);
		//y.setText(""+p[1]);
	}
	public final void mouseClicked(MouseEvent e){}
	public final void mouseEntered(MouseEvent e){}
	public final void mousePressed(MouseEvent e){}
	public final void mouseReleased(MouseEvent e){}
	public final void mouseExited(MouseEvent e){
		x.setText("00000000");	
		y.setText("00000000");	
	}

	public void setShowProjected(boolean p){
		showProjected = p;
	}
}

