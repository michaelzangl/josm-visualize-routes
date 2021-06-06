package org.openstreetmap.josm.plugins.visualizeroutes.gui.stoparea;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.utils.UnBoldLabel;

import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.text.MessageFormat;

import static org.openstreetmap.josm.tools.I18n.tr;

public class IncompleteMembersWarningPanel extends UnBoldLabel {
    public IncompleteMembersWarningPanel() {
        super(MessageFormat.format(
            "<html><p>{0}</p><p>{1}</p></html>",
            tr("This relation contains incomplete (not downloaded) members!"),
            tr("Some features may not be visible on this map.")));
        setForeground(new Color(0xAA0000));
        setBorder(new CompoundBorder(
            new LineBorder(getForeground(), 2),
            new EmptyBorder(5, 10, 5, 10)
        ));
        setBackground(new Color(0xFFBABA));
        setOpaque(true);
    }
}
