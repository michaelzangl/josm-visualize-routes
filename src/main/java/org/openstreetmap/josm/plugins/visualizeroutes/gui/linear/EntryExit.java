package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;

enum EntryExit {
    ENTRY,
    EXIT,
    BOTH;

    public static EntryExit ofRole(String role) {
        if (role.equals(OsmRouteRelationTags.ROLE_STOP_ENTRY_ONLY) || role.equals(OsmRouteRelationTags.ROLE_PLATFORM_ENTRY_ONLY)) {
            return ENTRY;
        } else if (role.equals(OsmRouteRelationTags.ROLE_STOP_EXIT_ONLY) || role.equals(OsmRouteRelationTags.ROLE_PLATFORM_EXIT_ONLY)) {
            return EXIT;
        } else {
            return BOTH;
        }
    }
}
