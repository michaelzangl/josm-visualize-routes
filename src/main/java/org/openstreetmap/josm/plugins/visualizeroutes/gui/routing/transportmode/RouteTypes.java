package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode;

import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RouteTypes {
    private static final List<RouteType> TYPES = Arrays.asList(
        new BusRouteType()
    );

    private RouteTypes() {
    }

    public static RouteType getRouteType(RelationAccess relation) {
        return getRouteType(relation.get(OsmRouteRelationTags.KEY_ROUTE));
    }

    public static RouteType getRouteType(String type) {
        return TYPES.stream()
            .filter(it -> it.getTypeIdentifier().equals(type))
            .findFirst()
            .orElse(new UnknownRouteType());
    }
}
