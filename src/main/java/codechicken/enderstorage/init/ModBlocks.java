package codechicken.enderstorage.init;

import codechicken.enderstorage.block.BlockEnderStorage;
import codechicken.enderstorage.item.ItemEnderStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
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

}
