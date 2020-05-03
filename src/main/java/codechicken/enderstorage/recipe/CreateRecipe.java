package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.ModContent;
import codechicken.lib.colour.EnumColour;
import com.google.gson.JsonObject;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;

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
    public ItemStack getCraftingResult(CraftingInventory inv) {
        EnumColour colour = EnumColour.WHITE;
        finish:
        for (int x = 0; x < 3; x++) {
            for (int y = 0; y < 3; y++) {
                ItemStack stack = inv.getStackInSlot(x + y * inv.getWidth());
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

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModContent.createRecipeSerializer;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<CreateRecipe> {

        @Override
        public CreateRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            Map<String, Ingredient> key = ShapedRecipe.deserializeKey(JSONUtils.getJsonObject(json, "key"));
            String[] pattern = ShapedRecipe.shrink(ShapedRecipe.patternFromJson(JSONUtils.getJsonArray(json, "pattern")));
            int width = pattern[0].length();
            int height = pattern.length;
            NonNullList<Ingredient> ingredients = ShapedRecipe.deserializeIngredients(pattern, key, width, height);
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            return new CreateRecipe(recipeId, group, result, ingredients);
        }

        @Nullable
        @Override
        public CreateRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            NonNullList<Ingredient> ingredients = NonNullList.withSize(3 * 3, Ingredient.EMPTY);

            for (int k = 0; k < ingredients.size(); ++k) {
                ingredients.set(k, Ingredient.read(buffer));
            }

            ItemStack result = buffer.readItemStack();
            return new CreateRecipe(recipeId, s, result, ingredients);
        }

        @Override
        public void write(PacketBuffer buffer, CreateRecipe recipe) {
            buffer.writeString(recipe.group);

            for (Ingredient ingredient : recipe.input) {
                ingredient.write(buffer);
            }

            buffer.writeItemStack(recipe.output);
        }
    }

}
