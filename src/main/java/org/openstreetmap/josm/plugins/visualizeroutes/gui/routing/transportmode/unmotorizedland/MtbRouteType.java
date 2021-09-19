package org.openstreetmap.josm.plugins.visualizeroutes.gui.routing.transportmode.unmotorizedland;

import java.util.Arrays;
import java.util.List;

import static org.openstreetmap.josm.tools.I18n.tr;

public class MtbRouteType extends BicycleRouteType {

    @Override
    public String getName() {
        return tr("MTB (Mountain Bike)");
    }

    @Override
    public String getTypeIdentifier() {
        return "mtb";
    }

    @Override
    public List<String> getAccessTags() {
        return Arrays.asList("mtb", "bicycle", "vehicle", "access");
    }

}
