package org.openstreetmap.josm.plugins.visualizeroutes.gtfs.data;

import org.openstreetmap.josm.data.coor.LatLon;

public interface TripPoint {
    LatLon getPoint();

    double getDistTraveled();
}
