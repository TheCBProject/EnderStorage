package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class EnderLiquidStoragePlugin implements EnderStoragePlugin<EnderLiquidStorage> {

    @Override
    public EnderLiquidStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderLiquidStorage(manager, freq);
    }

    @Override
    public EnderStorageManager.StorageType<EnderLiquidStorage> identifier() {
        return EnderLiquidStorage.TYPE;
    }

    @Override
    public void sendClientInfo(ServerPlayerEntity player, List<EnderLiquidStorage> list) {
    }
}
