package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaGroupRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;

public class StopAreaUtils {

    private StopAreaUtils() {
    }

    public static Relation findParentStopGroup(Relation stopAreaRelation) {
        return stopAreaRelation == null
                ? null
                : (Relation) stopAreaRelation
                .getReferrers()
                .stream()
                .filter(r -> r instanceof Relation && OsmStopAreaGroupRelationTags.isStopAreaGroup(r))
                .findFirst()
                .orElse(null);
    }


    public static Relation findContainingStopArea(OsmPrimitive primitive) {
        return (Relation) primitive.getReferrers().stream()
                .filter(it -> it instanceof Relation && OsmStopAreaRelationTags.isStopArea(it))
                .findFirst()
                .orElse(null);
    }
}
