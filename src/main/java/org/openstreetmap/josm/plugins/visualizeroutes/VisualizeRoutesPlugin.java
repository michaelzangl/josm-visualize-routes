package org.openstreetmap.josm.plugins.visualizeroutes;

import org.openstreetmap.josm.gui.dialogs.relation.RelationEditorHooks;
import org.openstreetmap.josm.gui.dialogs.relation.actions.AbstractRelationEditorAction;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionGroup;
import org.openstreetmap.josm.plugins.Plugin;
import org.openstreetmap.josm.plugins.PluginInformation;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.LineRelationTabManager;

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
    }

    private void addTabs() {
        // Dirty hack, but works
        RelationEditorHooks.addActionsToMembers(new IRelationEditorActionGroup() {
            @Override
            public List<AbstractRelationEditorAction> getActions(IRelationEditorActionAccess editorAccess) {
                EventQueue.invokeLater(() ->
                        new LineRelationTabManager(editorAccess));
                return List.of();
            }
        });
    }
}
