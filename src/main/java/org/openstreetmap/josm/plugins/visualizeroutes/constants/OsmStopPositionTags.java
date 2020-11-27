package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.OsmPrimitive;

public class OsmStopPositionTags {
    public static String KEY_PUBLIC_TRANSPORT = "public_transport";
    public static String KEY_PUBLIC_TRANSPORT_VALUE_STOP_POSITION = "stop_position";

    private OsmStopPositionTags() {
    }

    public static boolean isStopPosition(OsmPrimitive primitive) {
        return primitive.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_STOP_POSITION);
    }
}
