package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.reference.Reference;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ModItems {

    public static ItemEnderPouch enderPouch;

    public static void init() {
        enderPouch = new ItemEnderPouch();
        GameRegistry.register(enderPouch.setRegistryName("enderPouch"));
    }

    public static void registerModels() {
        ArrayList<ModelResourceLocation> modelLocations = new ArrayList<ModelResourceLocation>();
        for (int l = 0; l < 16; l++) {
            for (int m = 0; m < 16; m++) {
                for (int r = 0; r < 16; r++) {
                    for (int ow = 0; ow < 2; ow++) {
                        for (int op = 0; op < 2; op++) {
                            Frequency frequency = new Frequency(l, m, r);
                            boolean owned = (ow != 0);
                            boolean open = (op != 0);
                            ModelResourceLocation location = new ModelResourceLocation(Reference.MOD_PREFIX + "enderPouch", frequency.toModelLoc() + ",open=" + open);
                            modelLocations.add(location);
                        }
                    }
                }
            }
        }
        ModelLoader.registerItemVariants(enderPouch, modelLocations.toArray(new ModelResourceLocation[modelLocations.size()]));
        ModelLoader.setCustomMeshDefinition(enderPouch, new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                Frequency frequency = Frequency.fromItemStack(stack);
                return null;
            }
        });
    }

}
