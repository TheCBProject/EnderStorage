package codechicken.enderstorage.internal;

import codechicken.core.GuiModListScroll;
import codechicken.lib.packet.PacketCustom;
import codechicken.core.CCUpdateChecker;
import codechicken.enderstorage.common.ItemEnderStorageRenderer;
import codechicken.enderstorage.storage.item.TileEnderChest;
import codechicken.enderstorage.storage.item.EnderChestRenderer;
import codechicken.enderstorage.storage.liquid.TileEnderTank;
import codechicken.enderstorage.storage.liquid.EnderTankRenderer;
import net.minecraft.item.Item;
import net.minecraftforge.client.MinecraftForgeClient;
import cpw.mods.fml.client.registry.ClientRegistry;

import static codechicken.enderstorage.EnderStorage.*;

public class EnderStorageClientProxy extends EnderStorageProxy
{    
    @Override
    public void init()
    {
        if(config.getTag("checkUpdates").getBooleanValue(true))
            CCUpdateChecker.updateCheck("EnderStorage");

        super.init();

        GuiModListScroll.register("EnderStorage");

        PacketCustom.assignHandler(EnderStorageCPH.channel, new EnderStorageCPH());

        MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(blockEnderChest), new ItemEnderStorageRenderer());

        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderChest.class, new EnderChestRenderer());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderTank.class, new EnderTankRenderer());
    }
    
    public static float getPearlBob(double time)
    {
        return (float) Math.sin(time/25*3.141593) * 0.1F;
    }

    public static int getTimeOffset(int x, int y, int z)
    {
        return x*3+y*5+z*9;
    }
}
