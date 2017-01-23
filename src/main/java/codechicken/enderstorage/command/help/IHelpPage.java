package codechicken.enderstorage.command.help;

import java.util.List;

/**
 * Created by covers1624 on 18/01/2017.
 * Somewhat a command but not really.
 * Has aliases and hold info on how things work.
 */
public interface IHelpPage {

    String name();

    String getBrief();

    List<String> getHelpText();

}
