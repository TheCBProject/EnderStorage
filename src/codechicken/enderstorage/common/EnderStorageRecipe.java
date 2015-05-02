package codechicken.enderstorage.common;

import java.util.Iterator;

import codechicken.core.featurehack.GameDataManipulator;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.storage.item.ItemEnderChestDummy;

import cpw.mods.fml.common.registry.GameRegistry;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.RecipeSorter;
import net.minecraftforge.oredict.RecipeSorter.Category;

public class EnderStorageRecipe implements IRecipe
{
    @Override
    public boolean matches(InventoryCrafting ic, World var2) {
        for (int row = 0; row < 2; row++)
            if (offsetMatchesDyes(ic, 0, row))
                return true;

        return false;
    }

    private boolean offsetMatchesDyes(InventoryCrafting ic, int col, int row) {
        if (!stackMatches(ic.getStackInRowAndColumn(col + 1, row + 1), Item.getItemFromBlock(EnderStorage.blockEnderChest)))
            return false;

        boolean hasDye = false;
        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 3; j++) {
                //ignore chest slot
                if(i == row + 1 && j == col + 1)
                    continue;

                ItemStack stack = ic.getStackInRowAndColumn(j, i);
                if(i == row && getDyeType(stack) >= 0) {
                    hasDye = true;
                    continue;
                }

                if(stack != null)
                    return false;
            }

        return hasDye;
    }

    public static boolean stackMatches(ItemStack stack, Item item) {
        return stack != null && stack.getItem() == item;
    }

    public ItemStack getCraftingResult(InventoryCrafting ic) {
        for (int row = 0; row < 2; row++) {
            if (!offsetMatchesDyes(ic, 0, row))
                continue;

            ItemStack freqowner = ic.getStackInRowAndColumn(1, row + 1);
            int freq = freqowner.getItemDamage() & 0xFFF;

            int colour1 = recolour(0, row, freq, ic);
            int colour2 = recolour(1, row, freq, ic);
            int colour3 = recolour(2, row, freq, ic);

            ItemStack result = InventoryUtils.copyStack(freqowner, 1);
            result.setItemDamage(EnderStorageManager.getFreqFromColours(colour3, colour2, colour1) | freqowner.getItemDamage() & 0xF000);
            return result;
        }
        return null;
    }

    private int recolour(int i, int row, int freq, InventoryCrafting ic) {
        int dyeType = getDyeType(ic.getStackInRowAndColumn(i, row));
        if (dyeType >= 0)
            return ~dyeType & 0xF;
        return EnderStorageManager.getColourFromFreq(freq, 2-i);
    }

    public int getRecipeSize() {
        return 6;
    }

    public ItemStack getRecipeOutput() {
        return new ItemStack(EnderStorage.blockEnderChest);
    }

    public static void init() {
        EnderStorageRecipe instance = new EnderStorageRecipe();
        GameRegistry.addRecipe(instance);
        RecipeSorter.register("enderstorage:recolour", EnderStorageRecipe.class, Category.SHAPED, "");
        addNormalRecipies();
    }

    public static void removeVanillaChest() {
        GameDataManipulator.replaceItem(Block.getIdFromBlock(Blocks.ender_chest), new ItemEnderChestDummy());
        Iterator<IRecipe> iterator = CraftingManager.getInstance().getRecipeList().iterator();
        while (iterator.hasNext()) {
            ItemStack r = iterator.next().getRecipeOutput();
            if (r != null && r.getItem() == Item.getItemFromBlock(Blocks.ender_chest))
                iterator.remove();
        }

        if (!EnderStorage.removeVanillaRecipe)
            CraftingManager.getInstance().addRecipe(new ItemStack(Blocks.ender_chest),
                    "OOO",
                    "OeO",
                    "OOO",
                    'O', Blocks.obsidian,
                    'e', Items.ender_eye);
    }

    private static void addNormalRecipies() {
        for (int i = 0; i < 16; i++) {
            GameRegistry.addRecipe(new ItemStack(EnderStorage.blockEnderChest, 1, EnderStorageManager.getFreqFromColours(i, i, i)),
                    "bWb",
                    "OCO",
                    "bpb",
                    'b', Items.blaze_rod,
                    'p', Items.ender_pearl,
                    'O', Blocks.obsidian,
                    'C', Blocks.chest,
                    'W', new ItemStack(Blocks.wool, 1, i));

            GameRegistry.addRecipe(new ItemStack(EnderStorage.itemEnderPouch, 1, EnderStorageManager.getFreqFromColours(i, i, i)),
                    "blb",
                    "lpl",
                    "bWb",
                    'b', Items.blaze_powder,
                    'p', Items.ender_pearl,
                    'l', Items.leather,
                    'W', new ItemStack(Blocks.wool, 1, i));

            GameRegistry.addRecipe(new ItemStack(EnderStorage.blockEnderChest, 1, 1 << 12 | EnderStorageManager.getFreqFromColours(i, i, i)),
                    "bWb",
                    "OCO",
                    "bpb",
                    'b', Items.blaze_rod,
                    'p', Items.ender_pearl,
                    'O', Blocks.obsidian,
                    'C', Items.cauldron,
                    'W', new ItemStack(Blocks.wool, 1, i));
        }
    }

    public static int getDyeType(ItemStack item) {
        if (item == null) return -1;
        if (item.getItem() == Items.dye)
            return item.getItemDamage();
        for (int i = 0; i < 16; i++) {
            for (ItemStack target : OreDictionary.getOres(oreDictionaryNames[i]))
                if (OreDictionary.itemMatches(target, item, false))
                    return i;
        }
        return -1;
    }

    public static String[] oreDictionaryNames = new String[]{
            "dyeBlack",
            "dyeRed",
            "dyeGreen",
            "dyeBrown",
            "dyeBlue",
            "dyePurple",
            "dyeCyan",
            "dyeLightGray",
            "dyeGray",
            "dyePink",
            "dyeLime",
            "dyeYellow",
            "dyeLightBlue",
            "dyeMagenta",
            "dyeOrange",
            "dyeWhite"};

    public static int getDyeColour(String string) {
        for (int i = 0; i < 16; i++)
            if (oreDictionaryNames[i].substring(3).equalsIgnoreCase(string))
                return i;
        return -1;
    }
}
