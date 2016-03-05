package codechicken.enderstorage.storage;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import codechicken.lib.config.ConfigTag;
import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.internal.EnderStorageSPH;
import codechicken.enderstorage.storage.item.EnderItemStorage;

public class EnderItemStoragePlugin implements EnderStoragePlugin
{
    public static final int[] sizes = new int[]{9,27,54};
    public static int configSize;
    
    @Override
    public AbstractEnderStorage createEnderStorage(EnderStorageManager manager, String owner, int freq)
    {
        return new EnderItemStorage(manager, owner, freq);
    }
    
    @Override
    public String identifer()
    {
        return "item";
    }
    
    public void loadConfig(ConfigTag config)
    {
        configSize = config.getTag("storage-size").setComment("The size of each inventory of EnderStorage. 0 = 3x3, 1 = 3x9, 2 = 6x9").getIntValue(1);
        if(configSize < 0 || configSize > 2)
            configSize = 1;
    }
    
    @Override
    public void sendClientInfo(EntityPlayer player, List<AbstractEnderStorage> list)
    {
        for(AbstractEnderStorage inv : list)
            if(((EnderItemStorage)inv).openCount() > 0)
                EnderStorageSPH.sendOpenUpdateTo(player, inv.owner, inv.freq, true);
    }
}
