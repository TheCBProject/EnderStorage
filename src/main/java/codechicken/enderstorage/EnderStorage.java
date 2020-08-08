package codechicken.enderstorage;

import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.proxy.Proxy;
import codechicken.enderstorage.proxy.ProxyClient;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;
import net.minecraftforge.fml.event.server.FMLServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

@Mod (MOD_ID)
public class EnderStorage {

    public static final Logger logger = LogManager.getLogger("EnderStorage");

    public static final String MOD_ID = "enderstorage";

    public static Proxy proxy;

    public EnderStorage() {
        proxy = DistExecutor.unsafeRunForDist(() -> ProxyClient::new, () -> Proxy::new);
        FMLJavaModLoadingContext.get().getModEventBus().register(this);
        EnderStorageConfig.load();
        EnderStorageManager.init();
    }

    @SubscribeEvent
    public void onCommonSetup(FMLCommonSetupEvent event) {
        proxy.commonSetup(event);
    }

    @SubscribeEvent
    public void onClientSetup(FMLClientSetupEvent event) {
        proxy.clientSetup(event);
    }

    @SubscribeEvent
    public void onServerSetup(FMLDedicatedServerSetupEvent event) {

    }

    //    @Mod.EventHandler
    //    public void serverStarting(FMLServerStartingEvent event) {
    //        event.registerServerCommand(new EnderStorageCommand());
    //    }

}
