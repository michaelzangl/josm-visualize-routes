package org.openstreetmap.josm.plugins.visualizeroutes.gui.linear;

import org.openstreetmap.josm.plugins.visualizeroutes.gui.linear.lines.LineRefKey;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;

public class LineRefKeyPanel extends JPanel {
    public LineRefKeyPanel(LineRefKey lineRefKey) {
        super(new BorderLayout());
        setBorder(new CompoundBorder(
            new EmptyBorder(3, 3, 3, 3),
            new LineBorder(new Color(0x656565), 2)
        ));

        JLabel label = new JLabel(lineRefKey.getRef());
        label.setHorizontalAlignment(JLabel.CENTER);
        add(label);
    }

    @Override
    public Dimension getMinimumSize() {
        return getPreferredSize();
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(30, 25);
    }
}
