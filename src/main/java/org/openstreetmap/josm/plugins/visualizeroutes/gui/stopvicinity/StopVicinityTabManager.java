package org.openstreetmap.josm.plugins.visualizeroutes.gui.stopvicinity;

import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.AbstractTabManager;

import javax.swing.*;

import static org.openstreetmap.josm.tools.I18n.tr;

public class StopVicinityTabManager extends AbstractTabManager {
    public StopVicinityTabManager(IRelationEditorActionAccess editorAccess) {
        super(editorAccess);
    }

    @Override
    protected TabAndDisplay getTabToShow(Relation relation) {
        return new TabAndDisplay() {
            @Override
            public boolean shouldDisplay() {
                return OsmStopAreaRelationTags.isStopArea(relation);
            }

            @Override
            public JPanel getTabContent() {
                return new StopVicinityPanel(relation);
            }

            @Override
            public String getTitle() {
                return tr("Vicinity");
            }
        };
    }
}
