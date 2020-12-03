package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing;

import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.AbstractTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.ZoomSaver;

import static org.openstreetmap.josm.tools.I18n.tr;

public class RoutingTabManager extends AbstractTabManager<RoutingPanel> {
    private final ZoomSaver zoomSaver = new ZoomSaver();

    public RoutingTabManager(IRelationEditorActionAccess editorAccess) {
        super(editorAccess);
    }

    @Override
    protected TabAndDisplay<RoutingPanel> getTabToShow(IRelationEditorActionAccess editorAccess) {
        return new TabAndDisplay<RoutingPanel>() {
            @Override
            public boolean shouldDisplay() {
                return RelationAccess.of(editorAccess).hasTag(OsmRouteRelationTags.KEY_TYPE, OsmRouteRelationTags.KEY_TYPE_VALUE_ROUTE);
            }

            @Override
            public RoutingPanel getTabContent() {
                return new RoutingPanel(editorAccess, zoomSaver);
            }

            @Override
            public String getTitle() {
                return tr("Map");
            }
        };
    }
}
