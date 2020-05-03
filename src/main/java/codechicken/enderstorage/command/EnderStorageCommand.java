//package codechicken.enderstorage.command;
//
//import codechicken.enderstorage.command.help.ColourHelp;
//import codechicken.enderstorage.command.help.FrequencyHelp;
//import codechicken.enderstorage.command.help.ValidStorageHelp;
//import codechicken.lib.command.help.HelpCommandBase;
//import codechicken.lib.command.help.IHelpCommandHost;
//import com.google.common.collect.ImmutableList;
//import net.minecraft.command.CommandException;
//import net.minecraft.command.ICommand;
//import net.minecraft.command.ICommandSender;
//import net.minecraft.server.MinecraftServer;
//import net.minecraftforge.server.command.CommandTreeBase;
//
//import java.util.List;
//import java.util.Map;
//
///**
// * Created by covers1624 on 18/01/2017.
// */
//public class EnderStorageCommand extends CommandTreeBase implements IHelpCommandHost {
//
//    private HelpCommandBase helpCommand;
//
//    public EnderStorageCommand() {
//        helpCommand = new HelpCommandBase(this);
//        addSubcommand(helpCommand);
//        addSubcommand(new ClearCommand());
//        helpCommand.addHelpPage(new ColourHelp());
//        helpCommand.addHelpPage(new FrequencyHelp());
//        helpCommand.addHelpPage(new ValidStorageHelp());
//    }
//
//    @Override
//    public String getName() {
//        return "EnderStorage";
//    }
//
//    @Override
//    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
//        if (args.length < 1) {
//            helpCommand.displayHelp(server, sender);
//        } else {
//            super.execute(server, sender, args);
//        }
//    }
//
//    @Override
//    public List<String> getAliases() {
//        //TODO Is "ES" already used by someone?
//        return ImmutableList.of("ES", "es", "EnderStorage", "enderstorage");
//    }
//
//    @Override
//    public String getUsage(ICommandSender sender) {
//        return "/" + getName() + " help";
//    }
//
//    @Override
//    public int getRequiredPermissionLevel() {
//        return 0;
//    }
//
//    @Override
//    public Map<String, ICommand> getSubCommandMap() {
//        return getCommandMap();
//    }
//
//    @Override
//    public String getParentName() {
//        return getName();
//    }
//}
