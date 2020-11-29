package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.Tagged;

public class OsmPlatformTags {
    public static String KEY_RAILWAY = "railway";
    public static String KEY_HIGHWAY = "highway";
    public static String KEY_PUBLIC_TRANSPORT = "public_transport";
    public static String KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM = "platform";

    private OsmPlatformTags() {
    }

    public static boolean isPlatform(Tagged primitive) {
        return primitive.hasTag(KEY_HIGHWAY, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM)
                || primitive.hasTag(KEY_RAILWAY, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM)
                || primitive.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM);
    }
}
