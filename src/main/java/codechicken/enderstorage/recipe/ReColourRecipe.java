package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.ModContent;
import codechicken.lib.colour.EnumColour;
import com.google.gson.JsonElement;
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
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by covers1624 on 8/07/2017.
 */
public class ReColourRecipe extends RecipeBase {

    private Ingredient ingredient;

    public ReColourRecipe(ResourceLocation id, String group, @Nonnull ItemStack result, Ingredient ingredient) {
        super(id, group, result, NonNullList.from(Ingredient.EMPTY, ingredient));
        this.ingredient = ingredient;
    }

    @Override
    public ItemStack getCraftingResult(CraftingInventory inv) {
        int foundRow = 0;
        Frequency currFreq = new Frequency();
        for (int row = 1; row < 3; row++) {//Grab the input frequency, and store it's row.
            ItemStack stack = inv.getStackInSlot(1 + row * inv.getWidth());
            if (ingredient.test(stack)) {
                foundRow = row;
                currFreq = Frequency.readFromStack(stack);
                break;
            }
        }
        EnumColour[] colours = new EnumColour[] { null, null, null };
        for (int col = 0; col < 3; col++) {//Grab the dyes in rows..
            for (int row = 0; row < foundRow; row++) {
                ItemStack stack = inv.getStackInSlot(col + row * inv.getWidth());
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

        return currFreq.writeToStack(super.getCraftingResult(inv));
    }

    @Override
    public boolean matches(CraftingInventory inv, World worldIn) {
        if (inv.isEmpty()) {
            return false;
        }
        boolean inputFound = false;
        int foundRow = 0;
        for (int row = 1; row < 3; row++) {//Find the input in the last 2 rows.
            ItemStack stack = inv.getStackInSlot(1 + row * inv.getWidth());
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
                ItemStack stack = inv.getStackInSlot(col + row * inv.getWidth());
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
                    ItemStack stack = inv.getStackInSlot(col + row * inv.getWidth());
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
    public boolean isDynamic() {
        return true;
    }

    @Override
    public IRecipeSerializer<?> getSerializer() {
        return ModContent.reColourRecipeSerializer;
    }

    public static class Serializer extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<ReColourRecipe> {

        @Override
        public ReColourRecipe read(ResourceLocation recipeId, JsonObject json) {
            String group = JSONUtils.getString(json, "group", "");
            ItemStack result = ShapedRecipe.deserializeItem(JSONUtils.getJsonObject(json, "result"));
            Ingredient ingredient;
            JsonElement ing = json.get("ingredient");
            if (ing != null) {
                ingredient = Ingredient.deserialize(ing);
            } else {
                ingredient = Ingredient.fromStacks(result);
            }

            return new ReColourRecipe(recipeId, group, result, ingredient);
        }

        @Nullable
        @Override
        public ReColourRecipe read(ResourceLocation recipeId, PacketBuffer buffer) {
            String s = buffer.readString(32767);
            Ingredient ing = Ingredient.read(buffer);
            ItemStack result = buffer.readItemStack();
            return new ReColourRecipe(recipeId, s, result, ing);
        }

        @Override
        public void write(PacketBuffer buffer, ReColourRecipe recipe) {
            buffer.writeString(recipe.group);
            recipe.ingredient.write(buffer);
            buffer.writeItemStack(recipe.output);
        }
    }

}
