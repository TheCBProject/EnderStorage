package codechicken.enderstorage.init;

import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.block.BlockEnderStorage.Type;
import codechicken.enderstorage.client.ParticleDummyModel;
import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
import codechicken.enderstorage.item.ItemEnderStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.model.ModelRegistryHelper;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMap;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ModBlocks {

    public static BlockEnderStorage blockEnderStorage;
    public static ItemEnderStorage itemEnderStorage;

    public static void init() {
        blockEnderStorage = new BlockEnderStorage();
        itemEnderStorage = new ItemEnderStorage(blockEnderStorage);
        ForgeRegistries.BLOCKS.register(blockEnderStorage.setRegistryName("ender_storage"));
        ForgeRegistries.ITEMS.register(itemEnderStorage.setRegistryName("ender_storage"));
        GameRegistry.registerTileEntity(TileEnderChest.class, "Ender Chest");
        GameRegistry.registerTileEntity(TileEnderTank.class, "Ender Tank");
    }

    @SideOnly (Side.CLIENT)
    public static void registerModels() {
        for (int i = 0; i < Type.VALUES.length; i++) {
            Type variant = Type.VALUES[i];
            ModelResourceLocation location = new ModelResourceLocation("enderstorage:ender_storage", "type=" + variant.getName());
            ModelLoader.setCustomModelResourceLocation(itemEnderStorage, i, location);
        }

        ModelRegistryHelper.register(new ModelResourceLocation("enderstorage:ender_storage", "type=ender_chest"), new EnderChestItemRender());
        ModelRegistryHelper.register(new ModelResourceLocation("enderstorage:ender_storage", "type=ender_tank"), new EnderTankItemRender());

        ModelLoader.setCustomStateMapper(blockEnderStorage, new StateMap.Builder().ignore(BlockEnderStorage.VARIANTS).build());
        ModelRegistryHelper.register(new ModelResourceLocation("enderstorage:ender_storage", "normal"), ParticleDummyModel.INSTANCE);
    }

}
