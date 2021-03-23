package codechicken.enderstorage.recipe;

import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.ICraftingRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.IShapedRecipe;

import javax.annotation.Nonnull;

/**
 * Created by covers1624 on 7/07/2017.
 */
public abstract class RecipeBase implements ICraftingRecipe, IShapedRecipe<CraftingInventory> {

    protected final ResourceLocation id;
    protected final String group;
    protected final ItemStack output;
    protected final NonNullList<Ingredient> input;

    public RecipeBase(ResourceLocation id, String group, @Nonnull ItemStack result, NonNullList<Ingredient> input) {
        this.id = id;
        this.group = group;
        this.output = result.copy();
        this.input = input;
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                Ingredient ingredient = Ingredient.EMPTY;

                if (i >= 0 && j >= 0 && i < 3 && j < 3) {
                    ingredient = this.input.get(i + j * 3);
                }

                if (!ingredient.test(inv.getItem(i + j * inv.getWidth()))) {
                    return false;
                }
            }
        }

        return true;
    }

    @Override
    public ItemStack assemble(CraftingInventory inv) {
        return output.copy();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return input;
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ItemStack getResultItem() {
        return output;
    }

    @Override
    public int getRecipeWidth() {
        return 3;
    }

    @Override
    public int getRecipeHeight() {
        return 3;
    }
}
