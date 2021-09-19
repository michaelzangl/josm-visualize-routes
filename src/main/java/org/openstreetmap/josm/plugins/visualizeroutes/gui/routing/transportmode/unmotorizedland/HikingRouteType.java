package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland;

import static org.openstreetmap.josm.tools.I18n.tr;

public class HikingRouteType extends FootRouteType {
    @Override
    public String getName() {
        return tr("Hiking");
    }

    @Override
    public String getTypeIdentifier() {
        return "hiking";
    }

}
