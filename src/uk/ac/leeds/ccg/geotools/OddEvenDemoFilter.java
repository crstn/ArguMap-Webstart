package uk.ac.leeds.ccg.geotools;

/**
 * This class is only to demonstrate how a filter could be writen.
 * filters allow features to be hidden from view based on their id.
 * <br>This is a v.silly filter that tests the odd/evenness of each id.
 */
public class OddEvenDemoFilter extends uk.ac.leeds.ccg.geotools.SimpleFilter
{
    private boolean showOdd = true;
    /**
     * Takes the id, and performs a test to see if features with that
     * id should be displayed.
     * for this class that tests to see if it is odd or even.
     * @param id An int of the id to test.
     * @return boolean true if features with this ID should be displayed.
     */
    public boolean isVisible(int id)
    {
        boolean isOdd = (id%2>0);
        if(isOdd & showOdd){return true;}
        if(!isOdd & !showOdd){return true;}
        return false;
    }
    
    /**
     * Sets if features with odd ids should be show.
     * @param flag true if odd ids should be show, false if even ids should be shown.
     */
    public void setShowOdd(boolean flag){
        showOdd = flag;
        notifyFilterChangedListeners(FilterChangedEvent.DATA);
    }
    
    public Object clone() {
        OddEvenDemoFilter c = new OddEvenDemoFilter();
        c.setShowOdd(this.showOdd);
        return c;
    }
    
}