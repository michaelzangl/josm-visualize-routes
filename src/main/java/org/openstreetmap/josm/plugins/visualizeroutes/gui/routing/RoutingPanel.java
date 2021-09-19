package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing;

import org.openstreetmap.josm.actions.JosmAction;
import org.openstreetmap.josm.actions.SplitWayAction;
import org.openstreetmap.josm.data.Bounds;
import org.openstreetmap.josm.data.osm.*;
import org.openstreetmap.josm.gui.ExtendedDialog;
import org.openstreetmap.josm.gui.MapView;
import org.openstreetmap.josm.gui.MapViewState.MapViewPoint;
import org.openstreetmap.josm.gui.draw.MapViewPath;
import org.openstreetmap.josm.gui.layer.MapViewPaintable;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmRouteRelationTags;
import org.openstreetmap.josm.plugins.visualizeroutes.constants.OsmStopPositionTags;
import org.openstreetmap.josm.plugins.visualizeroutes.data.DerivedDataSet;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.RelationAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.router.*;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopVicinityPanel;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.*;
import org.openstreetmap.josm.plugins.visualizeroutes.utils.DownloadUtils;
import org.openstreetmap.josm.tools.ImageProvider;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.List;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.openstreetmap.josm.tools.I18n.tr;

public class RoutingPanel extends AbstractVicinityPanel<RoutingDerivedDataSet> {

    public static final String TAG_PART_OF_ACTIVE_ROUTE = "partOfActiveRoute";
    public static final String TAG_PART_OF_ACTIVE_ROUTE_VALUE_FORWARD = "forward";
    public static final String TAG_PART_OF_ACTIVE_ROUTE_VALUE_BACKWARD = "backward";
    public static final String TAG_PART_OF_ACTIVE_ROUTE_VALUE_MULTIPLE = "multiple";
    public static final String TAG_ACTIVE_RELATION_SEGMENT_STARTS = "activeRelationSegmentStarts";
    public static final String TAG_ACTIVE_RELATION_SEGMENT_ENDS = "activeRelationSegmentEnds";
    public static final String TAG_ACTIVE_RELATION_SEGMENT_VALUE_NORMAL = "normal";
    public static final String TAG_ACTIVE_RELATION_SEGMENT_VALUE_BROKEN = "broken";
    public static final String TAG_ACTIVE_RELATION_STOP_INDEX = "activeRelationStopIndex";
    public static final String TAG_ACTIVE_RELATION_STOP_MISORDERED = "activeRelationStopMisordered";
    public static final String TAG_ACTIVE_RELATION_STOP_TOO_FAR = "activeRelationStopTooFar";
    public static final String TAG_MEMBER_OF_ACTIVE_RELATION = "memberOfActiveRelation";
    public static final String TAG_MEMBER_OF_ACTIVE_RELATION_VALUE_ROUTE = "route";
    public static final String TAG_MEMBER_OF_ACTIVE_RELATION_VALUE_PLATFORM = "platform";
    public static final String TAG_MEMBER_OF_ACTIVE_RELATION_VALUE_STOP = "stop";
    public static final String TAG_MEMBER_OF_ACTIVE_RELATION_VALUE_UNKNOWN = "unknown";
    public static final String ACTIVE_RELATION_STOP_OFFSET = "activeRelationStopOffset";
    private static final int MIN_ROUTING_DISTANCE = 1000;
    private static final int MIN_ROUTING_SEGMENTS = 5;
    private static final Color ROUTER_HIGHLIGHT_ROUTE = new Color(0xFF1A1A);
    private static final Color ROUTER_HIGHLIGHT_ROUTE_SPLIT = new Color(0xEC8181);
    private final static ImageIcon SPLIT_ICON = ImageProvider.get("splitway.svg");

    private RoutingPanelSpecialMode mode;
    private JPanel actionButtonsPanel;

