package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.registry.GameRegistry;

import static codechicken.enderstorage.init.ModBlocks.blockEnderStorage;
import static codechicken.enderstorage.init.ModItems.enderPouch;

/**
 * Created by covers1624 on 5/16/2016.
 */
public class ModRecipes {

    public static void init() {
        for (int i = 0; i < 16; i++) {
            Frequency frequency = new Frequency(i, i, i);
            GameRegistry.addRecipe(frequency.toItemStack(new ItemStack(blockEnderStorage)), "bWb", "OCO", "bpb", 'b', Items.blaze_rod, 'p', Items.ender_pearl, 'O', Blocks.obsidian, 'C', Blocks.chest, 'W', new ItemStack(Blocks.wool, 1, i));
            GameRegistry.addRecipe(frequency.toItemStack(new ItemStack(enderPouch)), "blb", "lpl", "bWb", 'b', Items.blaze_powder, 'p', Items.ender_pearl, 'l', Items.leather, 'W', new ItemStack(Blocks.wool, 1, i));
            GameRegistry.addRecipe(frequency.toItemStack(new ItemStack(blockEnderStorage, 1, 1)), "bWb", "OCO", "bpb", 'b', Items.blaze_rod, 'p', Items.ender_pearl, 'O', Blocks.obsidian, 'C', Items.cauldron, 'W', new ItemStack(Blocks.wool, 1, i));
        }
        //GameRegistry.addRecipe(new ItemStack(blockEnderStorage), "bWb", "OCO", "bpb", 'b', Items.blaze_rod, 'p', Items.ender_pearl, 'O', Blocks.obsidian, 'C', Blocks.chest, 'W', new ItemStack(Blocks.wool));
        //GameRegistry.addRecipe(new ItemStack(enderPouch), "blb", "lpl", "bWb", 'b', Items.blaze_powder, 'p', Items.ender_pearl, 'l', Items.leather, 'W', new ItemStack(Blocks.wool));
        //GameRegistry.addRecipe(new ItemStack(blockEnderStorage, 1, 1), "bWb", "OCO", "bpb", 'b', Items.blaze_rod, 'p', Items.ender_pearl, 'O', Blocks.obsidian, 'C', Items.cauldron, 'W', new ItemStack(Blocks.wool));
    }

}
