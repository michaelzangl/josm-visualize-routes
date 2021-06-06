package org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.data.osm.visitor.BoundingXYVisitor;
import org.openstreetmap.josm.data.osm.visitor.OsmPrimitiveVisitor;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapViewState;
import org.openstreetmap.josm.gui.MapViewState.MapViewPoint;
import org.openstreetmap.josm.gui.MapViewState.MapViewRectangle;
import org.openstreetmap.josm.gui.dialogs.relation.MemberTableModel;
import org.openstreetmap.josm.gui.dialogs.relation.RelationEditor;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.gui.draw.MapViewPath;
import org.openstreetmap.josm.gui.layer.LayerManager;
import org.openstreetmap.josm.gui.layer.OsmDataLayer;
import org.openstreetmap.josm.gui.mappaint.Cascade;
import org.openstreetmap.josm.gui.mappaint.MultiCascade;
import org.openstreetmap.josm.plugins.pt_assistant.data.DerivedDataSet;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmPlatformTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaGroupRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopAreaRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationEditorAccessUtils;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.*;
import org.openstreetmap.josm.plugins.visualizeroutes.utils.DownloadUtils;
import org.openstreetmap.josm.tools.Pair;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.text.MessageFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static org.openstreetmap.josm.tools.I18n.tr;

/**
 * Allows to show the vicintiy of a stop area end edit it in a visualized view
 * <p>
 * Supported features:
 * - add / remove platform
 * - add / remove stop_position
 */
public class StopVicinityPanel extends AbstractVicinityPanel {
    public static final String CSS_CLASS_PLATFORM = "platform";
    public static final String CSS_CLASS_EMPTYMEMBER = "emptymember";
    public static final String CSS_CLASS_STOP_POSITION = "stop_position";
    private final IRelationEditorActionAccess editorAccess;

    public StopVicinityPanel(IRelationEditorActionAccess editorAccess, ZoomSaver zoomSaver) {
        super(createDataSetWithNewRelation(editorAccess.getEditor().getLayer(), editorAccess.getEditor().getRelation(), editorAccess), zoomSaver);
        this.editorAccess = editorAccess;

        if (RelationAccess.of(editorAccess)
                .getMembers()
                .stream()
                .anyMatch(it -> it.getMember().isIncomplete())) {
            UnBoldLabel warnPanel = new UnBoldLabel(MessageFormat.format(
                "<html><p>{0}</p><p>{1}</p></html>",
                tr("This relation contains incomplete (not downloaded) members!"),
                tr("Some features may not be visible on this map.")));
            warnPanel.setForeground(new Color(0xAA0000));
            warnPanel.setBorder(new CompoundBorder(
                new LineBorder(warnPanel.getForeground(), 2),
                new EmptyBorder(5, 10, 5, 10)
            ));
            warnPanel.setBackground(new Color(0xFFBABA));
            warnPanel.setOpaque(true);
            //warnPanel.setSize(warnPanel.getMinimumSize());
            //warnPanel.setLocation(10, 40);
            add(warnPanel, BorderLayout.NORTH);
        }

        UnBoldLabel legend = new UnBoldLabel(
            tr("blue: area relation member")
            + " "
            + tr("red: invalid relation member")
            + " "
            + (StopAreaUtils.findParentStopGroup(editorAccess.getEditor().getRelation()) != null
                ?  tr("green: objects belonging to the same area group") : "")
        );
        legend.setBorder(new EmptyBorder(5, 5, 5, 5));
        add(legend, BorderLayout.SOUTH);
    }

