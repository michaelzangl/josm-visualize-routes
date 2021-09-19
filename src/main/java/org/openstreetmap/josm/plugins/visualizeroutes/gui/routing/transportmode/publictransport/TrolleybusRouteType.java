package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import static org.openstreetmap.josm.tools.I18n.tr;

public class TrolleybusRouteType extends BusRouteType {
    @Override
    public String getName() {
        return tr("Trolleybus service");
    }

    @Override
    public String getTypeIdentifier() {
        return "trolleybus";
    }
}
