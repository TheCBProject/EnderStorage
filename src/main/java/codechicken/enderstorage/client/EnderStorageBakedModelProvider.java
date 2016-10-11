package codechicken.enderstorage.client;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.ModItems;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.reference.Reference;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.model.bakery.SimplePerspectiveAwareLayerModelBakery;
import codechicken.lib.model.loader.IBakedModelLoader;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.util.ArrayUtils;
import com.google.common.collect.ImmutableList.Builder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by covers1624 on 7/25/2016.
 */
public class EnderStorageBakedModelProvider implements IBakedModelLoader {

    public static final EnderStorageBakedModelProvider INSTANCE = new EnderStorageBakedModelProvider();

    public static class EnderStorageKeyProvider implements IModKeyProvider {

        public static final EnderStorageKeyProvider INSTANCE = new EnderStorageKeyProvider();

        @Override
        public String getMod() {
            return Reference.MOD_PREFIX.replace(":", "");
        }

        @Override
        public String createKey(ItemStack stack) {
            if (ModItems.enderPouch.equals(stack.getItem())) {
                Frequency frequency = Frequency.fromItemStack(stack);
                EnderItemStorage storage = (EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item");
                return frequency.toModelLoc() + ",open=" + (storage.openCount() > 0);
            }
            return null;
        }

        @Override
        public String createKey(IBlockState state) {
            return null;
        }
    }

    @Override
    public IModKeyProvider createKeyProvider() {
        return EnderStorageKeyProvider.INSTANCE;
    }

    @Override
    public void addTextures(Builder<ResourceLocation> builder) {
        String pouchLocation = Reference.MOD_PREFIX + "items/pouch/";
        builder.add(new ResourceLocation(pouchLocation + "closed"));
        builder.add(new ResourceLocation(pouchLocation + "open"));
        builder.add(new ResourceLocation(pouchLocation + "owned_closed"));
        builder.add(new ResourceLocation(pouchLocation + "owned_open"));
        builder.addAll(addAllColours(pouchLocation + "buttons/left/"));
        builder.addAll(addAllColours(pouchLocation + "buttons/middle/"));
        builder.addAll(addAllColours(pouchLocation + "buttons/right/"));
    }

    private List<ResourceLocation> addAllColours(String locationParent) {
        ArrayList<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        for (EnumColour colour : EnumColour.values()) {
            locations.add(new ResourceLocation(locationParent + colour.getMinecraftName()));
        }
        return locations;
    }

    @Override
    public IBakedModel bakeModel(String key) {
        Map<String, String> values = ArrayUtils.convertKeyValueArrayToMap(key.split(","));
        if (!ArrayUtils.containsKeys(values, "owned", "open", "left", "middle", "right")) {
            LogHelper.warn("Invalid key for EnderPouch model [%s]!", key);
            return null;
        }
        boolean owned = Boolean.parseBoolean(values.get("owned"));
        boolean open = Boolean.parseBoolean(values.get("open"));
        String pouchPrefix = Reference.MOD_PREFIX + "items/pouch/";
        //button,colour.
        String buttonsPrefix = pouchPrefix + "buttons/%s/%s";
        ResourceLocation leftButton = new ResourceLocation(String.format(buttonsPrefix, "left", values.get("left")));
        ResourceLocation middleButton = new ResourceLocation(String.format(buttonsPrefix, "middle", values.get("middle")));
        ResourceLocation rightButton = new ResourceLocation(String.format(buttonsPrefix, "right", values.get("right")));
        ResourceLocation bagLocation;
        if (open) {
            if (owned) {
                bagLocation = new ResourceLocation(pouchPrefix + "owned_open");
            } else {
                bagLocation = new ResourceLocation(pouchPrefix + "open");
            }
        } else {
            if (owned) {
                bagLocation = new ResourceLocation(pouchPrefix + "owned_closed");
            } else {
                bagLocation = new ResourceLocation(pouchPrefix + "closed");
            }
        }
        SimplePerspectiveAwareLayerModelBakery correctModel = new SimplePerspectiveAwareLayerModelBakery(bagLocation, leftButton, middleButton, rightButton);
        return correctModel.bake(TransformUtils.DEFAULT_ITEM);
    }
}
