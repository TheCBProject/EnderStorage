package codechicken.enderstorage.recipe;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.IShapedRecipe;

import javax.annotation.Nonnull;

/**
 * Created by covers1624 on 7/07/2017.
 */
public abstract class RecipeBase implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

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
    public boolean matches(CraftingContainer inv, Level worldIn) {
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
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
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
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

    @Override
    public CraftingBookCategory category() {
        return CraftingBookCategory.MISC;
    }
}
