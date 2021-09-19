package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland.RailwayRouteType;

import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class TrainRouteType implements RouteType {
    @Override
    public String getName() {
        return tr("Train");
    }

    @Override
    public String getTypeIdentifier() {
        return "train";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.PUBLIC_TRANSPORT;
    }

    @Override
    public AccessDirection mayDriveOn(Map<String, String> tags) {
        return RailwayRouteType.getRailAccess(tags);
    }
}
