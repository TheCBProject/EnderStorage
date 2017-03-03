package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.blockbakery.BlockBakery;
import codechicken.lib.model.blockbakery.CCBakeryModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ModItems {

    public static ItemEnderPouch enderPouch;

    public static void init() {

        enderPouch = new ItemEnderPouch();
        GameRegistry.register(enderPouch.setRegistryName("enderPouch"));
    }

    @SideOnly (Side.CLIENT)
    public static void registerModels() {

        ModelLoader.setCustomModelResourceLocation(enderPouch, 0, new ModelResourceLocation("enderstorage:enderPouch", "inventory"));
        ModelRegistryHelper.register(new ModelResourceLocation("enderstorage:enderPouch", "inventory"), new CCBakeryModel(""));
        BlockBakery.registerItemKeyGenerator(enderPouch, stack -> {
            Frequency frequency = Frequency.fromItemStack(stack);
            boolean open = ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item")).openCount() > 0;
            return BlockBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + frequency.toModelLoc() + "|" + open;
        });
    }

}
