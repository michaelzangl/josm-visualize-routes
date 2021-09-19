package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import static org.openstreetmap.josm.tools.I18n.tr;

public class TramRouteType extends TrainRouteType {
    @Override
    public String getName() {
        return tr("Tram");
    }

    @Override
    public String getTypeIdentifier() {
        return "tram";
    }
}
