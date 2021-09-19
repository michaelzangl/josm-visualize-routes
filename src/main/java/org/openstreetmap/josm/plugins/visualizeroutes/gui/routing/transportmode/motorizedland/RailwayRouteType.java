package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland;

import com.opencsv.bean.ComplexFieldMapEntry;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class RailwayRouteType implements RouteType {
    /**
     * 'railway' values that are not acutally a routable way but a building / other thing tha is mapped as way.
     */
    private static final List<String> NON_RAILWAY_RAILWAY_TAGS = Arrays.asList(
            "platform",
            "station",
            "turntable",
            "roundhouse",
            "traverser",
            "wash"
    );

    @Override
    public String getName() {
        return tr("Railway");
    }

    @Override
    public String getTypeIdentifier() {
        return "railway";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.MOTORIZED_LAND;
    }

    @Override
    public AccessDirection mayDriveOn(Map<String, String> tags) {
        return getRailAccess(tags);
    }

    public static AccessDirection getRailAccess(Map<String, String> tags) {
        var railway = tags.get("railway");
        var allowed = railway != null && !NON_RAILWAY_RAILWAY_TAGS.contains(railway);
        return allowed ? AccessDirection.BOTH : AccessDirection.NONE;
    }
}
