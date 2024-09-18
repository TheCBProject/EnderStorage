package codechicken.enderstorage.plugin;

import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.api.StorageType;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.enderstorage.storage.EnderItemStorage;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class EnderItemStoragePlugin implements EnderStoragePlugin<EnderItemStorage> {

    @Override
    public EnderItemStorage createEnderStorage(EnderStorageManager manager, Frequency freq) {
        return new EnderItemStorage(manager, freq);
    }

    @Override
    public StorageType<EnderItemStorage> identifier() {
        return EnderItemStorage.TYPE;
    }

    @Override
    public void sendClientInfo(ServerPlayer player, List<EnderItemStorage> list) {
        for (EnderItemStorage inv : list) {
            if (inv.openCount() > 0) {
                EnderStorageSPH.sendOpenUpdateTo(player, inv.freq, true);
            }
        }
    }
}
