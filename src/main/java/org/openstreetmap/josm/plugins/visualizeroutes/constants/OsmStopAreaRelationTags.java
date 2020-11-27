package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.Tagged;

public class OsmStopAreaRelationTags {
    public static final String KEY_TYPE = "type";
    public static final String KEY_TYPE_VALUE_PUBLIC_TRANSPORT = "public_transport";
    public static final String KEY_PUBLIC_TRANSPORT = "public_transport";
    public static final String KEY_PUBLIC_TRANSPORT_VALUE_STOP_AREA = "stop_area";
    public static final String ROLE_STOP = "stop";


    private OsmStopAreaRelationTags() {

    }

    public static boolean isStopArea(Tagged tagged) {
        return tagged.hasTag(KEY_TYPE, KEY_TYPE_VALUE_PUBLIC_TRANSPORT)
                && tagged.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_STOP_AREA);
    }
}
