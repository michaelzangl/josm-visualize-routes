package org.openstreetmap.josm.plugins.visualizeroutes.constants;

import org.openstreetmap.josm.data.osm.Tagged;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Tags for the relation
 */
public class OsmRouteRelationTags {
    public static final String KEY_TYPE = "type";
    public static final String KEY_TYPE_VALUE_ROUTE = "route";

    public static final String KEY_ROUTE = "route";
    public static final String KEY_ROUTE_VALUE_BUS = "bus";
    public static final String KEY_ROUTE_VALUE_TROLLEYBUS = "trolleybus";
    public static final String KEY_ROUTE_VALUE_SHARE_TAXI = "share_taxi";
    public static final String KEY_ROUTE_VALUE_TRAM = "tram";
    public static final String KEY_ROUTE_VALUE_LIGHT_RAIL = "light_rail";
    public static final String KEY_ROUTE_VALUE_SUBWAY = "subway";
    public static final String KEY_ROUTE_VALUE_TRAIN = "train";
    public static final Collection<String> KEY_ROUTE_VALUES_PUBLIC_TRANSPORT = Set.of(
            KEY_ROUTE_VALUE_BUS,
            KEY_ROUTE_VALUE_TROLLEYBUS,
            KEY_ROUTE_VALUE_SHARE_TAXI,
            KEY_ROUTE_VALUE_TRAM,
            KEY_ROUTE_VALUE_LIGHT_RAIL,
            KEY_ROUTE_VALUE_SUBWAY,
            KEY_ROUTE_VALUE_TRAIN);


    public static final String KEY_PUBLIC_TRANSPORT_VERSION = "public_transport:version";
    public static final String KEY_PUBLIC_TRANSPORT_VERSION_VALUE_TWO = "2";

    public static final String ROLE_STOP = "stop";
    public static final String ROLE_STOP_ENTRY_ONLY = "stop_entry_only";
    public static final String ROLE_STOP_EXIT_ONLY = "stop_exit_only";
    public static final Collection<String> STOP_ROLES = List.of(ROLE_STOP, ROLE_STOP_ENTRY_ONLY, ROLE_STOP_EXIT_ONLY);
    public static final String ROLE_PLATFORM = "platform";
    public static final String ROLE_PLATFORM_EXIT_ONLY = "platform_exit_only";
    public static final String ROLE_PLATFORM_ENTRY_ONLY = "platform_entry_only";
    public static final Collection<String> PLATFORM_ROLES = List.of(ROLE_PLATFORM, ROLE_PLATFORM_EXIT_ONLY, ROLE_PLATFORM_ENTRY_ONLY);


    public static boolean isV2PtRoute(Tagged tagged) {
        return isRoute(tagged) && tagged.hasTag(KEY_PUBLIC_TRANSPORT_VERSION, KEY_PUBLIC_TRANSPORT_VERSION_VALUE_TWO);
    }

    public static boolean isRoute(Tagged tagged) {
        return tagged.hasTag(KEY_TYPE, KEY_TYPE_VALUE_ROUTE) && tagged.hasTag(KEY_ROUTE);
    }

    private OsmRouteRelationTags() {}
}
