package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteCategory;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.openstreetmap.josm.tools.I18n.tr;

public class HorseRouteType implements RouteType {
    @Override
    public String getName() {
        return tr("Horse");
    }

    @Override
    public String getTypeIdentifier() {
        return "horse";
    }

    @Override
    public RouteCategory getCategory() {
        return RouteCategory.UNMOTORIZED_LAND;
    }

    @Override
    public List<String> getAccessTags() {
        return Arrays.asList("horse", "access");
    }

    @Override
    public boolean mayDefaultAccess(Map<String, String> tags) {
        String highway = tags.get("highway");
        return highway != null && !Arrays.asList("motorway", "motorway_link", "trunk", "cycleway", "footway").contains(highway);
    }

    @Override
    public String getOverpassFilterForPossibleWays() {
        return "(if: is_tag(\"highway\") || is_tag(\"horse\"))";
    }

}
