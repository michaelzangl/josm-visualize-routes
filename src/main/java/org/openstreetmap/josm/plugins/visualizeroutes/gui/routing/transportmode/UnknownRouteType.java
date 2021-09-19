package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode;

import static org.openstreetmap.josm.tools.I18n.tr;

public class UnknownRouteType implements RouteType {
    @Override
    public String getName() {
        return tr("Unknown route");
    }

    @Override
    public String getTypeIdentifier() {
        return "";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.UNKNOWN;
    }
}
