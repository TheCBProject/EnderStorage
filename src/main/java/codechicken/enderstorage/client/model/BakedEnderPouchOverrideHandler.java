package codechicken.enderstorage.client.model;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.EnderPouchModelLoader;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.util.LogHelper;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class BakedEnderPouchOverrideHandler extends ItemOverrideList {

    public static final BakedEnderPouchOverrideHandler INSTANCE = new BakedEnderPouchOverrideHandler();

    public BakedEnderPouchOverrideHandler() {
        super(ImmutableList.<ItemOverride>of());
    }

    @Override
    public IBakedModel handleItemState(IBakedModel originalModel, ItemStack stack, World world, EntityLivingBase entity) {
        Frequency frequency = Frequency.fromItemStack(stack);
        EnderItemStorage storage = (EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item");
        String key = frequency.toModelLoc() + ",open=" + (storage.openCount() > 0);
        IBakedModel pouchModel = EnderPouchModelLoader.getModel(key);
        if (pouchModel == null) {
            LogHelper.warn("Unable to get pouch model for key [%s]!", key);
            return originalModel;
        }
        return pouchModel;
    }
}
