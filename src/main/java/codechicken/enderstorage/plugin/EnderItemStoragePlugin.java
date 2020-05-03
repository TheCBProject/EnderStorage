package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.enderstorage.storage.EnderItemStorage;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public class EnderItemStoragePlugin implements EnderStoragePlugin<EnderItemStorage> {

    @Override
    public EnderItemStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderItemStorage(manager, freq);
    }

    @Override
    public EnderStorageManager.StorageType<EnderItemStorage> identifier() {
        return EnderItemStorage.TYPE;
    }

    @Override
    public void sendClientInfo(ServerPlayerEntity player, List<EnderItemStorage> list) {
        for (AbstractEnderStorage inv : list) {
            if (((EnderItemStorage) inv).openCount() > 0) {
                EnderStorageSPH.sendOpenUpdateTo(player, inv.freq, true);
            }
        }
    }
}
