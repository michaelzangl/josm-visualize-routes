package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import org.openstreetmap.josm.gui.dialogs.relation.RelationEditor;

import java.awt.*;

public class DialogUtils {
    private DialogUtils() {
    }
    /**
     * Show or focus that relation editor
     * @param editor The editor
     */
    public static void showRelationEditor(RelationEditor editor) {
        if (editor.isVisible()) {
            EventQueue.invokeLater(() -> {
                editor.setAlwaysOnTop(true);
                editor.toFront();
                editor.requestFocus();
                editor.setAlwaysOnTop(false);
            });
        } else {
            editor.setVisible(true);
        }
    }
}
