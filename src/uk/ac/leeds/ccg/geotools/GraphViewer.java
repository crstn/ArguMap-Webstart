package uk.ac.leeds.ccg.geotools;


public class GraphViewer extends Viewer{
  XYGrid g;
  public GraphViewer(){
    super();
    scale.SetIsotrophic(false);
    g =new XYGrid();
    g.setBounds(new GeoRectangle());
    Theme grid = new Theme(g);
    addTheme(grid);
  }
  public void addTheme(Theme t){
    super.addTheme(t);
    g.setBounds(t.getBounds());
  }
}
