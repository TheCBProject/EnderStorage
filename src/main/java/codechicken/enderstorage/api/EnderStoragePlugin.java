package codechicken.enderstorage.api;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.config.ConfigTag;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public interface EnderStoragePlugin {
    AbstractEnderStorage createEnderStorage(EnderStorageManager manager, String owner, int freq);

    String identifier();

    void sendClientInfo(EntityPlayer player, List<AbstractEnderStorage> list);

    void loadConfig(ConfigTag tag);
}
