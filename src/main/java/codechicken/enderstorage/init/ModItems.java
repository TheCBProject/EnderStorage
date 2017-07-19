package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.ModelBakery;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ModItems {

    public static ItemEnderPouch enderPouch;

    public static void init() {
        enderPouch = new ItemEnderPouch();
        ForgeRegistries.ITEMS.register(enderPouch.setRegistryName("ender_pouch"));
    }

    @SideOnly (Side.CLIENT)
    public static void registerModels() {
        ModelResourceLocation invLocation = new ModelResourceLocation("enderstorage:ender_pouch", "inventory");
        ModelLoader.setCustomModelResourceLocation(enderPouch, 0, invLocation);
        ModelLoader.setCustomMeshDefinition(enderPouch, (stack) -> invLocation);
        ModelRegistryHelper.register(invLocation, new CCBakeryModel());
        ModelBakery.registerItemKeyGenerator(enderPouch, stack -> {
            Frequency frequency = Frequency.readFromStack(stack);
            boolean open = ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item")).openCount() > 0;
            return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + frequency.toModelLoc() + "|" + open;
        });
    }

}
