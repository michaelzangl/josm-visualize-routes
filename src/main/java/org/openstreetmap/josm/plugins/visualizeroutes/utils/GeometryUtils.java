package org.openstreetmap.josm.plugins.visualizeroutes.utils;

import org.openstreetmap.josm.data.ProjectionBounds;
import org.openstreetmap.josm.data.coor.EastNorth;
import org.openstreetmap.josm.data.coor.LatLon;
import org.openstreetmap.josm.data.osm.BBox;
import org.openstreetmap.josm.data.projection.Projection;
import org.openstreetmap.josm.data.projection.ProjectionRegistry;

import java.util.Objects;

import static org.openstreetmap.josm.data.projection.Ellipsoid.WGS84;


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

    /**
     * Increases the size by approx. meters → only for small bboxes.
     * @param bbox The bbox
     * @param meters How many meters to add
     * @return A bigger bbox
     */
    public static BBox increaseSize(BBox bbox, double meters) {
        LatLon center = bbox.getCenter();
        if (center == null || !center.isValid()) {
            return bbox;
        } else {
            double dLat = meters / (WGS84.a * Math.PI * 2) * 360;
            double radius = WGS84.a * Math.cos(center.lat());
            double dLon = meters / (radius * Math.PI * 2) * 360;
            return new BBox(
                    bbox.getTopLeftLon() - dLon,
                    bbox.getTopLeftLat() - dLat,
                    bbox.getBottomRightLon() + dLon,
                    bbox.getBottomRightLat() + dLon
            );
        }
    }
}
