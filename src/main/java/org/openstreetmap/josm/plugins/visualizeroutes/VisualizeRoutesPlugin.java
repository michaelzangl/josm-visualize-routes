package org.openstreetmap.josm.plugins.visualizeroutes;

import org.openstreetmap.josm.actions.ExtensionFileFilter;
import org.openstreetmap.josm.gui.MainApplication;
import org.openstreetmap.josm.gui.MainFrame;
import org.openstreetmap.josm.gui.MainMenu;
import org.openstreetmap.josm.gui.MapFrame;
import org.openstreetmap.josm.gui.dialogs.relation.RelationEditorHooks;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.visualizeroutes.actions.stoparea.AddStopAreaAction;
import org.openstreetmap.josm.plugins.visualizeroutes.gtfs.gui.GtfsLayerSettingsPanel;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.LineRelationTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.members.MembersTableEnhancer;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.RoutingTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopAreaTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopVicinityTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.EnhancedRelationEditorAccess;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class VisualizeRoutesPlugin extends Plugin {
    /**
     * Creates the plugin
     *
     * @param info the plugin information describing the plugin.
     */
    public VisualizeRoutesPlugin(PluginInformation info) {
        super(info);
        addTabs();
        JMenu toolsMenu = MainApplication.getMenu().toolsMenu;
        toolsMenu.addSeparator();
        MainMenu.add(toolsMenu, new AddStopAreaAction());

        ExtensionFileFilter.addImporter(new org.openstreetmap.josm.plugins.visualizeroutes.gtfs.GtfsImporter());
    }

    private void addTabs() {
        // Dirty hack, but works
        RelationEditorHooks.addActionsToMembers(editorAccess -> {
            EnhancedRelationEditorAccess enhancedAccess = new EnhancedRelationEditorAccess(editorAccess);
            // We need invoke later here because while this method is called, UI is not filled.
            EventQueue.invokeLater(() -> {
                new LineRelationTabManager(enhancedAccess);
                new StopVicinityTabManager(enhancedAccess);
                new StopAreaTabManager(enhancedAccess);
                new RoutingTabManager(enhancedAccess);
                new MembersTableEnhancer(enhancedAccess);
            });
            // Don't add actions.
            return List.of();
        });
    }

    @Override
    public void mapFrameInitialized(MapFrame oldFrame, MapFrame newFrame) {
        super.mapFrameInitialized(oldFrame, newFrame);
        if (newFrame != null) {
            GtfsLayerSettingsPanel.hackInto(newFrame);
        }
    }
}
