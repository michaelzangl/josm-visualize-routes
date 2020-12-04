package org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.pt_assistant.data.DerivedDataSet;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;

public class AreaGroupDerivedDataSet extends DerivedDataSet {
    private final IRelationEditorActionAccess editorAccess;

    public AreaGroupDerivedDataSet(IRelationEditorActionAccess editorAccess) {
        super(editorAccess.getEditor().getLayer().getDataSet());
        this.editorAccess = editorAccess;
    }

    @Override
    protected void addAdditionalGeometry(AdditionalGeometryAccess addTo) {
        // We need to add all members of the current relation and flag them with a special tag
        // This is because JOSM cannot handle super relations in MapCSS
        RelationAccess.of(editorAccess)
            .getMembers()
            .stream()
            .map(RelationMember::getMember)
            .forEach(child -> {
                if (child instanceof Relation) {
                    Relation copy = new Relation((Relation) child);
                    copy.put("childOfActiveAreaGroup", "1");
                    addOrGetDerived(copy);
                }
            });
        // No need to add our parent relation => we flagged all children, that should be enough
    }

    @Override
    protected boolean isIncluded(OsmPrimitive primitive) {
        // Members are automatically included recursively
        return primitive instanceof Relation
            && !primitive.getPrimitiveId().equals(editorAccess.getEditor().getRelation())
            && (OsmStopAreaRelationTags.isStopArea((Relation) primitive) || OsmRouteRelationTags.isV2PtRoute((Relation) primitive));
    }
}
