package org.openstreetmap.josm.plugins.visualizeroutes.gui.stopvicinity;

import org.openstreetmap.josm.data.ProjectionBounds;

public class ZoomSaver {
    private ProjectionBounds lastZoom;

    public ZoomSaver() {
    }

    public void setLastZoom(ProjectionBounds lastZoom) {
        this.lastZoom = lastZoom;
    }

    public ProjectionBounds getLastZoom() {
        return lastZoom;
    }
}
