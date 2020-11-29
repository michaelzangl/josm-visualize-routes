package org.openstreetmap.josm.plugins.visualizeroutes.utils;

import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;

import java.util.Objects;


public final class GeometryUtils {
    public static ProjectionBounds enlargeInMeters(ProjectionBounds bounds, int meters) {
        Objects.requireNonNull(bounds, "bounds");
        Projection projection = ProjectionRegistry.getProjection();
        EastNorth center = bounds.getCenter();
        LatLon cLatLon = projection.eastNorth2latlon(center);
        // Meters one unit of east/north is around our position
        double unitEast = cLatLon.greatCircleDistance(projection.eastNorth2latlon(center.add(1, 0)));
        double unitNorth = cLatLon.greatCircleDistance(projection.eastNorth2latlon(center.add(0, 1)));

        double dEast = meters / Math.max(unitEast, 1e-10);
        double dNorth = meters / Math.max(unitNorth, 1e-10);
        EastNorth min = bounds.getMin().add(-dEast, -dNorth);
        EastNorth max = bounds.getMax().add(dEast, dNorth);
        return new ProjectionBounds(min, max);
    }

}
