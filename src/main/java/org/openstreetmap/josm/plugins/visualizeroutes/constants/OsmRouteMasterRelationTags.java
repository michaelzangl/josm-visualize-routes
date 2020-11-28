package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Tagged;

public class OsmRouteMasterRelationTags {
    public static final String KEY_TYPE = "type";
    public static final String KEY_TYPE_VALUE_ROUTE_MASTER = "route_master";


    public static boolean isRouteMaster(Tagged relation) {
        return relation.hasTag(KEY_TYPE, KEY_TYPE_VALUE_ROUTE_MASTER);
    }

    private OsmRouteMasterRelationTags() {
    }
}
