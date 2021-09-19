package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MinibusRouteType extends BusRouteType {
    @Override
    public String getName() {
        return tr("Minibus route");
    }

    @Override
    public String getTypeIdentifier() {
        return "minibus";
    }
}
