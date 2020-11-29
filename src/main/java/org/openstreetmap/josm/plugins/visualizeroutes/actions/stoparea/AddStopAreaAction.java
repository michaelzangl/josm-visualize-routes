package org.openstreetmap.josm.plugins.visualizeroutes.actions.stoparea;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.event.ActionEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.dialogs.relation.RelationEditor;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmPlatformTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopPositionTags;

public class AddStopAreaAction extends JosmAction {
    private static final List<String> TAGS_TO_COPY = Arrays.asList(
            OsmRouteRelationTags.KEY_NAME,
            OsmRouteRelationTags.KEY_REF,
            OsmRouteRelationTags.KEY_UIC_REF,
            OsmRouteRelationTags.KEY_UIC_NAME,
            OsmRouteRelationTags.KEY_OPERATOR,
            OsmRouteRelationTags.KEY_NETWORK
    );

    public AddStopAreaAction() {
        super(tr("Create stop area"), null, tr("Create a stop area from the selected elements"),
                null, false);
    }

    @Override
    protected void updateEnabledState(Collection<? extends OsmPrimitive> selection) {
        setEnabled(selection.size() > 0
                && selection.stream().noneMatch(it -> null != findContainingStopArea(it)));
    }

    @Override
    protected boolean listenToSelectionChange() {
        return true;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Relation areaRelation = new Relation();
        areaRelation.setModified(true);

        // Tags
        areaRelation.put(OsmStopAreaRelationTags.KEY_TYPE, OsmStopAreaRelationTags.KEY_TYPE_VALUE_PUBLIC_TRANSPORT);
        areaRelation.put(OsmStopAreaRelationTags.KEY_PUBLIC_TRANSPORT, OsmStopAreaRelationTags.KEY_PUBLIC_TRANSPORT_VALUE_STOP_AREA);
        HashSet<String> duplicateDetector = new HashSet<>();
        getLayerManager().getActiveDataSet()
                .getSelected()
                .stream()
                .flatMap(it -> it.getKeys().getTags().stream())
                .distinct()
                .filter(tag -> TAGS_TO_COPY.contains(tag.getKey()))
                .filter(tag -> duplicateDetector.add(tag.getKey()))
                .forEach(areaRelation::put);

        // Members
        getLayerManager().getActiveDataSet()
                .getSelected()
                .stream()
                .map(selected -> new RelationMember(suggestRole(selected), selected))
                .forEach(areaRelation::addMember);

        RelationEditor editor = RelationEditor.getEditor(
                MainApplication.getLayerManager().getEditLayer(),
                areaRelation,
                null /* no selected members */
        );
        editor.setVisible(true);
    }

    private String suggestRole(OsmPrimitive selected) {
        if (OsmPlatformTags.isPlatform(selected)) {
            return OsmStopAreaRelationTags.ROLE_PLATFORM;
        } else if (selected.hasTag("highway", "bus_stop")
                || OsmStopPositionTags.isStopPosition(selected)) {
            return OsmStopAreaRelationTags.ROLE_STOP;
        } else {
            return "";
        }
    }

    public static Relation findContainingStopArea(OsmPrimitive primitive) {
        return (Relation) primitive.getReferrers().stream()
                .filter(it -> it instanceof Relation && OsmStopAreaRelationTags.isStopArea(it))
                .findFirst()
                .orElse(null);
    }

}
