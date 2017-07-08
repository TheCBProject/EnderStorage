package codechicken.enderstorage.recipe;

import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.lib.colour.EnumColour;
import com.google.gson.JsonObject;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.*;
import net.minecraftforge.oredict.OreIngredient;
import net.minecraftforge.oredict.ShapedOreRecipe;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by covers1624 on 7/07/2017.
 */
public class Factories {

    public static class CraftingRecipe extends RecipeBase {

        public CraftingRecipe(@Nonnull ItemStack result, NonNullList<Ingredient> input) {
            super(new ResourceLocation(EnderStorage.MOD_ID, "crafting_recipe"), result, input);
        }

        @Nonnull
        @Override
        public ItemStack getCraftingResult(@Nonnull InventoryCrafting inv) {
            EnumColour colour = EnumColour.WHITE;
            finish:
            for (int x = 0; x < 3; x++) {
                for (int y = 0; y < 3; y++) {
                    ItemStack stack = inv.getStackInRowAndColumn(x, y);//Fucking MCP, these are named backwards.
                    if (!stack.isEmpty()) {
                        EnumColour c = EnumColour.fromWoolStack(stack);
                        if (c != null) {
                            colour = c;
                            break finish;
                        }
                    }
                }
            }
            Frequency frequency = new Frequency(colour, colour, colour);
            return frequency.writeToStack(super.getCraftingResult(inv));
        }
    }

    public static class RecipeFactory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            ShapedOreRecipe recipe = ShapedOreRecipe.factory(context, json);

            return new CraftingRecipe(recipe.getRecipeOutput(), recipe.getIngredients());
        }
    }

    public static class ReColourFactory implements IRecipeFactory {

        @Override
        public IRecipe parse(JsonContext context, JsonObject json) {
            ItemStack result = CraftingHelper.getItemStack(json.getAsJsonObject("result"), context);
            Ingredient ingredient;
            if (json.has("ingredient")) {
                ingredient = CraftingHelper.getIngredient(json.getAsJsonObject("ingredient"), context);
            } else {
                ingredient = Ingredient.fromStacks(result);
            }
            return new ReColourRecipe(result, ingredient, DyeIngredientFactory.create());
        }
    }

    public static class DyeIngredientFactory implements IIngredientFactory {

        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            return create();
        }

        public static Ingredient create() {
            Set<Ingredient> ingredients = new HashSet<>();
            for (EnumColour c : EnumColour.values()) {
                ingredients.add(new OreIngredient(c.getDyeOreName()));
            }
            return new MultiIngredient(ingredients);
        }
    }

    public static class WoolIngredientFactory implements IIngredientFactory {

        @Nonnull
        @Override
        public Ingredient parse(JsonContext context, JsonObject json) {
            Set<Ingredient> ingredients = new HashSet<>();
            for (EnumColour c : EnumColour.values()) {
                ingredients.add(new OreIngredient(c.getWoolOreName()));
            }
            return new MultiIngredient(ingredients);
        }
    }

    public static class MultiIngredient extends CompoundIngredient {

        public MultiIngredient(Collection<Ingredient> children) {
            super(children);
        }
    }

}
