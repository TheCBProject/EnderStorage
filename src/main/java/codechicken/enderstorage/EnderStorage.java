package codechicken.enderstorage;

import codechicken.core.launch.CodeChickenCorePlugin;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.proxy.CommonProxy;
import codechicken.lib.config.ConfigFile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;

import static codechicken.enderstorage.reference.Reference.*;

@Mod(modid = MOD_ID, name = MOD_NAME, version = VERSION, dependencies = DEPENDENCIES, acceptedMinecraftVersions = CodeChickenCorePlugin.mcVersion)
public class EnderStorage {

    @SidedProxy(clientSide = CLIENT_PROXY, serverSide = COMMON_PROXY)
    public static CommonProxy proxy;

    @Mod.Instance(MOD_NAME)
    public static EnderStorage instance;

    public static ConfigFile config;

    public EnderStorage() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        proxy.preInit();
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void preServerStart(FMLServerAboutToStartEvent event) {
        EnderStorageManager.reloadManager(false);
    }

}
