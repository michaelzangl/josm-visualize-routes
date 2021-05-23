package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import org.openstreetmap.josm.data.osm.OsmPrimitive;
import org.openstreetmap.josm.data.osm.OsmPrimitiveType;
import org.openstreetmap.josm.data.osm.Relation;
import org.openstreetmap.josm.data.osm.RelationMember;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteMasterRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;

import java.awt.*;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * Adds or removes the tab for the linear relation
 */
public class LineRelationTabManager extends AbstractTabManager<PublicTransportLinePanel> {
    public LineRelationTabManager(IRelationEditorActionAccess editorAccess) {
        super(editorAccess);
    }

    @Override
    protected TabAndDisplay<PublicTransportLinePanel> getTabToShow(IRelationEditorActionAccess editorAccess) {
        RelationAccess tags = RelationAccess.of(editorAccess);
        if (OsmRouteRelationTags.isRoute(tags)
            && OsmRouteRelationTags.KEY_ROUTE_VALUES_PUBLIC_TRANSPORT.contains(tags.get(OsmRouteRelationTags.KEY_ROUTE))) {
            Relation snapshot = editorAccess.getEditor().getRelation();
            Optional<Relation> master = Optional.ofNullable(snapshot)
                .flatMap(r -> r.getReferrers().stream().filter(PublicTransportLinePanel::isRouteMaster).map(it -> (Relation) it).findFirst());

            return new TabAndDisplay<PublicTransportLinePanel>() {
                @Override
                public boolean shouldDisplay() {
                    return true;
                }

                @Override
                public PublicTransportLinePanel getTabContent() {
                    return new PublicTransportLinePanel(new LineRelationsProvider() {
                        @Override
                        public List<LineRelation> getRelations() {
                            return master.map(m -> getRouteRelations(m.getMembers())
                                .map(it -> new LineRelation(it.getId() == snapshot.getId() ?
                                    RelationAccess.of(editorAccess) : RelationAccess.of(it), it.getId() == snapshot.getId()))
                                .collect(Collectors.toList()))
                                .orElseGet(() -> Arrays.asList(new LineRelation(RelationAccess.of(editorAccess), true)));
                        }

                        @Override
                        public Component createHeadlinePanel() {
                            return master
                                .map(RelationAccess::of)
                                .<Component>map(RouteMasterHeadlinePanel::new)
                                .orElseGet(() -> new NoRouteMasterHeadline(RelationAccess.of(editorAccess), editorAccess.getEditor().getLayer()));
                        }
                    });
                }

                @Override
                public String getTitle() {
                    return tr("Route");
                }
            };

        } else if (OsmRouteMasterRelationTags.isRouteMaster(tags)) {
            return new TabAndDisplay<PublicTransportLinePanel>() {
                @Override
                public boolean shouldDisplay() {
                    return true;
                }

                @Override
                public PublicTransportLinePanel getTabContent() {
                    return new PublicTransportLinePanel(getLinesForMaster(editorAccess));
                }

                @Override
                public String getTitle() {
                    return tr("Routes");
                }
            };
        } else if (OsmStopAreaRelationTags.isStopArea(tags)) {
            return new TabAndDisplay<PublicTransportLinePanel>() {
                @Override
                public boolean shouldDisplay() {
                    return true;
                }

                @Override
                public PublicTransportLinePanel getTabContent() {
                    return new PublicTransportLinePanel(new LineRelationsProvider() {
                        @Override
                        public List<LineRelation> getRelations() {
                            // All the trams / busses stopping on any of our stops.
                            // TODO: Platforms?
                            List<Relation> routeRelations = getRouteRelations(
                                RelationEditorAccessUtils.streamMembers(editorAccess)
                                .filter(it -> OsmRouteRelationTags.STOP_AND_PLATFORM_ROLES.contains(it.getRole()))
                                .flatMap(it -> it.getMember().getReferrers().stream()))
                                .distinct()
                                .collect(Collectors.toList());
                            List<OsmPrimitive> members = RelationEditorAccessUtils.streamMembers(editorAccess)
                                .map(RelationMember::getMember)
                                .collect(Collectors.toList());
                            // If we have many routes, show fewer stops.
                            int count = routeRelations.size() > 10 ? 1 : routeRelations.size() > 4 ? 3 : 5;
                            return routeRelations
                                .stream()
                                .map(it -> new LineRelationAroundStop(RelationAccess.of(it), true,
                                    members::contains, count))
                                .collect(Collectors.toList());
                        }

                        @Override
                        public Component createHeadlinePanel() {
                            return new StopAreaHeadline(RelationAccess.of(editorAccess));
                        }
                    });
                }

                @Override
                public String getTitle() {
                    return tr("Routes");
                }
            };
        } else {
            return new TabAndDisplay<PublicTransportLinePanel>() {
                @Override
                public boolean shouldDisplay() {
                    return false;
                }

                @Override
                public PublicTransportLinePanel getTabContent() {
                    throw new UnsupportedOperationException("No routes tab");
                }

                @Override
                public String getTitle() {
                    return null;
                }
            };
        }
    }

    /**
     * Lines if we are currently editing the master
     * @param editorAccess
     * @return
     */
    private LineRelationsProvider getLinesForMaster(IRelationEditorActionAccess editorAccess) {
        RelationAccess master = RelationAccess.of(editorAccess);
        return new LineRelationsProvider() {
            @Override
            public List<LineRelation> getRelations() {
                return getRouteRelations(master.getMembers())
                    .map(it -> new LineRelation(RelationAccess.of(it), true))
                    .collect(Collectors.toList());
            }

            @Override
            public Component createHeadlinePanel() {
                return new RouteMasterHeadlinePanel(master);
            }
        };
    }

    private static Stream<Relation> getRouteRelations(List<RelationMember> members) {
        return getRouteRelations(members.stream().map(RelationMember::getMember));
    }

    /**
     * Search for route relations
     * @param primitives The primitives to check if they are PT routes
     * @return All PT routes in the primitives stream.
     */
    private static Stream<Relation> getRouteRelations(Stream<OsmPrimitive> primitives) {
        return primitives
            .filter(it -> it.getType() == OsmPrimitiveType.RELATION)
            .map(it -> (Relation) it)
            .filter(OsmRouteRelationTags::isV2PtRoute);
    }

}
