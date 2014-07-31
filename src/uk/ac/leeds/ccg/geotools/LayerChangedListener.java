package uk.ac.leeds.ccg.geotools;

public interface LayerChangedListener extends java.util.EventListener {
    void layerChanged(LayerChangedEvent tce);

}