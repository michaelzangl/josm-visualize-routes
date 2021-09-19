package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;

import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class FerryRouteType implements RouteType {
    @Override
    public String getName() {
        return tr("Ferry");
    }

    @Override
    public String getTypeIdentifier() {
        return "ferry";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.PUBLIC_TRANSPORT;
    }

    @Override
    public AccessDirection mayDriveOn(Map<String, String> tags) {
        return "ferry".equals(tags.get("route")) ? AccessDirection.BOTH : AccessDirection.NONE;
    }
}
