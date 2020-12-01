package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.lines;

public class LineRefKeyEmpty implements LineRefKey {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LineRefKeyEmpty;
    }

    @Override
    public String getRef() {
        return "";
    }

    @Override
    public int hashCode() {
        return 42;
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
