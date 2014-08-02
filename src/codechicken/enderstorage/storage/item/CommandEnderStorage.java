package codechicken.enderstorage.storage.item;

import codechicken.core.commands.PlayerCommand;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.common.EnderStorageRecipe;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.StatCollector;
import net.minecraft.world.WorldServer;

public class CommandEnderStorage extends PlayerCommand
{
    @Override
    public String getCommandName() {
        return "enderstorage";
    }

    @Override
    public String getCommandUsage(ICommandSender var1) {
        return StatCollector.translateToLocal("enderstorage.command.usage");
    }

    @Override
    public void handleCommand(WorldServer world, EntityPlayerMP player, String[] args) {
        WCommandSender wrapped = new WCommandSender(player);

        if (args.length < 1 || args.length > 4) {
            wrapped.chatT("enderstorage.command.no_arguments");
            return;
        }

        int freq;
        if (args.length <= 2) {
            try {
                freq = Integer.parseInt(args[0]);
            } catch (NumberFormatException nfe) {
                wrapped.chatT("enderstorage.command.no_freq");
                return;
            }

            if (freq < 0 || freq >= 4096) {
                wrapped.chatT("enderstorage.command.no_freq");
                return;
            }
        } else {
            int[] colours = new int[3];
            for (int i = 0; i < 3; i++) {
                int d = EnderStorageRecipe.getDyeColour(args[i]);
                if (d < 0) {
                    wrapped.chatT("enderstorage.command.no_colour", args[i]);
                    return;
                }
                colours[i] = d;
            }
            freq = EnderStorageManager.getFreqFromColours(colours);
        }

        String owner = args.length % 2 == 1 ? "global" : args[args.length - 1];

        ((EnderItemStorage) EnderStorageManager.instance(world.isRemote)
                .getStorage(owner, freq, "item"))
                .openSMPGui(player, "enderstorage.serverop");
    }

    @Override
    public void printHelp(WCommandSender listener) {
        listener.chatT("enderstorage.command.usage");
    }

    @Override
    public boolean OPOnly() {
        return true;
    }

    @Override
    public int minimumParameters() {
        return 0;
    }
}
