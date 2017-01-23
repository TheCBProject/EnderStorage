package codechicken.enderstorage.command.sub;

import codechicken.enderstorage.command.EnderStorageCommand;
import codechicken.enderstorage.command.ICCCommand;
import codechicken.enderstorage.command.help.ColourHelp;
import codechicken.enderstorage.command.help.FrequencyHelp;
import codechicken.enderstorage.command.help.IHelpPage;
import codechicken.enderstorage.command.help.ValidStorageHelp;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 18/01/2017.
 */
public class HelpCommand extends CommandBase {

    private EnderStorageCommand parent;
    private List<IHelpPage> helpPages;

    public HelpCommand(EnderStorageCommand parent) {
        this.parent = parent;
        helpPages = new LinkedList<IHelpPage>();
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @Override
    public String getCommandName() {
        return "help";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "Displays help for EnderStorage commands.";
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            displayHelp(server, sender);
        } else if (args.length == 1) {
            String arg = args[0];
            if (parent.getCommandMap().containsKey(arg)) {
                ICommand subCommand = parent.getCommandMap().get(arg);
                sender.addChatMessage(new TextComponentString(GREEN + "Help for: /EnderStorage " + arg));
                if (subCommand instanceof ICCCommand) {
                    for (String line : ((ICCCommand) subCommand).getHelpLines()) {
                        sender.addChatMessage(new TextComponentString(BLUE + line));
                    }
                } else {
                    sender.addChatMessage(new TextComponentString(BLUE + subCommand.getCommandUsage(sender)));
                }
            } else {
                for (IHelpPage page : helpPages) {
                    if (page.name().equals(arg)) {
                        sender.addChatMessage(new TextComponentString(GREEN + "Displaying Help Page: " + arg));
                        for (String line : page.getHelpText()) {
                            sender.addChatMessage(new TextComponentString(BLUE + line));
                        }
                        return;
                    }
                }
                sender.addChatMessage(new TextComponentString(RED + "No Sub Command or Help Page exists for \"" + arg + "\"!"));
            }

        } else {
            sender.addChatMessage(new TextComponentString(RED + "Too many arguments!"));
        }
    }

    public void displayHelp(MinecraftServer server, ICommandSender sender) {
        sender.addChatMessage(new TextComponentString(TextFormatting.DARK_GREEN + "Available commands for EnderStorage:"));
        sender.addChatMessage(new TextComponentString(TextFormatting.GOLD + "For more info use \"/" + getCommandName() + " help [command]\""));
        for (Entry<String, ICommand> entry : parent.getCommandMap().entrySet()) {
            String prefix = "";
            if (!entry.getValue().checkPermission(server, sender)) {
                prefix = RED.toString();
            }
            sender.addChatMessage(new TextComponentString(prefix + "/EnderStorage " + YELLOW + entry.getValue().getCommandName() + BLUE + " " + getCommandBrief(entry.getValue(), sender)));
        }
        for (IHelpPage page : helpPages) {
            sender.addChatMessage(new TextComponentString("/EnderStorage" + YELLOW + " help " + page.name() + BLUE + " " + page.getBrief()));
        }
    }

    private static String getCommandBrief(ICommand command, ICommandSender sender) {
        if (command instanceof ICCCommand) {
            return ((ICCCommand) command).getBrief();
        } else {
            return command.getCommandUsage(sender);
        }
    }

    public HelpCommand registerHelpPage(IHelpPage page) {
        helpPages.add(page);
        return this;
    }

    public HelpCommand registerHelpPages() {
        registerHelpPage(new FrequencyHelp());
        registerHelpPage(new ValidStorageHelp());
        registerHelpPage(new ColourHelp());
        return this;
    }
}
