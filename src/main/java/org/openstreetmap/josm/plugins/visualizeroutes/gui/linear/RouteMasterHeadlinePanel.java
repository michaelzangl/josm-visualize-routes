package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;
import org.openstreetmap.josm.plugins.visualizeroutes.utils.DownloadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.text.MessageFormat;

import static org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel.safeHtml;
import static org.openstreetmap.josm.tools.I18n.tr;

public class RouteMasterHeadlinePanel extends JPanel {
    public RouteMasterHeadlinePanel(RelationAccess masterRelation) {
        setLayout(new BorderLayout());
        String color = masterRelation.get("colour");
            if (color == null) {
                color = "#888888";
            }
        String headline = MessageFormat.format("<font bgcolor=\"{0}\">{1}</font> {2}", safeHtml(color),
            safeHtml(masterRelation.get("ref")), safeHtml(masterRelation.get("name")));
        String infos = safeHtml(masterRelation.get("operator")) + " " + safeHtml(masterRelation.get("network"));
        String routeMasterText = MessageFormat.format("<html><h2>{0}</h2><div>{1}</div></html>", headline, infos);
        add(new UnBoldLabel(routeMasterText));

        add(new JButton(new JosmAction(tr("Download routes"), "download", null, null, false) {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadUtils.downloadUsingOverpass(
                        "org/openstreetmap/josm/plugins/visualizeroutes/gui/linear/downloadRouteMaster.query.txt",
                    line -> line
                        .replace("##ROUTEIDS##", DownloadUtils.collectMemberIds(masterRelation, OsmPrimitiveType.RELATION))
                        .replace("##MASTERID##", masterRelation.getRelation() != null ? masterRelation.getRelation().getId() + "" : Long.MAX_VALUE + ""));
            }
        }), BorderLayout.EAST);
    }

}
