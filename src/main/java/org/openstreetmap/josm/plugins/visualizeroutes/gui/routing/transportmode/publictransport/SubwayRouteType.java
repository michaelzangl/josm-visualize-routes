package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import static org.openstreetmap.josm.tools.I18n.tr;

public class SubwayRouteType extends TrainRouteType {
    @Override
    public String getName() {
        return tr("Subway");
    }

    @Override
    public String getTypeIdentifier() {
        return "subway";
    }
}