package codechicken.enderstorage;

import codechicken.enderstorage.command.EnderStorageCommand;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.proxy.Proxy;
import codechicken.lib.CodeChickenLib;
import codechicken.lib.internal.ModDescriptionEnhancer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartedEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

import static codechicken.enderstorage.EnderStorage.*;
import static codechicken.lib.CodeChickenLib.MC_VERSION;
import static codechicken.lib.CodeChickenLib.MC_VERSION_DEP;

@Mod (modid = MOD_ID, name = MOD_NAME, dependencies = DEPENDENCIES, acceptedMinecraftVersions = MC_VERSION_DEP, certificateFingerprint = "f1850c39b2516232a2108a7bd84d1cb5df93b261", updateJSON = UPDATE_URL)
public class EnderStorage {

    public static final String MOD_ID = "enderstorage";
    public static final String MOD_NAME = "EnderStorage";
    public static final String VERSION = "${mod_version}";
    public static final String DEPENDENCIES = "required-after:codechickenlib@[" + CodeChickenLib.MOD_VERSION + ",)";
    static final String UPDATE_URL = "http://chickenbones.net/Files/notification/version.php?query=forge&version=" + MC_VERSION + "&file=EnderStorage";

    @SidedProxy (clientSide = "codechicken.enderstorage.proxy.ProxyClient", serverSide = "codechicken.enderstorage.proxy.Proxy")
    public static Proxy proxy;

    @Mod.Instance (EnderStorage.MOD_ID)
    public static EnderStorage instance;

    public EnderStorage() {
        instance = this;
    }

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        ConfigurationHandler.init(event.getSuggestedConfigurationFile());
        proxy.preInit();
        ModMetadata metadata = event.getModMetadata();
        metadata.description = modifyDesc(metadata.description);
        ModDescriptionEnhancer.registerEnhancement(MOD_ID, MOD_NAME);
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        proxy.init();
    }

    @Mod.EventHandler
    public void serverStarting(FMLServerStartingEvent event) {
        event.registerServerCommand(new EnderStorageCommand().registerSubCommands());
    }

    @Mod.EventHandler
    public void preServerStart(FMLServerStartedEvent event) {
        EnderStorageManager.reloadManager(false);
    }

    private static String modifyDesc(String desc) {
        desc += "\n";
        desc += "    Credits: Ecu - original idea, design, chest and pouch texture\n";
        desc += "    Rosethorns - tank model\n";
        desc += "    Soaryn - tank texture\n";
        return desc;
    }
}
