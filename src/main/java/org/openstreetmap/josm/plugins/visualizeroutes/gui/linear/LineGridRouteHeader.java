package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;


import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.lines.LineRelation;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;
import org.openstreetmap.josm.tools.ColorHelper;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel.safeHtml;

/**
 * A single table header Cell that is displayed on top of each route. Corresponds to a route relation.
 */
public class LineGridRouteHeader extends JPanel {

    public LineGridRouteHeader(LineRelation lineRelation) {
        super(new BorderLayout());

        RelationAccess relation = lineRelation.getRelation();
        String ref = safeHtml(relation.get("ref"));
        String name = safeHtml(relation.get("name"));
        String path = Stream.of(relation.get("from"), relation.get("via"), relation.get("to")).filter(Objects::nonNull).collect(Collectors.joining(" → "));
        UnBoldLabel label = new UnBoldLabel(MessageFormat.format("<html><div><font bgcolor=\"{0}\" color=\"{1}\">{2}</font> {3}</div><div>{4}</div></html>",
            ColorHelper.color2html(lineRelation.getColor()), ColorHelper.color2html(UnBoldLabel.fontColor(lineRelation.getColor())), ref, name, safeHtml(path)));
        if (!lineRelation.isPrimary()) {
            label.setForeground(Color.GRAY);
        }
        label.setBorder(new EmptyBorder(0, 5, 0, 5));
        add(label);
    }

}
