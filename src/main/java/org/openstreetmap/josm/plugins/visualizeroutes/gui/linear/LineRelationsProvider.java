package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;


import org.openstreetmap.josm.data.osm.Relation;

import java.util.List;
import java.util.Optional;

/**
 * Provides the relations for createing a {@link PublicTransportLinePanel}
 */
public interface LineRelationsProvider {
    Optional<Relation> getMasterRelation();

    List<LineRelation> getRelations();
}
