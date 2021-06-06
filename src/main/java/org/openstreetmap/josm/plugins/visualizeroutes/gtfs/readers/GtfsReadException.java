package org.openstreetmap.josm.plugins.visualizeroutes.gtfs.readers;

public class GtfsReadException extends RuntimeException {
    public GtfsReadException(String fileName, int line, String message) {
        this(fileName, line, message, null);
    }

    public GtfsReadException(String fileName, int line, String message, Throwable e) {
        super(fileName + ":" + line + ": " + message, e);
    }
}
