package codechicken.enderstorage.command.sub;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.command.ICCCommand;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.util.ArrayUtils;
import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.TextComponentString;

import javax.annotation.Nullable;
import java.util.*;

import static net.minecraft.util.text.TextFormatting.*;

/**
 * Created by covers1624 on 18/01/2017.
 */
public class ClearCommand extends CommandBase implements ICCCommand {

    @Override
    public String getCommandName() {
        return "clear";
    }

    @Override
    public String getBrief() {
        return "Provides ability to clear a users EnderStorage.";
    }

    @Override
    public List<String> getHelpLines() {
        List<String> lines = new LinkedList<String>();
        lines.add("[] Defines required choice parameters.");
        lines.add("<> Defines optional parameters.");
        lines.add("Syntax: \"/EnderStorage clear [item|liquid|*] [freq|*] <player>\"");
        lines.add("If you don't provide a player it will clear all global Storage's.");
        lines.add("To clear all player Storage's provide \"*\" as the player.");
        lines.add("For frequency syntax use \"/EnderStorage help frequency\"");
        return lines;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return getBrief();
    }

    @Override
    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 2) {
            sender.addChatMessage(new TextComponentString(RED + "Not Enough Arguments!"));
            displayHelpText(sender);
            return;
        }
        String identifier = args[0];
        String frequency = args[1];

        String owner = null;
        if (args.length == 3) {
            owner = args[2];
        }

        Set<String> identifiers = EnderStorageManager.getPlugins().keySet();

        if ("*".equals(identifier)) {
            for (String ident : identifiers) {
                nukeStorage(ident, frequency, owner, sender);
            }
        } else {
            if (identifiers.contains(identifier)) {
                nukeStorage(identifier, frequency, owner, sender);
            } else {
                sender.addChatMessage(new TextComponentString(RED + "Invalid Storage Identifier [" + identifier + "]"));
                sender.addChatMessage(new TextComponentString("For valid Identifiers run, " + YELLOW + "\"/EnderStorage help validStorage\""));
            }
        }
    }

    private static void nukeStorage(String identifier, String frequency, final String owner, ICommandSender sender) throws CommandException {
        LogHelper.info("%s %s %s", identifier, frequency, owner);
        EnderStorageManager manager = EnderStorageManager.instance(false);
        List<String> validKeys = manager.getValidKeys(identifier);
        Predicate<String> frequencyPredicate;
        Predicate<String> ownerPredicate;

        if ("*".equals(frequency)) {
            frequencyPredicate = new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return true;
                }
            };
        } else {
            final String[] split = frequency.split(",");
            if (split != null && split.length == 3) {
                for (String c : split) {//Validate that all colours exist.
                    boolean valid = false;
                    for (EnumColour colour : EnumColour.values()) {
                        if (colour.getName().equalsIgnoreCase(c)) {
                            valid = true;
                        }
                    }
                    if (!valid) {
                        throw new CommandException(c + " is an invalid colour! \"/EnderStorage help colour\"");
                    }
                }
                //If we've come this far the colour is probably valid.
                frequencyPredicate = new Predicate<String>() {
                    @Override
                    public boolean apply(@Nullable String input) {
                        if (Strings.isNullOrEmpty(input)) {
                            return false;
                        }
                        //Assume this is valid as we control what comes in here.
                        Map<String, String> kvArray = ArrayUtils.convertKeyValueArrayToMap(input.split(","));
                        if (kvArray.get("left").equalsIgnoreCase(split[0])) {
                            if (kvArray.get("middle").equalsIgnoreCase(split[1])) {
                                if (kvArray.get("right").equalsIgnoreCase(split[2])) {
                                    return true;
                                }
                            }
                        }
                        return false;
                    }
                };
            } else {
                throw new CommandException("Invalid frequency format! \"<colour>,<colour>,<colour>\"");
            }
        }

        if (owner == null) {
            //No owner, cool, only global then.
            ownerPredicate = new Predicate<String>() {
                @Override
                public boolean apply(@Nullable String input) {
                    return input == null;
                }
            };
        } else {
            if ("*".equals(owner)) {
                //Any owner, not global.
                ownerPredicate = new Predicate<String>() {
                    @Override
                    public boolean apply(@Nullable String input) {
                        return !Strings.isNullOrEmpty(input);
                    }
                };
            } else {
                //Specific owner.
                ownerPredicate = new Predicate<String>() {
                    @Override
                    public boolean apply(@Nullable String input) {
                        return owner.equals(input);
                    }
                };
            }
        }

        boolean noStorage = true;
        List<String> cleared = new ArrayList<String>();
        for (String key : validKeys) {
            LogHelper.info(key);
            Map<String, String> kvArray = ArrayUtils.convertKeyValueArrayToMap(key.split(","));
            if (frequencyPredicate.apply(key)) {
                if (ownerPredicate.apply(kvArray.get("owner"))) {
                    noStorage = false;
                    Frequency freq = Frequency.fromString(kvArray.get("left"), kvArray.get("middle"), kvArray.get("right"), kvArray.get("owner"));
                    AbstractEnderStorage storage = manager.getStorage(freq, identifier);
                    storage.clearStorage();
                    cleared.add(freq.toString());
                }
            }
        }
        if (noStorage) {
            throw new CommandException("No storage's exist for that colour and owner..");
        } else {
            sender.addChatMessage(new TextComponentString(YELLOW + "Successfully cleared " + cleared.size() + " Storage's!"));
            for (String entry : cleared) {
                sender.addChatMessage(new TextComponentString(BLUE + entry));
            }
        }
    }

    private void displayHelpText(ICommandSender sender) {
        for (String line : getHelpLines()) {
            sender.addChatMessage(new TextComponentString(BLUE + line));
        }
    }
}
