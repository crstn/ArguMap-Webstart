package uk.ac.leeds.ccg.geotools;


import java.awt.Checkbox;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Label;
import java.awt.TextField;
import java.util.Iterator;

import uk.ac.leeds.ccg.geotools.classification.Bin;
import uk.ac.leeds.ccg.geotools.classification.Classifier;
import uk.ac.leeds.ccg.geotools.classification.ClassifierStats;
import uk.ac.leeds.ccg.geotools.classification.Difference;
import uk.ac.leeds.ccg.geotools.classification.EqualInterval;
import uk.ac.leeds.ccg.geotools.classification.NaturalBreaks;
import uk.ac.leeds.ccg.geotools.classification.Quantile;
import uk.ac.leeds.ccg.widgets.ColorPickLabel;

public class ClassificationShader extends DiscreteShader {
    private Shader color;
    private int cat=0;//number of catagories
    private GeoData data;
    private Color start,end;
    private boolean shortest;
    private boolean showInCatCount = true;
    private int mode;
    private Classifier classifier;
    
    public static final int EQUAL_INTERVAL=0, DIFFERENCE=1, QUANTILE = 2,BREAKS = 3;
    
    public ClassificationShader(){
    }
    
    public ClassificationShader(GeoData d,int cat,int mode){
        this(d,cat,mode,Color.blue,Color.red,true);
    }
    
    public ClassificationShader(GeoData d , int cat, int mode, Color start, Color end){
        this(d,cat,mode,start,end,true);
    }
    
    public ClassificationShader(GeoData d,int cat,int mode,Color start, Color end,boolean shortest){
        data = d;
        conf = new Configure();
        this.mode=mode;
        setupCats(cat,mode,start,end,shortest);
    }
    public int getMode(){
        return mode;
    }
    
    public int getNumberOfCatagories(){
        return cat;
    }
    public void setNumberOfCatagories(int c){
        cat=c;
    }
    public void setGeoData(GeoData gd){
        data = gd;
        setupCats(cat,mode,start,end,shortest);
    }
    /**
     * Gets a descriptive name for this shader type
     */
    public String getName(){return "Classification Shader";}
    
     /**
     * Sets the range for this shader by checking the range of
     * the given data set.
     * this method also makes sure the GeoData's missing value code
     * and the shaders missing value code match.
     * @param d The GeoData to pull the range from
     */
    public void setRange(GeoData d){
        System.out.println("Replacing data "+data+" with new one "+d);
        data=d;//not sure if this is a good idea...
        double temp = d.getMissingValueCode();
        if(temp!=missingCode){
            d.setMissingValueCode(missingCode);
            setRange(d.getMin(),d.getMax());
            d.setMissingValueCode(temp);
        }
        else{
            setRange(d.getMin(),d.getMax());
        }
        setupCats(cat,mode,start,end,shortest);
        this.notifyShaderChangedListeners();
        getKey().updateKey();
         rangeData = d;
    }
    
    public synchronized void setupCats(int cat,int mode,Color start, Color end,boolean shortest){
	String catName;
	double thisDataValue,nextDataValue;
        int numberOfDifferentValues,counter;
        this.start=start;
        this.end=end;
        this.cat=cat;
        this.shortest=shortest;
        this.mode=mode;
        if(cat>1){
            color = new HSVShader(start,end,shortest);
            color.setRange(1,cat);
        }
        else{
            color = new MonoShader(start);
        }
        keys.removeAllElements();
        Bin value;
        //     ArrayList list = sort(data);
        switch(mode){
            
            case EQUAL_INTERVAL:
                classifier = new EqualInterval(data,cat);
                break;
                
            case DIFFERENCE:
                classifier = new Difference(data,cat);
                break;
                
            case QUANTILE:
                classifier = new Quantile(data,cat);
                break;
            case BREAKS:
                classifier = new NaturalBreaks(data,cat);
                break;
        }
        Iterator binList = classifier.getBins().iterator();
        int i=1;
        
        ClassifierStats stats = new ClassifierStats();
        int binCounts[] = stats.countForEachBin(classifier,data);
        String countString = "";
        while(binList.hasNext()){
            Bin bin = (Bin)binList.next();
            
            if(this.showInCatCount) countString = " ["+binCounts[i-1]+"]";
            RangeItem k = new RangeItem(bin,color.getColor(i),bin.toString()+ countString);
            System.out.println("Adding "+k);
            keys.addElement(k);
            i++;
        }
        getKey().updateKey();
        notifyShaderChangedListeners();
    }
    
    
    
    class Configure extends SimpleShader.Configure{
        ColorPickLabel startLabel,endLabel;
        TextField catCount;
        Checkbox direct;
        Checkbox inCat;
        Choice modeList;
        public void update(){
            super.update();
            startLabel.setPickColor(start);
            endLabel.setPickColor(end);
            catCount.setText(""+cat);
            direct.setState(shortest);
            inCat.setState(showInCatCount);
            switch(mode){
                case ClassificationShader.EQUAL_INTERVAL:
                    System.out.println("Equ Int Choice");
                    modeList.select("Equal Interval");
                    break;
                case ClassificationShader.DIFFERENCE:
                    System.out.println("Difference Choice");
                    modeList.select("Difference");
                    break;
                case ClassificationShader.QUANTILE:
                    System.out.println("Quantile Choice");
                    modeList.select("Quantile");
                    break;
                case ClassificationShader.BREAKS:
                    System.out.println("Breaks Choice");
                    modeList.select("Breaks");
                    break;
            }
        }
        
        public void actionChanges(){
            super.actionChanges();
            start = startLabel.getPickColor();
            end = endLabel.getPickColor();
            try{
                int tcat = Integer.valueOf(catCount.getText()).intValue();
                cat = tcat;
                showInCatCount = inCat.getState();
            }
            catch(NumberFormatException nfe){System.err.println("Invalid value for new catagory count "+nfe);}
            
            switch(modeList.getSelectedIndex()){
                case 0:
                    mode = ClassificationShader.EQUAL_INTERVAL;
                    break;
                case 1:
                    mode = ClassificationShader.DIFFERENCE;
                    break;
                case 2:
                    mode = ClassificationShader.QUANTILE;
                    break;
                case 3:
                    mode = ClassificationShader.BREAKS;
                    break;
            }
            setupCats(cat,mode,start,end,direct.getState());
        }
        
        public void addItems(){
            super.addItems();
            System.out.println("Adding label");
            Label sc = new Label("Start Color");
            add(sc);
            startLabel = new ColorPickLabel(Color.gray);
            add(startLabel);
            Label ec = new Label("End Color");
            add(ec);
            endLabel = new ColorPickLabel(Color.gray);
            add(endLabel);
            add(new Label("Catagory Count"));
            catCount = new TextField("?");
            add(catCount);
            direct = new Checkbox("Direct",true);
            add(direct);
            inCat = new Checkbox("Show Counts",true);
            add(inCat);
            modeList = new Choice();
            modeList.add("Equal Interval");
            modeList.add("Difference");
            modeList.add("Quantile");
            modeList.add("Natural Breaks");
            add(new Label("Mode"));
            add(modeList);
            
        }
    }
    
    
    //{{DECLARE_CONTROLS
    //}}
}
