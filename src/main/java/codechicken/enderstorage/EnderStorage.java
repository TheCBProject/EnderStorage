package codechicken.enderstorage;

import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.init.ClientInit;
import codechicken.enderstorage.init.DataGenerators;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.plugin.EnderItemStoragePlugin;
import codechicken.enderstorage.plugin.EnderLiquidStoragePlugin;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

@Mod (MOD_ID)
public class EnderStorage {

    public static final String MOD_ID = "enderstorage";

    public EnderStorage(IEventBus modBus) {
        EnderStorageConfig.load();

        EnderStorageModContent.init(modBus);
        if (FMLEnvironment.dist.isClient()) {
            ClientInit.init(modBus);
        }

        EnderStorageNetwork.init(modBus);

        EnderStorageManager.init();
        EnderStorageManager.registerPlugin(new EnderItemStoragePlugin());
        EnderStorageManager.registerPlugin(new EnderLiquidStoragePlugin());

        NeoForge.EVENT_BUS.register(new EnderStorageManager.EnderStorageSaveHandler());
        NeoForge.EVENT_BUS.register(new TankSynchroniser());

        DataGenerators.init(modBus);
    }

    //    @Mod.EventHandler
    //    public void serverStarting(FMLServerStartingEvent event) {
    //        event.registerServerCommand(new EnderStorageCommand());
    //    }

}
