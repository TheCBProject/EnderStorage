package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.lib.colour.EnumColour;
import com.google.gson.JsonObject;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Map;

/**
 * Created by covers1624 on 1/11/19.
 */
public class CreateRecipe extends RecipeBase {

    public CreateRecipe(ResourceLocation id, String group, @Nonnull ItemStack result, NonNullList<Ingredient> input) {
        super(id, group, result, input);
    }

    @Nonnull
    @Override
    public ItemStack assemble(CraftingContainer inv) {
        EnumColour colour = EnumColour.WHITE;
        finish:
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ItemStack stack = inv.getItem(x + y * inv.getWidth());
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
        return frequency.writeToStack(super.assemble(inv));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnderStorageModContent.CREATE_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<CreateRecipe> {

        @Override
        public CreateRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            Map<String, Ingredient> key = ShapedRecipe.keyFromJson(GsonHelper.getAsJsonObject(json, "key"));
            String[] pattern = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(GsonHelper.getAsJsonArray(json, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            NonNullList<Ingredient> ingredients = ShapedRecipe.dissolvePattern(pattern, key, width, height);
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new CreateRecipe(recipeId, group, result, ingredients);
        }

        @Nullable
        @Override
        public CreateRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String s = buffer.readUtf(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(3 * 3, Ingredient.EMPTY);

            for (int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.fromNetwork(buffer));
            }

            ItemStack result = buffer.readItem();
            return new CreateRecipe(recipeId, s, result, ingredients);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CreateRecipe recipe) {
            buffer.writeUtf(recipe.group);

            for (Ingredient ingredient : recipe.input) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.output);
        }
    }

}
