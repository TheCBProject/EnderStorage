package codechicken.enderstorage.init;

import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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

    @SideOnly(Side.CLIENT)
    public static void registerModels() {
        for (int i = 0; i < VariantReference.enderBlockNamesList.size(); i++) {
            String variant = VariantReference.enderBlockNamesList.get(i);
            ModelResourceLocation location = new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=" + variant);
            ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(blockEnderStorage), i, location);
        }

        ModelRegistryHelper.register(new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=enderChest"), new EnderChestItemRender());
        ModelRegistryHelper.register(new ModelResourceLocation(Reference.MOD_PREFIX + "enderStorage", "type=enderTank"), new EnderTankItemRender());
    }

}
