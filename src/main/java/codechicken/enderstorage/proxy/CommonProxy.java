package codechicken.enderstorage.proxy;

import codechicken.enderstorage.handler.EventHandler;
import codechicken.enderstorage.init.EnderStorageRecipe;
import codechicken.enderstorage.init.ModBlocks;
import codechicken.enderstorage.init.ModItems;
import codechicken.enderstorage.init.ModRecipes;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.plugin.EnderItemStoragePlugin;
import codechicken.enderstorage.plugin.EnderLiquidStoragePlugin;
import codechicken.lib.packet.PacketCustom;
import net.minecraftforge.common.MinecraftForge;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class CommonProxy {

    public void preInit() {
        EnderStorageManager.registerPlugin(new EnderItemStoragePlugin());
        EnderStorageManager.registerPlugin(new EnderLiquidStoragePlugin());
        ModBlocks.init();
        ModItems.init();
        //MinecraftForge.EVENT_BUS.register(EnderStorageRecipe.init());
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(new EnderStorageManager.EnderStorageSaveHandler());
        MinecraftForge.EVENT_BUS.register(new TankSynchroniser());
    }

    public void init() {
        PacketCustom.assignHandler(EnderStorageSPH.channel, new EnderStorageSPH());
        ModRecipes.init();
    }

}
