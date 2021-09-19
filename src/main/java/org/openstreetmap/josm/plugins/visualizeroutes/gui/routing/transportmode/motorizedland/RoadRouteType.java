package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;

import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class RoadRouteType implements RouteType {

    @Override
    public String getName() {
        return tr("Road");
    }

    @Override
    public String getTypeIdentifier() {
        return "road";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.MOTORIZED_LAND;
    }

    @Override
    public AccessDirection mayDriveOn(Map<String, String> tags) {
        return tags.containsKey("highway") ? AccessDirection.BOTH : AccessDirection.NONE;
    }
}
