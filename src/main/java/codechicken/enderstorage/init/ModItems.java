package codechicken.enderstorage.init;

import codechicken.enderstorage.client.BakedEnderPouchOverrideHandler;
import codechicken.enderstorage.client.model.OverrideBakedModel;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.reference.Reference;
import codechicken.lib.render.ModelRegistryHelper;
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

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        ModelLoader.setCustomModelResourceLocation(enderPouch, 0,new ModelResourceLocation(Reference.MOD_PREFIX + "enderPouch", "inventory"));
        ModelRegistryHelper.register(new ModelResourceLocation(Reference.MOD_PREFIX + "enderPouch", "inventory"), new OverrideBakedModel(BakedEnderPouchOverrideHandler.INSTANCE));
        /*ModelLoader.setCustomMeshDefinition(enderPouch, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                Frequency frequency = Frequency.fromItemStack(stack);
                EnderItemStorage storage = (EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item");
                return new ModelResourceLocation(Reference.MOD_PREFIX + "enderPouch", frequency.toModelLoc() + ",open" + (storage.openCount() > 0));
            }
        });
        ArrayList<ModelResourceLocation> modelLocations = new ArrayList<ModelResourceLocation>();
        for (int l = 0; l < 16; l++) {
            for (int m = 0; m < 16; m++) {
                for (int r = 0; r < 16; r++) {
                    for (int ow = 0; ow < 2; ow++) {
                        for (int op = 0; op < 2; op++) {
                            Frequency frequency = new Frequency(l, m, r);
                            if ((ow != 0)) {
                                frequency.setOwner("dummy");
                            }
                            boolean open = (op != 0);
                            ModelResourceLocation location = new ModelResourceLocation(Reference.MOD_PREFIX + "enderPouch", frequency.toModelLoc() + ",open=" + open);
                            modelLocations.add(location);
                        }
                    }
                }
            }
        }
        ModelBakery.registerItemVariants(enderPouch, modelLocations.toArray(new ModelResourceLocation[modelLocations.size()]));
        */
    }

}
