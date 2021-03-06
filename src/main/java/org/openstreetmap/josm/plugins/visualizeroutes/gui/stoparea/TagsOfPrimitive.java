package org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.TagMap;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;

import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;

public class TagsOfPrimitive extends UnBoldLabel {
    public TagsOfPrimitive(OsmPrimitive p) {
        super(tagsToHtml(p.getKeys()));
        setBorder(new EmptyBorder(5, 5, 5, 5));
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, getWidth(), getHeight());
        super.paintComponent(g);
    }

    private static String tagsToHtml(TagMap keys) {
        StringBuffer str = new StringBuffer("<html>");
        keys.forEach((k, v) -> str.append(MessageFormat.format("<p><b>{0}</b> = {1}</p>",
            safeHtml(k), safeHtml(v))));
        return str.toString();
    }
}
