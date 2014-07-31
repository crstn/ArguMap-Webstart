package uk.ac.leeds.ccg.widgets;

public class ProgressChangedEvent extends java.util.EventObject{
	private double progress;
	public ProgressChangedEvent(Object source, double p){
		super(source);
		progress=p;
	}
	public double getProgress(){
		return progress;
	}
}


