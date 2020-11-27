package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Tagged;

public class OsmPlatformTags {
    public static String KEY_PUBLIC_TRANSPORT = "public_transport";
    public static String KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM = "platform";

    private OsmPlatformTags() {
    }

    public static boolean isPlatform(Tagged primitive) {
        return primitive.hasTag(KEY_PUBLIC_TRANSPORT, KEY_PUBLIC_TRANSPORT_VALUE_PLATFORM);
    }
}
