package codechicken.enderstorage.command;

import net.minecraft.command.ICommand;

import java.util.List;

/**
 * Created by covers1624 on 18/01/2017.
 */
public interface ICCCommand extends ICommand {

    String getBrief();

    List<String> getHelpLines();

}
