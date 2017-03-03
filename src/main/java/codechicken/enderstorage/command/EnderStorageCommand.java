package codechicken.enderstorage.command;

import codechicken.enderstorage.command.sub.ClearCommand;
import codechicken.enderstorage.command.sub.HelpCommand;
import com.google.common.collect.ImmutableList;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.server.command.CommandTreeBase;

import java.util.List;

/**
 * Created by covers1624 on 18/01/2017.
 */
public class EnderStorageCommand extends CommandTreeBase {

    private HelpCommand helpCommand;

    @Override
    public String getName() {

        return "EnderStorage";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {

        if (args.length < 1) {
            helpCommand.displayHelp(server, sender);
        } else {
            super.execute(server, sender, args);
        }
    }

    @Override
    public List<String> getAliases() {
        //TODO Is "ES" already used by someone?
        return ImmutableList.of("ES", "es", "EnderStorage", "enderstorage");
    }

    @Override
    public String getUsage(ICommandSender sender) {

        return "/" + getName() + " help";
    }

    @Override
    public int getRequiredPermissionLevel() {

        return 0;
    }

    public ICommand registerSubCommands() {

        addSubcommand(helpCommand = new HelpCommand(this).registerHelpPages());
        addSubcommand(new ClearCommand());
        return this;
    }
}
