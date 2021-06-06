package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.stops;

import static org.openstreetmap.josm.tools.I18n.tr;

import java.awt.Component;
import java.util.Optional;

import org.openstreetmap.josm.actions.AutoScaleAction;
import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.PublicTransportLinePanel;
import org.openstreetmap.josm.tools.ImageProvider;

class FoundStopPosition implements FoundStop {
    private final RelationMember member;

    FoundStopPosition(RelationMember member) {
        if (!OsmRouteRelationTags.STOP_AND_PLATFORM_ROLES.contains(member.getRole())) {
            throw new IllegalArgumentException("Not a stop position: " + member);
        }
        this.member = member;
    }

    Optional<Relation> findStopArea() {
        return member.getMember().getReferrers()
            .stream()
            .filter(it -> it.getType() == OsmPrimitiveType.RELATION
                && OsmStopAreaRelationTags.isStopArea(it)
            )
            .map(it -> (Relation) it)
            .findFirst();
    }

    EntryExit entryExit() {
        return EntryExit.ofRole(member.getRole());
    }

    @Override
    public boolean isIncomplete() {
        return member.getMember().isIncomplete();
    }

    @Override
    public boolean matches(OsmPrimitive p) {
        return this.member.getMember().equals(p);
    }

    @Override
    public String getNameAndInfos() {
        String name = member.getMember().get("name");
        return (name == null ? tr("- Stop without name -") : name);
    }

    @Override
    public Component createActionButtons() {
        return PublicTransportLinePanel.createActions(
            PublicTransportLinePanel.createAction(
                tr("Zoom to stop position"),
                new ImageProvider("mapmode", "zoom"),
                () -> {
                    member.getMember().getDataSet().setSelected(member.getMember());
                    AutoScaleAction.autoScale(AutoScaleAction.AutoScaleMode.SELECTION);
                }
            )
        );
    }

    public RelationMember getMember() {
        return member;
    }
}
