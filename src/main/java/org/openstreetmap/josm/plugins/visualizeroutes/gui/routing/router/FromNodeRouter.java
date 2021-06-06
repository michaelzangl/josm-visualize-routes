package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.router;

import org.openstreetmap.josm.data.osm.Node;
import org.openstreetmap.josm.data.osm.Way;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.RouteSegmentWay;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.RouteType;
import org.openstreetmap.josm.tools.Pair;

import java.util.Collections;
import java.util.stream.Stream;

/**
 * Router that starts at a node.
 */
public class FromNodeRouter extends AbstractRouter {
    private final Node node;
    private final int insertionIndexInRelation;

    public FromNodeRouter(Node node, int insertionIndexInRelation, RouteType type) {
        super(type);
        this.node = node;
        this.insertionIndexInRelation = insertionIndexInRelation;
    }

    @Override
    protected Stream<Pair<RouteSegmentWay, Double>> getRouterStartSegments() {
        // All ways starting at node
        return node
            .referrers(Way.class)
            .filter(way -> way.isFirstLastNode(node))
            .map(way -> new Pair<>(type.createRouteSegmentWay(way, way.firstNode().equals(node),
                insertionIndexInRelation, Collections.emptyList()), 0.0));
    }

    @Override
    protected Node getRoutingStartNode() {
        return node;
    }

    @Override
    public int getIndexInMembersToAddAfter() {
        return insertionIndexInRelation;
    }
}
