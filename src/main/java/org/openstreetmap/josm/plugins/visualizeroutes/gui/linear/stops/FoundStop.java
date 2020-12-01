package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.stops;

import java.awt.Component;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public interface FoundStop {
    boolean isIncomplete();

    boolean matches(OsmPrimitive p);

    String getNameAndInfos();

    default Component createActionButtons() {
        return null;
    }
}