    public RoutingPanel(EnhancedRelationEditorAccess editorAccess, ZoomSaver zoom) {
        super(new RoutingDerivedDataSet(editorAccess), editorAccess, zoom);

        // TODO: overwrite isIncomplete. To actually determine some features, we might need more than just non-incomplete members

        setMode(defaultMode());
    }

    private RoutingPanelSpecialMode defaultMode() {
        return dataSetCopy.getRouteTraverser().getSegments().isEmpty()
            ? new SelectStartPointMode() : new NormalMode();
    }

    @Override
    protected JComponent generateActionButtons() {
        if (actionButtonsPanel == null) {
            // Cannot set in constructor => super calls this to early.
            actionButtonsPanel = new JPanel();
            actionButtonsPanel.setOpaque(false);
        }
        return actionButtonsPanel;
    }

    private void setMode(RoutingPanelSpecialMode newMode) {
        if (this.mode != null) {
            this.mode.exitMode();
            actionButtonsPanel.removeAll();
        }
        this.mode = newMode;
        newMode.createActionButtons().forEach(generateActionButtons()::add);
        reLayoutActionButtons();
        newMode.enterMode();
    }

    @Override
    protected List<String> getStylePath() {
        return Arrays.asList(
            "org/openstreetmap/josm/plugins/visualizeroutes/gui/routing/base.mapcss",
            "org/openstreetmap/josm/plugins/visualizeroutes/gui/routing/bus.mapcss"
        );
    }

    @Override
    protected OsmPrimitive getPrimitiveAt(Point point) {
        return getOsmPrimitiveAt(point, mode.primitiveFilter());
    }

    @Override
    protected void doAction(Point point, OsmPrimitive derivedPrimitive, OsmPrimitive originalPrimitive) {
        mode.doAction(point, derivedPrimitive, originalPrimitive);
    }

    private static <T extends JComponent> T pad(T c) {
        c.setBorder(new EmptyBorder(5, 5, 5, 5));
        return c;
    }

    /**
     * Special modes, like traversing a route
     */
    private interface RoutingPanelSpecialMode {
        void doAction(Point point, OsmPrimitive derivedPrimitive, OsmPrimitive originalPrimitive);

        default void exitMode() {
            // Nop
        }

        default void enterMode() {
            // Nop
        }

        Predicate<OsmPrimitive> primitiveFilter();

        default Iterable<JComponent> createActionButtons() {
            return Collections.emptyList();
        }
    }

