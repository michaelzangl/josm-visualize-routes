package org.openstreetmap.josm.plugins.visualizeroutes.gui.utils;

import java.awt.*;

import javax.swing.JLabel;

public class UnBoldLabel extends JLabel {
    public UnBoldLabel(String text) {
        super(text);
        setHorizontalAlignment(LEFT);
        setFont(getFont().deriveFont(0));
    }

    @Override
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE, super.getMaximumSize().height);
    }

    public static String safeHtml(String text) {
        return text == null ? "" : text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;");
    }

    public static Color fontColor(Color color) {
        if ((color.getRed() + color.getGreen() + color.getBlue()) / 3 < 128) {
            return Color.WHITE;
        } else {
            return Color.BLACK;
        }
    }
}
