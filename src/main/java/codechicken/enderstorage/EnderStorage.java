package codechicken.enderstorage;

import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.init.ClientInit;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.plugin.EnderItemStoragePlugin;
import codechicken.enderstorage.plugin.EnderLiquidStoragePlugin;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

@Mod (MOD_ID)
public class EnderStorage {

    public static final String MOD_ID = "enderstorage";

    public EnderStorage() {
        EnderStorageConfig.load();

        EnderStorageModContent.init();
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientInit::init);

        EnderStorageNetwork.init();

        EnderStorageManager.init();
        EnderStorageManager.registerPlugin(new EnderItemStoragePlugin());
        EnderStorageManager.registerPlugin(new EnderLiquidStoragePlugin());

        MinecraftForge.EVENT_BUS.register(new EnderStorageManager.EnderStorageSaveHandler());
        MinecraftForge.EVENT_BUS.register(new TankSynchroniser());
    }

    //    @Mod.EventHandler
    //    public void serverStarting(FMLServerStartingEvent event) {
    //        event.registerServerCommand(new EnderStorageCommand());
    //    }

}
