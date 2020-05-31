package codechicken.enderstorage.config;

import codechicken.enderstorage.EnderStorage;
import codechicken.lib.config.ConfigTag;
import codechicken.lib.config.StandardConfigFile;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.nio.file.Paths;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageConfig {

    private static ConfigTag config;

    public static ItemStack personalItem;
    public static boolean anarchyMode;
    public static int storageSize;

    public static boolean disableCreatorVisuals;
    public static boolean useVanillaEnderChestSounds;

    public static void load() {
        if (config != null) {
            throw new IllegalStateException("Tried to load config more than once.");
        }
        config = new StandardConfigFile(Paths.get("./config/EnderStorage.cfg")).load();
//        ConfigSyncManager.registerSync(new ResourceLocation("enderstorage:config"), config);
        ConfigTag personalItemTag = config.getTag("personalItem")//
                .setComment("The RegistryName for the Item to lock EnderChests and Tanks.")//
                .setDefaultString("minecraft:diamond");
        anarchyMode = config.getTag("anarchyMode")//
                .setComment("Causes chests to lose personal settings and drop the diamond on break.")//
                .setDefaultBoolean(false)//
                .getBoolean();
        storageSize = config.getTag("item_storage_size")//
                .setComment("The size of each inventory of EnderStorage, 0 = 3x3, 1 = 3x9, 2 = 6x9, default = 1")//
                .setDefaultInt(1)//
                .getInt();

        disableCreatorVisuals = config.getTag("disableCreatorVisuals")//
                .setComment("Disables the tank on top of creators heads.")//
                .setDefaultBoolean(false)//
                .getBoolean();
        useVanillaEnderChestSounds = config.getTag("useVanillaEnderChestsSounds")//
                .setComment("Enable this to make EnderStorage use vanilla's EnderChest sounds instead of the standard chest.")//
                .setDefaultBoolean(false)//
                .getBoolean();

        ResourceLocation personalItemName = new ResourceLocation(personalItemTag.getString());
        if (ForgeRegistries.ITEMS.containsKey(personalItemName)) {
            personalItem = new ItemStack(ForgeRegistries.ITEMS.getValue(personalItemName));
        } else {
            EnderStorage.logger.warn("Failed to load PersonaItem '{}', does not exist. Using default.", personalItemName);
            personalItemTag.resetToDefault();
            personalItem = new ItemStack(Items.DIAMOND);
        }
        config.save();
    }
}
