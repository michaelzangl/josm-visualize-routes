package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;
import org.openstreetmap.josm.plugins.visualizeroutes.utils.DownloadUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

import static org.openstreetmap.josm.tools.I18n.tr;

public class StopAreaHeadline extends JPanel {
    public StopAreaHeadline(RelationAccess relation) {
        super(new BorderLayout());
        String name = relation.get("name");
        String title = tr("Routes stopping at {0}", name == null ? "" : name);
        add(new UnBoldLabel("<html><h2>" + UnBoldLabel.safeHtml(title) + "</h2></html>"));

        add(new JButton(new JosmAction(tr("Download routes"), "download", null, null, false) {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadUtils.downloadUsingOverpass(
                        "org/openstreetmap/josm/plugins/visualizeroutes/gui/linear/downloadStopArea.query.txt",
                    line -> line
                        .replace("##NODEIDS##", DownloadUtils.collectMemberIds(relation, OsmPrimitiveType.NODE))
                        .replace("##WAYIDS##", DownloadUtils.collectMemberIds(relation, OsmPrimitiveType.WAY)));
            }
        }), BorderLayout.EAST);
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(super.getMaximumSize().width, super.getPreferredSize().height);
    }
}
