package codechicken.enderstorage.api;

import codechicken.enderstorage.manager.EnderStorageManager;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public interface EnderStoragePlugin<T extends AbstractEnderStorage> {

    T createEnderStorage(EnderStorageManager manager, Frequency freq);

    EnderStorageManager.StorageType<T> identifier();

    void sendClientInfo(ServerPlayer player, List<T> list);
}
