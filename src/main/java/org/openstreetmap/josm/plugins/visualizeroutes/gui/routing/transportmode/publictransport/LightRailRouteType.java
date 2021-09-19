package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import static org.openstreetmap.josm.tools.I18n.tr;

public class LightRailRouteType extends TrainRouteType {
    @Override
    public String getName() {
        return tr("Light rail");
    }

    @Override
    public String getTypeIdentifier() {
        return "light_rail";
    }
}
