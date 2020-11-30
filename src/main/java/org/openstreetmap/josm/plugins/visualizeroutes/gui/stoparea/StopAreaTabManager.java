package org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea;

import static org.openstreetmap.josm.tools.I18n.tr;

import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaGroupRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopAreaGroupPanel;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.AbstractTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.ZoomSaver;

public class StopAreaTabManager extends AbstractTabManager<StopAreaGroupPanel> {
    // Used so that zoom gets not lost when re-creating the view due to changes in the relation.
    private final ZoomSaver zoomSaver = new ZoomSaver();

    public StopAreaTabManager(IRelationEditorActionAccess editorAccess) {
        super(editorAccess);
    }

    @Override
    protected AbstractTabManager.TabAndDisplay<StopAreaGroupPanel> getTabToShow(IRelationEditorActionAccess editorAccess) {
        return new TabAndDisplay<StopAreaGroupPanel>() {
            @Override
            public boolean shouldDisplay() {
                return OsmStopAreaGroupRelationTags.isStopAreaGroup(RelationAccess.of(editorAccess));
            }

            @Override
            public StopAreaGroupPanel getTabContent() {
                return new StopAreaGroupPanel(editorAccess, zoomSaver);
            }

            @Override
            public String getTitle() {
                return tr("Areas");
            }
        };
    }

    @Override
    protected void dispose(StopAreaGroupPanel view) {
        view.dispose();
    }
}
