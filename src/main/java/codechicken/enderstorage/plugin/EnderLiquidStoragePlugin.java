package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.lib.config.ConfigTag;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class EnderLiquidStoragePlugin implements EnderStoragePlugin {

    @Override
    public AbstractEnderStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderLiquidStorage(manager, freq);
    }

    @Override
    public String identifier() {
        return "liquid";
    }

    public void loadConfig(ConfigTag config) {
    }

    @Override
    public void sendClientInfo(EntityPlayer player, List<AbstractEnderStorage> list) {
    }
}
