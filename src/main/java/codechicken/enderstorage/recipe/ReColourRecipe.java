package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.lib.colour.EnumColour;
import com.google.gson.JsonElement;
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
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by covers1624 on 8/07/2017.
 */
public class ReColourRecipe extends RecipeBase {

    private final Ingredient ingredient;

    public ReColourRecipe(ResourceLocation id, String group, @Nonnull ItemStack result, Ingredient ingredient) {
        super(id, group, result, NonNullList.of(Ingredient.EMPTY, ingredient));
        this.ingredient = ingredient;
    }

    @Override
    public ItemStack assemble(CraftingContainer inv) {
        int foundRow = 0;
        Frequency currFreq = new Frequency();
        for (int row = 1; row < 3; row++) {//Grab the input frequency, and store it's row.
            ItemStack stack = inv.getItem(1 + row * inv.getWidth());
            if (ingredient.test(stack)) {
                foundRow = row;
                currFreq = Frequency.readFromStack(stack);
                break;
            }
        }
        EnumColour[] colours = new EnumColour[] { null, null, null };
        for (int col = 0; col < 3; col++) {//Grab the dyes in rows..
            for (int row = 0; row < foundRow; row++) {
                ItemStack stack = inv.getItem(col + row * inv.getWidth());
                if (!stack.isEmpty()) {
                    EnumColour colour = EnumColour.fromDyeStack(stack);
                    if (colour != null) {
                        if (colours[col] == null) {
                            colours[col] = colour;
                        } else {
                            colours[col] = EnumColour.mix(colours[col], colour);
                        }
                    }
                }
            }
        }
        currFreq.setLeft(colours[0]);
        currFreq.setMiddle(colours[1]);
        currFreq.setRight(colours[2]);

        return currFreq.writeToStack(super.assemble(inv));
    }

    @Override
    public boolean matches(CraftingContainer inv, Level worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        boolean inputFound = false;
        int foundRow = 0;
        for (int row = 1; row < 3; row++) {//Find the input in the last 2 rows.
            ItemStack stack = inv.getItem(1 + row * inv.getWidth());
            if (!stack.isEmpty()) {
                if (ingredient.test(stack)) {
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
                ItemStack stack = inv.getItem(col + row * inv.getWidth());
                if (!stack.isEmpty()) {
                    EnumColour colour = EnumColour.fromDyeStack(stack);
                    if (colour != null) {//Already a dye in that column, invalid.
                        if (colours[col] != null) {
                            EnumColour merge = EnumColour.mix(colours[col], colour);
                            if (merge == null || merge == colour) {
                                return false;
                            }
                            colours[col] = merge;
                        } else {//Cool valid dye.
                            hasDye = true;
                            colours[col] = colour;
                        }

                    }
                }
            }
        }
        if (hasDye) {//Cull the recipe of there is cruft.
            for (int col = 0; col < 3; col++) {
                for (int row = 0; row < 3; row++) {
                    ItemStack stack = inv.getItem(col + row * inv.getWidth());
                    if (!stack.isEmpty()) {
                        if (row >= foundRow) {//Make sure there is no dye bellow or on the same row as the chest.
                            if (EnumColour.fromDyeStack(stack) != null) {
                                return false;
                            }
                        }//Make sure there is no other cruft in the recipe.
                        if (!ingredient.test(stack) && EnumColour.fromDyeStack(stack) == null) {
                            return false;
                        }
                    }
                }
            }
        }

        return hasDye;
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnderStorageModContent.RECOLOUR_RECIPE_SERIALIZER.get();
    }

    public static class Serializer extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<ReColourRecipe> {

        @Override
        public ReColourRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            String group = GsonHelper.getAsString(json, "group", "");
            ItemStack result = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            Ingredient ingredient;
            JsonElement ing = json.get("ingredient");
            if (ing != null) {
                ingredient = Ingredient.fromJson(ing);
            } else {
                ingredient = Ingredient.of(result);
            }

            return new ReColourRecipe(recipeId, group, result, ingredient);
        }

        @Nullable
        @Override
        public ReColourRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String s = buffer.readUtf(32767);
            Ingredient ing = Ingredient.fromNetwork(buffer);
            ItemStack result = buffer.readItem();
            return new ReColourRecipe(recipeId, s, result, ing);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ReColourRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.ingredient.toNetwork(buffer);
            buffer.writeItem(recipe.output);
        }
    }

}
