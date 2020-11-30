package org.openstreetmap.josm.plugins.visualizeroutes;

import org.openstreetmap.josm.gui.dialogs.relation.RelationEditorHooks;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.LineRelationTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopAreaTabManager;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea.StopVicinityTabManager;

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
        // TODO: MainMenu.add(menu, new AddStopAreaAction());
    }

    private void addTabs() {
        // Dirty hack, but works
        RelationEditorHooks.addActionsToMembers(editorAccess -> {
            // We need invoke later here because while this method is called, UI is not filled.
            EventQueue.invokeLater(() -> {
                new LineRelationTabManager(editorAccess);
                new StopVicinityTabManager(editorAccess);
                new StopAreaTabManager(editorAccess);
            });
            // Don't add actions.
            return List.of();
        });
    }
}
