package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.lines;

import java.util.Objects;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmPlatformTags;

public class LineRefKeyPlatform implements LineRefKey {
    private final OsmPrimitive platform;

    public LineRefKeyPlatform(OsmPrimitive platform) {
        this.platform = platform;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LineRefKeyPlatform that = (LineRefKeyPlatform) o;
        return Objects.equals(platform, that.platform);
    }

    @Override
    public int hashCode() {
        return Objects.hash(platform);
    }


    @Override
    public String toString() {
        return "LineRefKeyPlatform{" +
            "platform=" + platform +
            '}';
    }

    @Override
    public String getRef() {
        String ref = platform.get(OsmPlatformTags.KEY_REF);
        return ref == null ? "" : ref;
    }
}
