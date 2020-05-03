package codechicken.enderstorage.api;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.lib.config.ConfigTag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;

import java.util.List;

public interface EnderStoragePlugin<T extends AbstractEnderStorage> {

    T createEnderStorage(EnderStorageManager manager, Frequency freq);

    EnderStorageManager.StorageType<T> identifier();

    void sendClientInfo(ServerPlayerEntity player, List<T> list);
}
