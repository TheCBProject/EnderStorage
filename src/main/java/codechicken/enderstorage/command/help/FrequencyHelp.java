package codechicken.enderstorage.command.help;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 18/01/2017.
 */
public class FrequencyHelp implements IHelpPage {

    @Override
    public String name() {

        return "frequency";
    }

    @Override
    public String getBrief() {

        return "Shows you how frequency is formatted inside EnderStorage commands.";
    }

    @Override
    public List<String> getHelpText() {

        List<String> list = new ArrayList<>();
        list.add("Frequency for commands is defined as follows:");
        list.add("\"<colour>,<colour>,<colour>\"");
        list.add("Colour must be the name of the colour i.e. \"red\"");
        list.add("Colour can be Upper or Lowercase.");
        return list;
    }
}
