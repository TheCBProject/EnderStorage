package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.config.ConfigTag;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class EnderItemStoragePlugin implements EnderStoragePlugin {

    public static final int[] sizes = new int[] { 9, 27, 54 };
    public static int configSize;

    @Override
    public AbstractEnderStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderItemStorage(manager, freq);
    }

    @Override
    public String identifier() {
        return "item";
    }

    public void loadConfig(ConfigTag config) {
        configSize = config.getTag("storage-size").setComment("The size of each inventory of EnderStorage. 0 = 3x3, 1 = 3x9, 2 = 6x9").getIntValue(1);
        if (configSize < 0 || configSize > 2) {
            configSize = 1;
        }
    }

    @Override
    public void sendClientInfo(EntityPlayer player, List<AbstractEnderStorage> list) {
        for (AbstractEnderStorage inv : list) {
            if (((EnderItemStorage) inv).openCount() > 0) {
                EnderStorageSPH.sendOpenUpdateTo(player, inv.freq, true);
            }
        }
    }
}
