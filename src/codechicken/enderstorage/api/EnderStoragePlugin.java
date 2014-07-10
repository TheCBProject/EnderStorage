package codechicken.enderstorage.api;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import codechicken.lib.config.ConfigTag;

public interface EnderStoragePlugin
{
    public AbstractEnderStorage createEnderStorage(EnderStorageManager manager, String owner, int freq);

    public String identifer();

    public void sendClientInfo(EntityPlayer player, List<AbstractEnderStorage> list);

    public void loadConfig(ConfigTag tag);
}
