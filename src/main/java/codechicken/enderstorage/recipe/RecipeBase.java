package codechicken.enderstorage.recipe;

import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nonnull;

/**
 * Created by covers1624 on 7/07/2017.
 */
public class RecipeBase extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe {

    protected ItemStack output = ItemStack.EMPTY;
    protected NonNullList<Ingredient> input = null;
    protected ResourceLocation group;

    public RecipeBase(ResourceLocation group, @Nonnull ItemStack result, NonNullList<Ingredient> input) {
        this.group = group;
        this.output = result.copy();
        this.input = input;
    }

    @Override
    public boolean matches(InventoryCrafting inv, World worldIn) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Ingredient ingredient = Ingredient.EMPTY;

                if (i >= 0 && j >= 0 && i < 3 && j < 3) {
                    ingredient = this.input.get(i + j * 3);
                }

                if (!ingredient.apply(inv.getStackInRowAndColumn(i, j))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack getCraftingResult(InventoryCrafting inv) {
        return output.copy();
    }

    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return input;
    }

    @Override
    public String getGroup() {
        return group.toString();
    }

    @Override
    public ItemStack getRecipeOutput() {
        return output;
    }
}
