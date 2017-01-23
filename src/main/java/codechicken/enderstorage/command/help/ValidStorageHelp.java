package codechicken.enderstorage.command.help;

import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.manager.EnderStorageManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by covers1624 on 18/01/2017.
 */
public class ValidStorageHelp implements IHelpPage {
    @Override
    public String name() {
        return "validStorage";
    }

    @Override
    public String getBrief() {
        return "Displays the valid key words for systems managed by EnderStorage.";
    }

    @Override
    public List<String> getHelpText() {
        List<String> list = new ArrayList<String>();
        list.add("This directly references what plugins are installed to EnderStorage");
        list.add("\"*\" is a valid keyword and defines and essentially means All Plugins.");
        list.add("Valid keywords:");
        for (Entry<String, EnderStoragePlugin> entry : EnderStorageManager.getPlugins().entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }
}
