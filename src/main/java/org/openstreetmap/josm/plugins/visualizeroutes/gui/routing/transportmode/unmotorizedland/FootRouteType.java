package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class FootRouteType implements RouteType {
    @Override
    public String getName() {
        return tr("Foot");
    }

    @Override
    public String getTypeIdentifier() {
        return "foot";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.UNMOTORIZED_LAND;
    }

    @Override
    public List<String> getAccessTags() {
        return Arrays.asList("foot", "access");
    }

    @Override
    public boolean mayDefaultAccess(Map<String, String> tags) {
        String highway = tags.get("highway");
        // Those highways won't allow pedestrians
        return highway != null && !Arrays.asList("motorway", "motorway_link", "trunk", "cycleway").contains(highway);
    }

    @Override
    public String getOverpassFilterForPossibleWays() {
        return "(if: is_tag(\"highway\") || is_tag(\"foot\"))";
    }

}
