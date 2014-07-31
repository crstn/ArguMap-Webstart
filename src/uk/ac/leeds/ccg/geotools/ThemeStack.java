/*
 * ThemeStack.java
 *
 * Created on 04 October 2001, 01:19
 */

package uk.ac.leeds.ccg.geotools;

import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author  jamesm
 * @version 
 */
public class ThemeStack {
    HashMap lookup = new HashMap();
    TreeSet themeInfos = new TreeSet();
    /** Creates new ThemeStack */
    public ThemeStack() {
    }
    
    public int addTheme(Theme t,int waight,boolean visible){
        while(getThemeByWaight(waight)!=null){
            waight--;
        }
        //System.out.println("Added new theme to stack with waight of "+waight);
        ThemeInfo info = new ThemeInfo(t,waight,visible);
        lookup.put(t,info);
        themeInfos.add(info);
        return waight;
    }
    
    public void removeTheme(Theme t){
//        System.out.println("lookup: "+lookup.toString());
//        System.out.println("parameter theme"+t.toString());
//        System.out.println("lookup theme: "+lookup.get(t).toString());
        if(lookup.get(t) != null){
            themeInfos.remove(lookup.get(t));
        }
        lookup.remove(t);
    }
    
    public void setToBottom(Theme t){
        //ThemeInfo info = (ThemeInfo)lookup.get(t);
        //themeInfos.remove(info);
        //info.setWaight(((ThemeInfo)themeInfos.last()).getWaight()+1);
        setWaight(t,((ThemeInfo)themeInfos.first()).getWaight()+1);
    }
    
    public void setToTop(Theme t){
        ThemeInfo info = (ThemeInfo)lookup.get(t);
        themeInfos.remove(info);
        ((ThemeInfo)themeInfos.first()).getWaight();
        info.setWaight(((ThemeInfo)themeInfos.last()).getWaight()-1);
        themeInfos.add(info);
    }
        
    
    public void setWaight(Theme t,int waight){
        Theme old = this.getThemeByWaight(waight);
        if(old!=null)setWaight(old,waight+1);
        //System.out.println("Setting waight of "+((ThemeInfo)lookup.get(t)).getTheme()+" to "+waight);
        ThemeInfo info = (ThemeInfo)lookup.get(t);
        themeInfos.remove(info);
        info.setWaight(waight);
        themeInfos.add(info);
       
    }
    
    public void swapThemes(Theme a,Theme b){
        //System.out.println("Swapping theme "+a.getName()+" with "+b.getName());
        ThemeInfo first = (ThemeInfo)lookup.get(a);
        ThemeInfo second = (ThemeInfo)lookup.get(b);
        int aw = first.getWaight();
        int bw = second.getWaight();
        
        themeInfos.remove(first);
        themeInfos.remove(second);
        second.setWaight(aw);
        first.setWaight(bw);
        themeInfos.add(first);
        themeInfos.add(second);
    }
    
    public void swapThemes(int a,int b){
        swapThemes(getThemeByWaight(a),getThemeByWaight(b));
    }
    
    public int getWaight(Theme t){
        return ((ThemeInfo)lookup.get(t)).getWaight();
    }
    
    public void setIsVisible(Theme t,boolean visible){
        ((ThemeInfo)lookup.get(t)).setIsVisible(visible);
    }
    
    public boolean isVisible(Theme t){
        return ((ThemeInfo)lookup.get(t)).isVisible();
    }
    
    public ThemeInfo[] getOrderedThemeInfos(){
        //SortedMap head = themeInfos.headSet(new ThemeInfo(null,Integer.MAX_VALUE,false));
        return (ThemeInfo[])themeInfos.toArray(new ThemeInfo[0]);
    }
    
    public Theme getThemeByWaight(int w){
        ThemeInfo dummy = new ThemeInfo(null,w,true);
        ThemeInfo dummy2 = new ThemeInfo(null,w-1,true);
        SortedSet sub = themeInfos.subSet(dummy,dummy2);
        if(sub.isEmpty())return null;
        return ((ThemeInfo)sub.first()).getTheme();
        
    }
            

    
    class ThemeInfo implements Comparable{
        Theme theme;
        boolean visible;
        int waight;
        
        public ThemeInfo(Theme theme,int waight,boolean visible){
            this.theme=theme;
            this.waight=waight;
            this.visible = visible;
        }
        
        public int compareTo(Object o){
            ThemeInfo b = (ThemeInfo)o;
            //if(!visible && b.visible) return -1000;
            //else if(visible &&!b.visible) return 1000;   
            //else return waight-b.waight;
            return b.waight-waight;
        }
        
        public boolean isVisible(){
            return visible;
        }
        public int getWaight(){
            return waight;
        }
        
        public Theme getTheme(){
            return theme;
        }
        
        public void setWaight(int waight){
            this.waight = waight;
        }
        
        public void setIsVisible(boolean visible){
            this.visible = visible;
        }
    }
}