    private class NormalMode implements RoutingPanelSpecialMode{
        @Override
        public void doAction(Point point, OsmPrimitive derivedPrimitive, OsmPrimitive originalPrimitive) {
            JPopupMenu menu = new JPopupMenu();

            // Segment starts / ends
            if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_ENDS) || derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_STARTS)) {
                // May have both: Start and end.
                if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_STARTS)) {
                    if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_STARTS, TAG_ACTIVE_RELATION_SEGMENT_VALUE_BROKEN)) {
                        menu.add(pad(new UnBoldLabel(tr("The route is broken before this point"))));
                    } else {
                        menu.add(pad(new UnBoldLabel(tr("The route starts at this point"))));
                    }
                }
                if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_ENDS)) {
                    if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_ENDS, TAG_ACTIVE_RELATION_SEGMENT_VALUE_BROKEN)) {
                        menu.add(pad(new UnBoldLabel(tr("The route is broken after this point"))));
                    } else {
                        menu.add(pad(new UnBoldLabel(tr("The route ends at this point"))));
                    }

                    // currently, we can only route at end of route. And we only do it if end is not ambiguous
                    List<RouteSegmentWay> previousRoute = dataSetCopy.getRouteTraverser()
                        .getSegments()
                        .stream()
                        // We should only continue the last way in a segment
                        // (user should delete part of the route first to replace it)
                        .map(seg -> seg.getWays().get(seg.getWays().size() - 1))
                        .filter(it -> it.lastNode() == originalPrimitive)
                        .collect(Collectors.toList());
                    if (previousRoute.size() == 1) {
                        menu.add(new JMenuItem(new JosmAction(tr("Add next ways (interactive)"), null, null, null, false) {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                setMode(new RoutingMode(new FromSegmentRouter(previousRoute.get(0), dataSetCopy.getRouteTraverser().getType())));
                            }
                        }));
                    } else if (previousRoute.size() > 1) {
                        menu.add(new UnBoldLabel(tr("Multiple route segments end at this point")));
                    }
                }

                menu.add(new JMenuItem(new JosmAction(tr("Download adjacent ways"), null, null, null, false) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        DownloadUtils.downloadUsingOverpass("org/openstreetmap/josm/plugins/visualizeroutes/gui/routing/downloadWaysStartingAtNode.query.txt", line -> line
                            .replace("##SELECTOR##", dataSetCopy.getRouteTraverser().getType().getOverpassFilterForPossibleWays())
                            .replace("##STARTNODEID##", "" + originalPrimitive.getId()));
                    }
                }));

                menu.add(new JSeparator());
            }

            if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_STOP_INDEX)) {
                Relation area = StopAreaUtils.findContainingStopArea(originalPrimitive);
                String name = area != null ? area.get("name") : originalPrimitive.get("name");
                menu.add(pad(new UnBoldLabel(tr("Stop {0}: {1}",
                    derivedPrimitive.get(TAG_ACTIVE_RELATION_STOP_INDEX), name))));
                if (derivedPrimitive.hasTag(ACTIVE_RELATION_STOP_OFFSET)) {
                    menu.add(pad(new UnBoldLabel(tr("Distance from route start: {0} meters",
                        derivedPrimitive.get(ACTIVE_RELATION_STOP_OFFSET)))));
                }
                if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_STOP_MISORDERED)) {
                    menu.add(pad(new UnBoldLabel(tr("This stop is not in the correct order along the route."))));
                }
                if (derivedPrimitive.hasTag(TAG_ACTIVE_RELATION_STOP_TOO_FAR)) {
                    menu.add(pad(new UnBoldLabel(tr("This stop is too far away from the route. Consider adding a stop_position."))));
                }

                menu.add(new JMenuItem(new JosmAction(tr("Remove this stop from the route"), null, null, null, false) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editorAccess.getMemberTableModel().removeMembersReferringTo(Arrays.asList(originalPrimitive));
                    }
                }));

                menu.add(new JSeparator());
            }

            // Remove from route
            if (derivedPrimitive.hasTag(TAG_PART_OF_ACTIVE_ROUTE)) {
                if (derivedPrimitive.hasTag(TAG_PART_OF_ACTIVE_ROUTE, TAG_PART_OF_ACTIVE_ROUTE_VALUE_MULTIPLE)) {
                    menu.add(pad(new UnBoldLabel(tr("This way is included in the route multiple times"))));
                }

                menu.add(new JMenuItem(new JosmAction(tr("Remove way from route"), null, null, null, false) {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        editorAccess.getMemberTableModel().removeMembersReferringTo(Arrays.asList(originalPrimitive));
                    }
                }));
            }


            menu.add(StopVicinityPanel.getSelectInMainWindow(Collections.singleton(originalPrimitive)));

            menu.show(RoutingPanel.this, point.x, point.y);
        }

        @Override
        public Predicate<OsmPrimitive> primitiveFilter() {
            return primitive -> primitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_ENDS)
                || primitive.hasTag(TAG_ACTIVE_RELATION_SEGMENT_STARTS)
                || primitive.hasTag(TAG_ACTIVE_RELATION_STOP_INDEX)
                || primitive.hasTag(TAG_PART_OF_ACTIVE_ROUTE);
        }
    }

    private class SelectStartPointMode implements RoutingPanelSpecialMode {

        private final MapViewPaintable highlightHoveredNode = (g, mv, bbox) -> {
            // Normal map highlighting won't highlight the nodes, since they are not neccessarely painted.
            // We mark it with a red dot.
            g.setColor(ROUTER_HIGHLIGHT_ROUTE);
            dataSetCopy.getHighlightedPrimitives()
                .stream()
                .map(id -> editorAccess.getEditor().getLayer().getDataSet().getPrimitiveById(id))
                .filter(it -> it instanceof Node)
                .forEach(node -> g.fill(circleArountPoint(mv.getState().getPointFor((Node) node))));
        };

        @Override
        public void enterMode() {
            mapView.addTemporaryLayer(highlightHoveredNode);
        }

        @Override
        public void exitMode() {
            mapView.removeTemporaryLayer(highlightHoveredNode);
        }

        @Override
        public void doAction(Point point, OsmPrimitive derivedPrimitive, OsmPrimitive originalPrimitive) {
            if (originalPrimitive instanceof Node) {
                // Index at which we should start adding our new primitives.
                // Normally, this is after stops/platforms but before the first way.
                // Since we cannot be sure that the relation is ordered propertly, we just search the first way.
                // May be -1 to start at beginning of relation
                List<RelationMember> members = RelationAccess.of(editorAccess).getMembers();
                int startAfterIndex = members
                    .stream()
                    .filter(m -> !OsmRouteRelationTags.STOP_AND_PLATFORM_ROLES.contains(m.getRole()))
                    .findFirst()
                    .map(it -> members.indexOf(it) - 1)
                    .orElse(-1);
                setMode(new RoutingMode(new FromNodeRouter((Node) originalPrimitive,
                    startAfterIndex, dataSetCopy.getRouteTraverser().getType())));
            }
        }

        @Override
        public Predicate<OsmPrimitive> primitiveFilter() {
            return p -> p instanceof Node
                // TODO: Only return a node, if those ways are actually suited for this type of relation.
                && (p.referrers(Way.class).count() > 1
                || p.hasTag(OsmStopPositionTags.KEY_PUBLIC_TRANSPORT, OsmStopPositionTags.KEY_PUBLIC_TRANSPORT_VALUE_STOP_POSITION));
        }

        @Override
        public Iterable<JComponent> createActionButtons() {
            if (dataSetCopy.getRouteTraverser().getSegments().isEmpty()) {
                // No segments => we can only select the start point, do not show cancel button
                return Arrays.asList(
                    createHint(tr("Your relation does not contain any ways. Please select the start node."))
                );
            } else {
                return Arrays.asList(
                    createHint(tr("Please select the start node.")),
                    new JButton(new JosmAction(tr("Cancel"), null, null, null, false) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            setMode(new NormalMode());
                        }
                    })
                );
            }
        }

        private UnBoldLabel createHint(String hint) {
            UnBoldLabel label = new UnBoldLabel(hint);
            label.setForeground(Color.BLACK);
            label.setBackground(Color.WHITE);
            label.setBorder(new LineBorder(Color.WHITE, 3));
            return label;
        }
    }

    private class RoutingMode implements RoutingPanelSpecialMode {

        private final List<RouteTarget> targets;
        private final AbstractRouter router;
        private final Set<OsmPrimitive> targetEnds;
        private final RoutingHintLayer hoverLayer;
        private final List<RouteSplitSuggestion> splitSuggestions;


        private RoutingMode(AbstractRouter router) {
            this.router = router;
            this.targets = router.findRouteTargets(MIN_ROUTING_DISTANCE, MIN_ROUTING_SEGMENTS);
            this.splitSuggestions = router.findRouteSplits();
            this.targetEnds = targets
                .stream()
                .map(RouteTarget::getEnd)
                .collect(Collectors.toSet());

            this.hoverLayer = new RoutingHintLayer();
        }

        private void drawSplitIcon(MapView mv, Graphics2D g, Node node) {
            MapViewPoint pos = mv.getState().getPointFor(node);
            g.setColor(new Color(0x79FFFFFF, true));
            g.fillRect((int) pos.getInViewX() - 8, (int) pos.getInViewY() - 8,
                16, 16);
            g.drawImage(SPLIT_ICON.getImage(),
                (int) pos.getInViewX() - 8, (int) pos.getInViewY() - 8,
                16, 16, null);
        }

        private void drawHighlightCircle(MapView mv, Graphics2D g, Set<Node> activeNodes, OsmPrimitive it) {
            MapViewPoint pos = mv.getState().getPointFor((Node) it);
            Ellipse2D.Double circle = circleArountPoint(pos);
            if (activeNodes.contains(it)) {
                g.fill(circle);
            } else {
                g.draw(circle);
            }
        }

        private List<HighlighterAndAction> findTraces(PrimitiveId target) {
            var candidates = Stream.concat(
                    targets
                            .stream()
                            .filter(it -> it.getEnd().getPrimitiveId().equals(target))
                            .map(TraceHighlighter::new),
                    splitSuggestions
                            .stream()
                            .filter(it -> it.getEndAtNode().getPrimitiveId().equals(target))
                            .map(SplitHighlighter::new)
            ).collect(Collectors.toList());
            if (!candidates.isEmpty()) {
                // Remove the long ones
                var maxLength = candidates.get(0).getLength() + 500;
                candidates = candidates.stream()
                        .filter(it -> it.getLength() <= maxLength)
                        .collect(Collectors.toList());
            }
            return candidates;
        }

        @Override
        public void enterMode() {
            mapView.addTemporaryLayer(hoverLayer);
        }

        @Override
        public void exitMode() {
            mapView.removeTemporaryLayer(hoverLayer);
        }

        @Override
        public Predicate<OsmPrimitive> primitiveFilter() {
            return primitive -> targetEnds.contains(primitive)
                || splitSuggestions.stream().anyMatch(split -> split.getEndAtNode().equals(primitive));
        }

        @Override
        public void doAction(Point point, OsmPrimitive derivedPrimitive, OsmPrimitive originalPrimitive) {
            List<HighlighterAndAction> traces = findTraces(originalPrimitive.getPrimitiveId())
                    .stream()
                    .sorted(Comparator.comparing(HighlighterAndAction::getLength))
                    .collect(Collectors.toList());
            if (traces.size() == 1) {
                traces.get(0).doAction();
            } else if (traces.size() > 1) {
                JPopupMenu menu = new JPopupMenu();
                menu.addPopupMenuListener(new PopupMenuListener() {
                    @Override
                    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                    }
                    @Override
                    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                        hoverLayer.setPopupHighlightedTrace(null);
                    }
                    @Override
                    public void popupMenuCanceled(PopupMenuEvent e) {
                    }
                });
                menu.add(new UnBoldLabel("There are multiple routes to this point"));
                menu.add(new UnBoldLabel("Please select one"));
                traces.forEach(trace -> {
                    var menuItem = new JMenuItem(new JosmAction(trace.getDescription(), null, null, null, false) {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            trace.doAction();
                        }
                    });
                    menuItem.addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {
                            hoverLayer.setPopupHighlightedTrace(trace);
                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            hoverLayer.setPopupHighlightedTrace(null);
                        }
                    });
                    /*menuItem.addMouseListener(new MouseAdapter() {
                        @Override
                        public void mouseEntered(MouseEvent e) {
                            hoverLayer.setPopupHighlightedTrace(trace);
                        }

                        @Override
                        public void mouseExited(MouseEvent e) {
                            hoverLayer.setPopupHighlightedTrace(null);
                        }
                    });*/
                    menu.add(menuItem);
                });
                menu.show(mapView, point.x, point.y);
            }
        }

        @Override
        public Iterable<JComponent> createActionButtons() {
            return Arrays.asList(new JButton(new JosmAction(tr("Done"), null, null, null, false) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    setMode(defaultMode());
                }
            }));
        }

        private class TraceHighlighter implements HighlighterAndAction {
            private final RouteTarget target;

            public TraceHighlighter(RouteTarget target) {
                this.target = target;
            }

            @Override
            public String getDescription() {
                var trace = this.target.getTrace();
                var name = trace.get(trace.size() - 1).getWay().getDisplayName(DefaultNameFormatter.getInstance());
                return tr("Route via {0}", name == null ? '?' : name);
            }

            @Override
            public double getLength() {
                return target.getTrace().stream().mapToDouble(RouteSegmentWay::getLength).sum();
            }

            @Override
            public Stream<Node> paintHighlight(Graphics2D g) {
                // Paint a line
                MapViewPath line = new MapViewPath(mapView);
                target.getTrace().forEach(toDraw -> line.append(toDraw.getWay().getNodes(), false));

                g.setColor(ROUTER_HIGHLIGHT_ROUTE);
                g.draw(line);
                return target
                        .getTrace()
                        .stream()
                        .map(RouteSegmentWay::lastNode);
            }

            @Override
            public void doAction() {
                editorAccess.getMemberTableModel().addMembersAfterIdx(
                        target.getTrace()
                                .stream()
                                .map(RouteSegmentWay::getWay)
                                .collect(Collectors.toList()), router.getIndexInMembersToAddAfter());

                setMode(new RoutingMode(new FromSegmentRouter(
                        target.getTrace().get(target.getTrace().size() - 1), router.getType())));
            }
        }

        private class SplitHighlighter implements HighlighterAndAction{
            private final RouteSplitSuggestion split;

            public SplitHighlighter(RouteSplitSuggestion split) {
                this.split = split;
            }

            @Override
            public String getDescription() {
                return tr("Split way to reach this point");
            }

            @Override
            public double getLength() {
                return split.getStartAtNode().getCoor().greatCircleDistance(split.getEndAtNode().getCoor());
            }

            @Override
            public Stream<Node> paintHighlight(Graphics2D g) {
                // Parts of the way that won't be used after split.
                MapViewPath line = new MapViewPath(mapView);
                line.append(split.getSegmentBefore(), false);
                line.append(split.getSegmentAfter(), false);
                g.setColor(ROUTER_HIGHLIGHT_ROUTE_SPLIT);
                g.draw(line);

                // This is the segment used after split
                MapViewPath lineActive = new MapViewPath(mapView);
                lineActive.append(split.getSegment(), false);
                g.setColor(ROUTER_HIGHLIGHT_ROUTE);
                g.draw(lineActive);

                return Stream.of(split.getEndAtNode());
            }

            @Override
            public void paintHighlightForeground(Graphics2D g) {
                // Split indicators
                Node splitStart = split.getStartAtNode();
                if (!split.getWay().isFirstLastNode(splitStart)) {
                    drawSplitIcon(mapView, g, splitStart);
                }
                Node splitEnd = split.getEndAtNode();
                if (!split.getWay().isFirstLastNode(splitEnd)) {
                    drawSplitIcon(mapView, g, splitEnd);
                }
            }

            @Override
            public void doAction() {
                if (1 == new AskAboutSplitDialog(split).showDialog().getValue()) {
                    HashSet<Node> segment = new HashSet<>(split.getSegment());
                    DataSet ds = split.getWay().getDataSet();
                    Objects.requireNonNull(ds, "ds");
                    Collection<OsmPrimitive> oldSelection = new ArrayList<>(ds.getSelected());

                    // There is no split way method that does not depend on context.
                    // We need to set selection, the action will then use the selected ways.
                    ds.setSelected(Stream.concat(
                            Stream.of(split.getWay()),
                            split.streamSplitNodes()
                    ).collect(Collectors.toList()));
                    SplitWayAction.runOn(ds);

                    // No return value. But all the new split result ways are selected.
                    // We try to find the way that was a result of our split.
                    // Comparing start/end nodes is not enough, since we might have loops
                    List<Way> result = ds.getSelectedWays()
                            .stream()
                            // We try to find the way that was a result of our split.
                            // Comparing start/end nodes is not enough, since we might have loops
                            .filter(it -> new HashSet<>(it.getNodes()).equals(segment))
                            .collect(Collectors.toList());
                    // silently ignore if not found => user is presented with normal route selection and can retry.
                    if (result.size() == 1) {
                        var way = result.get(0);
                        editorAccess.getMemberTableModel().addMembersAfterIdx(
                                Arrays.asList(way),
                                router.getIndexInMembersToAddAfter());

                        setMode(new RoutingMode(new FromSegmentRouter(
                                router.getType().createRouteSegmentWay(
                                        way,
                                        split.getEndAtNode().getPrimitiveId()
                                                .equals(way.lastNode().getPrimitiveId()),
                                        router.getIndexInMembersToAddAfter() + 1,
                                        Collections.emptyList()
                                ),
                                router.getType()
                        )));
                    } else {
                        setMode(defaultMode());
                    }

                    // Restore selection
                    ds.setSelected(oldSelection);
                }
            }
        }

        private class RoutingHintLayer implements MapViewPaintable {
            // Currently highlighted path from open selection popup
            private HighlighterAndAction popupHighlightedTrace;
            private final List<PaintableInvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();

            @Override
            public void paint(Graphics2D g, MapView mv, Bounds bbox) {
                Set<PrimitiveId> hoveredSet = dataSetCopy.getHighlightedPrimitives();
                List<HighlighterAndAction> toPaint = popupHighlightedTrace != null ? Collections.singletonList(popupHighlightedTrace) : hoveredSet.size() == 1 ? RoutingMode.this.findTraces(hoveredSet.iterator().next()) : Collections.emptyList();
                g.setStroke(new BasicStroke(3));

                Set<Node> activeNodes = toPaint.stream().flatMap(it -> it.paintHighlight(g)).collect(Collectors.toSet());

                g.setStroke(new BasicStroke(2));
                // Highlight split targets
                g.setColor(ROUTER_HIGHLIGHT_ROUTE_SPLIT);
                for (RouteSplitSuggestion it : splitSuggestions) {
                    RoutingMode.this.drawHighlightCircle(mv, g, activeNodes, it.getEndAtNode());
                }

                // Highlight all possible routing targets
                g.setColor(ROUTER_HIGHLIGHT_ROUTE);
                for (OsmPrimitive it : targetEnds) {
                    RoutingMode.this.drawHighlightCircle(mv, g, activeNodes, it);
                }

                // Paint split icons over everything else
                toPaint.forEach(it -> it.paintHighlightForeground(g));
            }

            public void setPopupHighlightedTrace(HighlighterAndAction popupHighlightedTrace) {
                this.popupHighlightedTrace = popupHighlightedTrace;
                this.invalidationListeners.forEach(it -> it.paintableInvalidated(new PaintableInvalidationEvent(this)));
            }

            @Override
            public void addInvalidationListener(PaintableInvalidationListener l) {
                this.invalidationListeners.add(l);
            }

            @Override
            public void removeInvalidationListener(PaintableInvalidationListener l) {
                this.invalidationListeners.remove(l);
            }
        }
    }

    private interface HighlighterAndAction {
        /**
         * @return A description to display in a dropdown if there are multiple options
         */
        String getDescription();

        /**
         * @return The distance form the start (=> priority)
         */
        double getLength();

        Stream<Node> paintHighlight(Graphics2D g);

        default void paintHighlightForeground(Graphics2D g) {
        }

        void doAction();
    }

    private static Ellipse2D.Double circleArountPoint(MapViewPoint pos) {
        return new Ellipse2D.Double(pos.getInViewX() - 3, pos.getInViewY() - 3, 7, 7);
    }

    private class AskAboutSplitDialog extends ExtendedDialog {

        public AskAboutSplitDialog(RouteSplitSuggestion split) {
            super(mapView, tr("Split way in main dataset?"),
                tr("Split way"), tr("Abort"));
            setContent(tr("Do you really want to split this way?\nIt will be split in the main dataset. Closing this relation editor will not undo the split."));
            setButtonIcons("splitway", "cancel");
            setCancelButton(2);
            toggleEnable("pt_split_way_ask");
        }
    }
}
