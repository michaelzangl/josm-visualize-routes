package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import org.openstreetmap.josm.actions.relation.DownloadMembersAction;
import org.openstreetmap.josm.gui.dialogs.relation.DownloadRelationMemberTask;
import org.openstreetmap.josm.gui.dialogs.relation.DownloadRelationTask;
import org.openstreetmap.josm.gui.dialogs.relation.actions.IRelationEditorActionAccess;
import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.MessageFormat;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class IncompleteMembersWarningPanel extends JPanel {
    public IncompleteMembersWarningPanel(IRelationEditorActionAccess editor) {
        super(new BorderLayout());
        setForeground(new Color(0xAA0000));
        setBorder(new CompoundBorder(
            new LineBorder(getForeground(), 2),
            new EmptyBorder(5, 10, 5, 10)
        ));
        setBackground(new Color(0xFFBABA));
        setOpaque(true);

        add(new UnBoldLabel(MessageFormat.format(
                "<html><p>{0}</p><p>{1}</p></html>",
                tr("This relation contains incomplete (not downloaded) members!"),
                tr("Some features may not be visible on this map."))));

        DownloadMembersAction downloadAction = new DownloadMembersAction();
        downloadAction.setPrimitives(List.of(editor.getEditor().getRelation()));
        add(new JButton(downloadAction), BorderLayout.EAST);
    }
}
