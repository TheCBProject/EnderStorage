package codechicken.enderstorage.init;

import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.client.render.DummyBakedModel;
import codechicken.enderstorage.client.render.EnderChestItemRender;
import codechicken.enderstorage.item.ItemEnderStorage;
import codechicken.enderstorage.reference.Reference;
import codechicken.enderstorage.reference.VariantReference;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.render.ModelRegistryHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ModBlocks {

    public static BlockEnderStorage blockEnderStorage;

    public static void init() {
        blockEnderStorage = new BlockEnderStorage();
        GameRegistry.register(blockEnderStorage.setRegistryName("enderStorage"));
        GameRegistry.register(new ItemEnderStorage(blockEnderStorage).setRegistryName("enderStorage"));
        GameRegistry.registerTileEntity(TileEnderChest.class, "Ender Chest");
        GameRegistry.registerTileEntity(TileEnderTank.class, "Ender Tank");
    }

    public static void registerModels() {
        for (int i = 0; i < VariantReference.enderBlockNamesList.size(); i++) {
            String variant = VariantReference.enderBlockNamesList.get(i);
            ModelResourceLocation location = new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=" + variant);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockEnderStorage), i, location);
        }

        ModelRegistryHelper.register(new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=enderChest"), new DummyBakedModel());
        ModelRegistryHelper.register(new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=enderTank"), new DummyBakedModel());
    }

}
