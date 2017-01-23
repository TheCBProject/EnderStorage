package codechicken.enderstorage.command.help;

import codechicken.lib.colour.EnumColour;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 23/01/2017.
 */
public class ColourHelp implements IHelpPage {
    @Override
    public String name() {
        return "colour";
    }

    @Override
    public String getBrief() {
        return "Displays the valid colours used by EnderStorage";
    }

    @Override
    public List<String> getHelpText() {
        List<String> list = new ArrayList<String>();
        list.add("A colour can be one of the following names: ");
        for (EnumColour colour : EnumColour.values()) {
            list.add(colour.getName());
        }
        return list;
    }
}
