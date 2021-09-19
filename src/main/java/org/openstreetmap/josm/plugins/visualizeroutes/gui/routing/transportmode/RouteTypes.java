package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode;

import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland.DetourRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland.RailwayRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland.RoadRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.motorizedland.TracksRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.BusRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.FerryRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.LightRailRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.MinibusRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.MonorailRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.ShareTaxiRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.SubwayRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.TrainRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.TramRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport.TrolleybusRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.BicycleRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.FitnessTrailRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.FootRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.HikingRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.HorseRouteType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland.MtbRouteType;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class RouteTypes {
    private static final List<RouteType> TYPES = Arrays.asList(
            new DetourRouteType(),
            new RailwayRouteType(),
            new RoadRouteType(),
            new TracksRouteType(),
            new BusRouteType(),
            new FerryRouteType(),
            new LightRailRouteType(),
            new MinibusRouteType(),
            new MonorailRouteType(),
            new ShareTaxiRouteType(),
            new SubwayRouteType(),
            new TrainRouteType(),
            new TramRouteType(),
            new TrolleybusRouteType(),
            new BicycleRouteType(),
            new FitnessTrailRouteType(),
            new FootRouteType(),
            new HikingRouteType(),
            new HorseRouteType(),
            new MtbRouteType()
    );

    private RouteTypes() {
    }

    public static RouteType getRouteType(RelationAccess relation) {
        return getRouteType(relation.get(OsmRouteRelationTags.KEY_ROUTE));
    }

    public static RouteType getRouteType(String type) {
        return TYPES.stream()
            .filter(it -> it.getTypeIdentifier().equals(type))
            .findFirst()
            .orElse(new UnknownRouteType());
    }
}
