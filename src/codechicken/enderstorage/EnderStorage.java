package codechicken.enderstorage;

import java.io.File;

import codechicken.core.CommonUtils;
import codechicken.core.launch.CodeChickenCorePlugin;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.common.BlockEnderStorage;
import codechicken.enderstorage.internal.EnderStorageProxy;
import codechicken.enderstorage.storage.EnderItemStoragePlugin;
import codechicken.enderstorage.storage.EnderLiquidStoragePlugin;
import codechicken.enderstorage.storage.item.ItemEnderPouch;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.config.ConfigTag;
import net.minecraft.command.CommandHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerAboutToStartEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = "EnderStorage", dependencies = "required-after:CodeChickenCore@[" + CodeChickenCorePlugin.version + ",)", acceptedMinecraftVersions = CodeChickenCorePlugin.mcVersion)
public class EnderStorage
{
    @SidedProxy(clientSide = "codechicken.enderstorage.internal.EnderStorageClientProxy", serverSide = "codechicken.enderstorage.internal.EnderStorageProxy")
    public static EnderStorageProxy proxy;

    public static ConfigFile config;

    public static BlockEnderStorage blockEnderChest;
    public static ItemEnderPouch itemEnderPouch;

    public static Item personalItem;
    public static boolean disableVanillaEnderChest;
    public static boolean removeVanillaRecipe;
    public static boolean anarchyMode;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        config = new ConfigFile(new File(CommonUtils.getMinecraftDir() + "/config", "EnderStorage.cfg")).setComment("EnderStorage Configuration File\nDeleting any element will restore it to it's default value\nBlock ID's will be automatically generated the first time it's run");

        proxy.preInit();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        loadPersonalItem();
        disableVanillaEnderChest = config.getTag("disable-vanilla").setComment("Set to true to make the vanilla enderchest unplaceable.").getBooleanValue(true);
        removeVanillaRecipe = config.getTag("disable-vanilla_recipe").setComment("Set to true to make the vanilla enderchest uncraftable.").getBooleanValue(false);
        anarchyMode = config.getTag("anarchy-mode").setComment("Causes chests to lose personal settings and drop the diamond on break").getBooleanValue(false);

        EnderStorageManager.loadConfig(config);
        EnderStorageManager.registerPlugin(new EnderItemStoragePlugin());
        EnderStorageManager.registerPlugin(new EnderLiquidStoragePlugin());

        proxy.init();
    }

    private void loadPersonalItem() {
        ConfigTag tag = config.getTag("personalItemID")
                .setComment("The name of the item used to set the chest to personal. Diamond by default");
        String name = tag.getValue("diamond");
//        personalItem = (Item) Item.itemRegistry.getObject(name);
//        if (personalItem == null) {
//            personalItem = Items.diamond;
//            tag.setValue("diamond");
//        }
    }

    @EventHandler
    public void preServerStart(FMLServerAboutToStartEvent event) {
        EnderStorageManager.reloadManager(false);
    }

    @EventHandler
    public void serverStart(FMLServerStartingEvent event) {
//        CommandHandler commandManager = (CommandHandler) event.getServer().getCommandManager();
//        commandManager.registerCommand(new CommandEnderStorage());
    }

    public static ItemStack getPersonalItem() {
        return new ItemStack(personalItem, 1, 0);
    }
}
