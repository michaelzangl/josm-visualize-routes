package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Tagged;

public class OsmPlatformTags {
    public static final String KEY_REF = "ref";
    public static String KEY_RAILWAY = "railway";
    public static String KEY_HIGHWAY = "highway";
    public static String KEY_PUBLIC_TRANSPORT = "public_transport";
    public static String KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM = "platform";
    public static String KEY_PUBLIC_TRANSPORT_VALUE_STOP_POSITION = "stop_position";

    private OsmPlatformTags() {
    }

    public static boolean isPlatform(Tagged primitive) {
        return primitive.hasTag(KEY_HIGHWAY, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM)
                || primitive.hasTag(KEY_RAILWAY, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM)
                || primitive.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM);
    }

    public static boolean isStopPosition(OsmPrimitive primitive) {
        return primitive.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_STOP_POSITION);
    }
}
