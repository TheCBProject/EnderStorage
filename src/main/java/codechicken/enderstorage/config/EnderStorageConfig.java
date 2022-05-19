package codechicken.enderstorage.config;

import codechicken.lib.config.ConfigCategory;
import codechicken.lib.config.ConfigFile;
import codechicken.lib.config.ConfigValue;
import com.mojang.logging.LogUtils;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.nio.file.Paths;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final CrashLock LOCK = new CrashLock("Already initialized.");

    private static ConfigCategory config;

    private static ConfigValue personalItemTag;
    @Nullable
    private static ItemStack personalItem;
    public static boolean anarchyMode;
    public static int storageSize;

    public static boolean disableCreatorVisuals;
    public static boolean useVanillaEnderChestSounds;

    public static void load() {
        LOCK.lock();

        config = new ConfigFile(MOD_ID)
                .path(Paths.get("./config/EnderStorage.cfg"))
                .load();
//        ConfigSyncManager.registerSync(new ResourceLocation("enderstorage:config"), config);
        personalItemTag = config.getValue("personalItem")
                .setComment("The RegistryName for the Item to lock EnderChests and Tanks.")
                .setDefaultString("minecraft:diamond");
        anarchyMode = config.getValue("anarchyMode")
                .setComment("Causes chests to lose personal settings and drop the diamond on break.")
                .setDefaultBoolean(false)
                .getBoolean();
        storageSize = config.getValue("item_storage_size")
                .setComment("The size of each inventory of EnderStorage, 0 = 3x3, 1 = 3x9, 2 = 6x9, default = 1")
                .setDefaultInt(1)
                .getInt();

        disableCreatorVisuals = config.getValue("disableCreatorVisuals")
                .setComment("Disables the tank on top of creators heads.")
                .setDefaultBoolean(false)
                .getBoolean();
        useVanillaEnderChestSounds = config.getValue("useVanillaEnderChestsSounds")
                .setComment("Enable this to make EnderStorage use vanilla's EnderChest sounds instead of the standard chest.")
                .setDefaultBoolean(false)
                .getBoolean();
        config.save();
    }

    public static ItemStack getPersonalItem() {
        if (personalItem == null) {
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(personalItemTag.getString()));
            if (item == Items.AIR) {
                LOGGER.error("Invalid personal item in config. Got: '{}. Resetting to default.", personalItemTag.getString());
                item = Items.DIAMOND;
                personalItemTag.reset();
                personalItemTag.save();
            }
            personalItem = new ItemStack(item);
        }
        return personalItem;
    }
}
