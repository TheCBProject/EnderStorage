package codechicken.enderstorage.recipe;

import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.lib.colour.EnumColour;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import javax.annotation.Nonnull;

/**
 * Created by covers1624 on 8/07/2017.
 */
public class ReColourRecipe extends RecipeBase {

    private Ingredient ingredient;
    private Ingredient dyeIngredient;

    public ReColourRecipe(@Nonnull ItemStack result, Ingredient ingredient, Ingredient dyeIngredient) {
        super(new ResourceLocation(EnderStorage.MOD_ID, "crafting_recipe"), result, NonNullList.from(Ingredient.EMPTY, ingredient));
        this.ingredient = ingredient;
        this.dyeIngredient = dyeIngredient;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        int foundRow = 0;
        Frequency currFreq = new Frequency();
        for (int row = 1; row < 3; row++) {//Grab the input frequency, and store it's row.
            ItemStack stack = inv.getStackInRowAndColumn(1, row);//Fucking MCP, these are named backwards.
            if (ingredient.apply(stack)) {
                foundRow = row;
                currFreq = Frequency.readFromStack(stack);
                break;
            }
        }
        EnumColour[] colours = new EnumColour[] { null, null, null };
        for (int col = 0; col < 3; col++) {//Grab the dyes in rows..
            for (int row = 0; row < foundRow; row++) {
                ItemStack stack = inv.getStackInRowAndColumn(col, row);//Fucking MCP, these are named backwards.
                if (!stack.isEmpty()) {
                    EnumColour colour = EnumColour.fromDyeStack(stack);
                    if (colour != null) {
                        if (colours[col] == null) {
                            colours[col] = colour;
                        }
                    }
                }
            }
        }
        currFreq.setLeft(colours[0]);
        currFreq.setMiddle(colours[1]);
        currFreq.setRight(colours[2]);

        return currFreq.writeToStack(super.getCraftingResult(inv));
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        boolean inputFound = false;
        int foundRow = 0;
        for (int row = 1; row < 3; row++) {//Find the input in the last 2 rows.
            ItemStack stack = inv.getStackInRowAndColumn(1, row);//Fucking MCP, these are named backwards.
            if (!stack.isEmpty()) {
                if (ingredient.apply(stack)) {
                    foundRow = row;
                    inputFound = true;
                    break;
                }
            }
        }
        if (!inputFound) {
            return false;
        }
        EnumColour[] colours = new EnumColour[] { null, null, null };
        boolean hasDye = false;
        for (int col = 0; col < 3; col++) {//Grab the dyes in the columns above the chest.
            for (int row = 0; row < foundRow; row++) {
                ItemStack stack = inv.getStackInRowAndColumn(col, row);//Fucking MCP, these are named backwards.
                if (!stack.isEmpty()) {
                    if (dyeIngredient.apply(stack)) {
                        EnumColour colour = EnumColour.fromDyeStack(stack);
                        if (colour != null) {//Already a dye in that column, invalid.
                            if (colours[col] != null) {
                                return false;
                            } else {//Cool valid dye.
                                hasDye = true;
                                colours[col] = colour;
                            }

                        }
                    }
                }
            }
        }
        if (hasDye) {//Cull the recipe of there is cruft.
            for (int col = 0; col < 3; col++) {
                for (int row = 0; row < 3; row++) {
                    ItemStack stack = inv.getStackInRowAndColumn(col, row);//Fucking MCP, these are named backwards.
                    if (!stack.isEmpty()) {
                        if (row >= foundRow) {//Make sure there is no dye bellow or on the same row as the chest.
                            if (dyeIngredient.apply(stack)) {
                                return false;
                            }
                        }//Make sure there is no other cruft in the recipe.
                        if (!ingredient.apply(stack) && !dyeIngredient.apply(stack)) {
                            return false;
                        }
                    }
                }
            }
        }

        return hasDye;
    }

    @Override
    public boolean isDynamic() {
        return true;
    }
}
