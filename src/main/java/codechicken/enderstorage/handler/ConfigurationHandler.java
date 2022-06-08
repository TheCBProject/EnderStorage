package codechicken.enderstorage.handler;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.config.ConfigTag;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

import java.io.File;

/**
 * TODO 1.13, move to new config system, Plugins also get ref to the config, so we can't do it now without binary change.
 * Created by covers1624 on 4/11/2016.
 */
public class ConfigurationHandler {

    private static boolean initialized;

    public static ConfigFile config;

    public static boolean anarchyMode;
    public static boolean disableCreatorVisuals;
    public static boolean perDimensionStorage;
    public static boolean useVanillaEnderChestSounds;
    public static ItemStack personalItem;

    public static void init(File file) {
        if (!initialized) {
            config = new ConfigFile(file).setComment("EnderStorage Configuration File\n" + "Deleting any element will restore it to it's default value");
            initialized = true;
        }
    }

    public static void loadConfig() {
        config.removeTag("clientUpdateCheck");
        config.removeTag("disableVanilla");
        config.removeTag("disableVanillaRecipe");
        anarchyMode = config.getTag("anarchyMode").setComment("Causes chests to lose personal settings and drop the diamond on break").getBooleanValue(false);
        ConfigTag tag = config.getTag("personalItem").setComment("The name of the item used to set the chest to personal. Diamond by default. Format <modid>:<registeredItemName>|<meta>, Meta can be replaced with \"WILD\"");
        //region personalItemParsing
        String name = tag.getValue("minecraft:diamond|0");
        Item item;
        int meta;
        try {
            int pipeIndex = name.lastIndexOf("|");
            item = Item.REGISTRY.getObject(new ResourceLocation(name.substring(0, pipeIndex)));
            if (item == null) {
                throw new Exception("Item does not exist!");
            }
            String metaString = name.substring(pipeIndex + 1);
            if (metaString.equalsIgnoreCase("WILD")) {
                meta = OreDictionary.WILDCARD_VALUE;
            } else {
                meta = Integer.parseInt(metaString);
            }
        } catch (Exception e) {
            tag.setValue("minecraft:diamond|0");
            LogHelper.log(Level.ERROR, e, "Unable to parse Personal item config entry, Resetting to default.");
            item = Items.DIAMOND;
            meta = 0;
        }
        personalItem = new ItemStack(item, 1, meta);
        //endregion
        perDimensionStorage = config.getTag("perDimensionStorage").setComment("Makes storage connection with the same color only possible inside a dimension. No cross dimension storage.").getBooleanValue(false);
        disableCreatorVisuals = config.getTag("disableCreatorVisuals").setComment("Disables the tank on top of the creators heads.").getBooleanValue(false);
        useVanillaEnderChestSounds = config.getTag("useVanillaEnderChestSounds").setComment("Enable this to make EnderStorage use vanilla's EnderChest sounds instead of the standard chest.").getBooleanValue(false);
        EnderStorageManager.loadConfig(config);
    }

}