    private static DerivedDataSet createDataSetWithNewRelation(OsmDataLayer layer, Relation stopRelation,
                                                               IRelationEditorActionAccess editorAccess) {
        long editedRelationId = stopRelation == null ? 0 : stopRelation.getId();
        BBox bBox = new BBox();
        RelationEditorAccessUtils.streamMembers(editorAccess)
            // Extra space: Something around 200.500m depending on where we are on the map.
            .forEach(p -> bBox.addPrimitive(p.getMember(), 0.005));

        return new DerivedDataSet(layer.getDataSet()) {
            @Override
            protected boolean isIncluded(OsmPrimitive primitive) {
                return primitive.getType() != OsmPrimitiveType.RELATION
                    // Normal primitives: all in bbox
                    ? bBox.intersects(primitive.getBBox())
                    // Relations: all except the one we edit
                    // todo: restrict this, e.g. only PT relations + multipolygons in bbox
                    : primitive.getId() != editedRelationId;
            }

            @Override
            protected void addAdditionalGeometry(AdditionalGeometryAccess addTo) {
                // Now apply the relation editor changes
                // Simulate org.openstreetmap.josm.gui.dialogs.relation.actions.SavingAction.applyChanges
                Relation relation = new Relation();
                editorAccess.getTagModel().applyToPrimitive(relation);
                // This is a hack to tag our currently active relation.
                // There is no id selector in MapCSS, so we need a way to uniquely identify our relation
                relation.put("activePtRelation", "1");

                if (stopRelation != null) {
                    addTo.addAsCopy(stopRelation, relation);

                    // Now we search for all sibling relations.
                    // Due to https://josm.openstreetmap.de/ticket/6129#comment:24 we cannot do it in MapCSS
                    stopRelation.getReferrers()
                        .stream()
                        .filter(r -> r instanceof Relation && OsmStopAreaGroupRelationTags.isStopAreaGroup((Relation) r))
                        .flatMap(parent -> ((Relation) parent).getMembers().stream())
                        .map(RelationMember::getMember)
                        .filter(sibling -> sibling != stopRelation)
                        .forEach(sibling -> {
                            Relation copy = new Relation((Relation) sibling);
                            copy.put("siblingOfActive", "1");
                            // This will add the copy with the fake tag.
                            addOrGetDerived(copy);
                        });

                } else {
                    addTo.add(relation);
                }

                RelationEditorAccessUtils.getRelationMembers(editorAccess)
                    .forEach(m -> relation.addMember(addOrGetDerivedMember(m)));
            }
        };
    }


