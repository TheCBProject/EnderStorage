package codechicken.enderstorage.proxy;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.plugin.EnderItemStoragePlugin;
import codechicken.enderstorage.plugin.EnderLiquidStoragePlugin;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class Proxy {

    public void commonSetup(FMLCommonSetupEvent event) {
        EnderStorageNetwork.init();
        EnderStorageManager.registerPlugin(new EnderItemStoragePlugin());
        EnderStorageManager.registerPlugin(new EnderLiquidStoragePlugin());
        //MinecraftForge.EVENT_BUS.register(EnderStorageRecipe.init());
        MinecraftForge.EVENT_BUS.register(new EnderStorageManager.EnderStorageSaveHandler());
        MinecraftForge.EVENT_BUS.register(new TankSynchroniser());
    }

    public void clientSetup(FMLClientSetupEvent event) {
    }
}
