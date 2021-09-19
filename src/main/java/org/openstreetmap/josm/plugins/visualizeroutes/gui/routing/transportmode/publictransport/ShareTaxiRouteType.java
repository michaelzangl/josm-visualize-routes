package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.publictransport;

import java.util.Arrays;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class ShareTaxiRouteType extends BusRouteType {
    @Override
    public String getName() {
        return tr("Share taxi route");
    }

    @Override
    public String getTypeIdentifier() {
        return "minibus";
    }

    @Override
    public List<String> getAccessTags() {
        return Arrays.asList("psv", "motor_vehicle", "vehicle", "access");
    }

}