    @Override
    protected JComponent generateActionButtons() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton downloadButton = new JButton(new JosmAction(
            tr("Download vicinity"),
            "download",
            tr("Download data around the current station area."),
            null,
            false
        ) {
            @Override
            public void actionPerformed(ActionEvent e) {
                DownloadUtils.downloadUsingOverpass(
                    "org/openstreetmap/josm/plugins/pt_assistant/gui/linear/downloadStopAreaVicinity.query.txt",
                    line -> line
                        .replace("##NODEIDS##", DownloadUtils.collectMemberIds(RelationAccess.of(editorAccess), OsmPrimitiveType.NODE))
                        .replace("##WAYIDS##", DownloadUtils.collectMemberIds(RelationAccess.of(editorAccess), OsmPrimitiveType.WAY))
                        .replace("##RELATIONIDS##", DownloadUtils.collectMemberIds(RelationAccess.of(editorAccess), OsmPrimitiveType.RELATION)));
            }
        });
        panel.add(downloadButton);
        JButton zoomToButton = new JButton(new JosmAction(
            tr("Zoom to"),
            "dialogs/autoscale/data",
            tr("Zoom to the current station area."),
            null,
            false
        ) {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomToRelation();
            }
        });
        panel.add(zoomToButton);

        return panel;
    }


    private void zoomToRelation() {
        BoundingXYVisitor v = new BoundingXYVisitor();
        RelationAccess.of(editorAccess).getMembers().forEach(
            m -> m.getMember().accept((OsmPrimitiveVisitor) v));
        mapView.zoomTo(v.getBounds());
        mapView.zoomOut();
    }

    @Override
    protected void doInitialZoom() {
        zoomToRelation();
    }

    @Override
    protected String getStylePath() {
        return "org/openstreetmap/josm/plugins/visualizeroutes/gui/stoparea/stopareavicinity.mapcss";
    }

    @Override
    protected void paintComponent(Graphics g) {
        // TODO REMOVE
        super.paintComponent(g);
    }

    @Override
    protected OsmPrimitive getPrimitiveAt(Point point) {
        // Cannot use the mapView methods - they use a global ref to the active layer
        MapViewState state = mapView.getState();
        MapViewPoint center = state.getForView(point.getX(), point.getY());
        BBox bbox = getBoundsAroundMouse(point, state);
        List<Node> nodes = dataSetCopy.getClone().searchNodes(bbox);
        Optional<Node> nearest = nodes.stream()
            .filter(it -> !getAvailableActions(it).isEmpty())
            .min(Comparator.comparing(node -> state.getPointFor(node).distanceToInViewSq(center)));
        if (nearest.isPresent()) {
            return nearest.get();
        } else {
            // No nearest node => search way
            List<Way> ways = dataSetCopy.getClone().searchWays(bbox)
                .stream()
                .filter(it -> !getAvailableActions(it).isEmpty())
                .collect(Collectors.toList());

            Integer snapDistance = MapView.PROP_SNAP_DISTANCE.get();
            return ways.stream()
                .filter(way -> wayAreaContains(way, point))
                .findFirst()
                .orElseGet(() -> ways.stream()
                    .map(way -> new Pair<>(way, distanceSq(way, point)))
                    // Acutally, it is snap way distance, but we don't have that as prop
                    .filter(wd -> wd.b < snapDistance * snapDistance)
                    .min(Comparator.comparing(wd -> wd.b))
                    .map(wd -> wd.a)
                    .orElse(null));
        }
    }

    private double distanceSq(Way way, Point point) {
        double minDistance = Double.POSITIVE_INFINITY;
        for (int i = 0; i < way.getNodesCount() - 1; i++) {
            Point2D pA = mapView.getState().getPointFor(way.getNode(i)).getInView();
            Point2D pB = mapView.getState().getPointFor(way.getNode(i + 1)).getInView();
            double c = pA.distanceSq(pB);
            if (c < 1) {
                continue;
            }

            double a = point.distanceSq(pB);
            double b = point.distanceSq(pA);
            if (a > c || b > c) {
                continue;
            }

            double perDistSq = a - (a - b + c) * (a - b + c) / 4 / c;
            minDistance = Math.min(perDistSq, minDistance);
        }
        return minDistance;
    }

    private boolean wayAreaContains(Way way, Point point) {
        MapViewPath path = new MapViewPath(mapView.getState());
        path.appendClosed(way.getNodes(), false);
        return path.contains(point);
    }

    private BBox getBoundsAroundMouse(Point point, MapViewState state) {
        Integer snapDistance = MapView.PROP_SNAP_DISTANCE.get();
        MapViewRectangle rect = state.getForView(point.getX() - snapDistance, point.getY() - snapDistance)
            .rectTo(state.getForView(point.getX() + snapDistance, point.getY() + snapDistance));
        return rect.getLatLonBoundsBox().toBBox();
    }

    @Override
    protected void doAction(Point point) {
        OsmPrimitive primitive = getPrimitiveAt(point);
        if (primitive != null) {
            OsmPrimitive originalPrimitive = dataSetCopy.findOriginal(primitive);
            if (originalPrimitive != null) {
                List<EStopVicinityAction> actions = getAvailableActions(originalPrimitive);
                if (!actions.isEmpty()) {
                    showActionsMenu(point, originalPrimitive, actions);
                }
            }
        }
    }

    private void showActionsMenu(Point point, OsmPrimitive primitive, List<EStopVicinityAction> actions) {
        ActionsMenu menu = new ActionsMenu(primitive, actions);
        menu.show(mapView, point.x, point.y);
        menu.addPopupMenuListener(new PopupMenuListener() {
            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                dataSetCopy.getClone().setSelected(Collections.singleton(primitive));
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                dataSetCopy.getClone().setSelected(Collections.emptySet());
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
            }
        });
    }

    protected List<EStopVicinityAction> getAvailableActions(OsmPrimitive primitive) {
        for (RelationMember m : RelationAccess.of(editorAccess).getMembers()) {
            if (m.getMember().equals(primitive)) {
                return getAvailableActionsForMember(primitive, m.getRole());
            }
        }
        return getAvailableActionsForNonmember(primitive);
    }

    private List<EStopVicinityAction> getAvailableActionsForMember(OsmPrimitive primitive, String role) {
        ArrayList<EStopVicinityAction> actions = new ArrayList<>();
        actions.add(EStopVicinityAction.REMOVE_FROM_STOP_AREA);

        Cascade defaultLayer = getCascade(primitive);
        if (defaultLayer.containsKey(CSS_CLASS_PLATFORM) && !OsmStopAreaRelationTags.ROLE_PLATFORM.equals(role)) {
            actions.add(EStopVicinityAction.SET_ROLE_PLATFORM);
        }
        if (defaultLayer.containsKey(CSS_CLASS_EMPTYMEMBER) && !"".equals(role)) {
            actions.add(EStopVicinityAction.SET_ROLE_EMPTY);
        }
        if (defaultLayer.containsKey(CSS_CLASS_STOP_POSITION) && !OsmStopAreaRelationTags.ROLE_STOP.equals(role)) {
            actions.add(EStopVicinityAction.SET_ROLE_STOP);
        }
        return actions;
    }

    private Cascade getCascade(OsmPrimitive primitive) {
        MultiCascade mc = new MultiCascade();
        getStyle().apply(mc, primitive, 1, false);
        return mc.getOrCreateCascade("default");
    }

    private List<EStopVicinityAction> getAvailableActionsForNonmember(OsmPrimitive primitive) {
        Relation area = StopAreaUtils.findContainingStopArea(primitive);
        if (area != null && !area.equals(editorAccess.getEditor().getRelation())) {
            // If the item is in a different stop area, we don't allow adding it.
            return Arrays.asList(EStopVicinityAction.OPEN_AREA_RELATION);
        } else {
            Cascade cascade = getCascade(primitive);
            if (cascade.containsKey(CSS_CLASS_STOP_POSITION)) {
                return Arrays.asList(EStopVicinityAction.ADD_STOP_TO_STOP_AREA);
            } else if (cascade.containsKey(CSS_CLASS_PLATFORM)) {
                return Arrays.asList(EStopVicinityAction.ADD_PLATFORM_TO_STOP_AREA);
            } else if (cascade.containsKey(CSS_CLASS_EMPTYMEMBER)) {
                return Arrays.asList(EStopVicinityAction.ADD_EMPTY_TO_STOP_AREA);
            } else {
                return Collections.emptyList();
            }
        }
    }

    private boolean isStopMemberInAnyRoute(OsmPrimitive primitive) {
        return primitive
            .getReferrers()
            .stream()
            .filter(r -> r instanceof Relation)
            .map(r -> (Relation) r)
            .filter(OsmRouteRelationTags::isV2PtRoute)
            .anyMatch(r -> r.getMembers()
                .stream()
                .anyMatch(member -> member.getMember().equals(primitive) && OsmRouteRelationTags.STOP_ROLES.contains(member.getRole())));
    }

    // Enum to make it fast, e.g for getAvailableActions
    enum EStopVicinityAction {
        // Add with stop role
        ADD_STOP_TO_STOP_AREA {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                menu.add(createActionButton(tr("Add this stop to the stop area relation"), () -> {
                    addMember(editorAccess, primitive, OsmStopAreaRelationTags.ROLE_STOP);
                }));
            }
        },
        // Add with platform role
        ADD_PLATFORM_TO_STOP_AREA {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                menu.add(createActionButton(tr("Add this platform to the stop area relation"), () -> {
                    addMember(editorAccess, primitive, OsmStopAreaRelationTags.ROLE_PLATFORM);
                }));
            }
        },

        ADD_EMPTY_TO_STOP_AREA {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                menu.add(createActionButton(tr("Add this element to the stop area relation"), () -> {
                    addMember(editorAccess, primitive, "");
                }));
            }
        },
        // Remove any member
        REMOVE_FROM_STOP_AREA {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                menu.add(createActionButton(tr("Remove from the stop area relation"), () -> {
                    MemberTableModel model = editorAccess.getMemberTableModel();
                    for (int i = 0; i < model.getRowCount(); i++) {
                        if (model.getValue(i).getMember().equals(primitive)) {
                            model.remove(i);
                            break;
                        }
                    }
                }));
            }
        },

        OPEN_AREA_RELATION {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                Relation area = StopAreaUtils.findContainingStopArea(primitive);
                if (area != null) {
                    JPanel panel = new JPanel();
                    panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                    panel.setBorder(new EmptyBorder(5, 5, 5, 5));
                    panel.add(new UnBoldLabel("<html>" + tr("This element belongs to the stop area <i>{0}</i>",
                        UnBoldLabel.safeHtml(area.getName())) + "</html>"));
                    if (Objects.equals(StopAreaUtils.findParentStopGroup(area), StopAreaUtils.findParentStopGroup(editorAccess.getEditor().getRelation()))) {
                        panel.add(new UnBoldLabel(tr("This element belongs to the same stop area group.")));
                    }
                    menu.add(panel);
                    menu.add(createActionButton(tr("Open area relation"), () -> {
                        DialogUtils.showRelationEditor(RelationEditor.getEditor(
                            editorAccess.getEditor().getLayer(),
                            area,
                            area.getMembers()
                                .stream()
                                .filter(it -> it.getMember().equals(primitive))
                                .collect(Collectors.toList())
                        ));
                    }));
                }
            }
        },
        SET_ROLE_PLATFORM {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                addActionButtonForRole(menu, primitive, editorAccess, OsmStopAreaRelationTags.ROLE_PLATFORM);
            }
        },
        SET_ROLE_STOP {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                addActionButtonForRole(menu, primitive, editorAccess, OsmStopAreaRelationTags.ROLE_STOP);
            }
        },
        SET_ROLE_EMPTY {
            @Override
            void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess) {
                addActionButtonForRole(menu, primitive, editorAccess, "");
            }
        };

        private static void addActionButtonForRole(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess, String role) {
            menu.add(createActionButton(tr("Set role to ''{0}''", role), () -> {
                for (int i = 0; i < editorAccess.getMemberTableModel().getRowCount(); i++) {
                    if (editorAccess.getMemberTableModel().getValue(i).getMember().equals(primitive)) {
                        editorAccess.getMemberTableModel().updateRole(new int[] {i}, role);
                    }
                }
            }));
        }

        private static void addMember(IRelationEditorActionAccess editorAccess, OsmPrimitive primitive, String role) {
            MemberTableModel table = editorAccess.getMemberTableModel();
            table.addMembersAtEnd(Collections.singletonList(primitive));
            table.updateRole(new int[]{table.getRowCount() - 1}, role);
        }

        abstract void addActionButtons(JPopupMenu menu, OsmPrimitive primitive, IRelationEditorActionAccess editorAccess);

        private static JMenuItem createActionButton(String text, Runnable action) {
            return new JMenuItem(new AbstractAction(text) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    action.run();
                }
            });
        }

    }

    private class ActionsMenu extends JPopupMenu {
        ActionsMenu(OsmPrimitive forPrimitive, List<EStopVicinityAction> actions) {
            add(new TagsOfPrimitive(forPrimitive));
            add(new JSeparator());
            for (EStopVicinityAction action : actions) {
                action.addActionButtons(this, forPrimitive, editorAccess);
            }
            add(EStopVicinityAction.createActionButton(tr("Select in main window"), () -> {
                MainApplication.getLayerManager().getActiveDataSet()
                    .setSelected(forPrimitive);
            }));
        }
    }
}
