package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.api.StorageType;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class EnderLiquidStoragePlugin implements EnderStoragePlugin<EnderLiquidStorage> {

    @Override
    public EnderLiquidStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderLiquidStorage(manager, freq);
    }

    @Override
    public StorageType<EnderLiquidStorage> identifier() {
        return EnderLiquidStorage.TYPE;
    }

    @Override
    public void sendClientInfo(ServerPlayer player, List<EnderLiquidStorage> list) {
    }
}
