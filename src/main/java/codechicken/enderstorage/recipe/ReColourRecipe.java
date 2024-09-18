package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.lib.colour.EnumColour;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.common.crafting.IShapedRecipe;

/**
 * Created by covers1624 on 8/07/2017.
 */
public class ReColourRecipe implements CraftingRecipe, IShapedRecipe<CraftingContainer> {

    protected final String group;
    protected final ItemStack result;
    protected final Ingredient ingredient;

    public ReColourRecipe(ItemStack result) {
        this("", result);
    }

    public ReColourRecipe(String group, ItemStack result) {
        this.group = group;
        this.result = result;
        ingredient = Ingredient.of(result);
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
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
        int foundRow = 0;
        Frequency currFreq = new Frequency();
        for (int row = 1; row < 3; row++) { // Grab the input frequency, and store it's row.
            ItemStack stack = inv.getItem(1 + row * inv.getWidth());
            if (ingredient.test(stack)) {
                foundRow = row;
                currFreq = Frequency.readFromStack(stack);
                break;
            }
        }
        EnumColour[] colours = new EnumColour[] { null, null, null };
        for (int col = 0; col < 3; col++) { // Grab the dyes in rows..
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
        if (colours[0] != null) currFreq.setLeft(colours[0]);
        if (colours[1] != null) currFreq.setMiddle(colours[1]);
        if (colours[2] != null) currFreq.setRight(colours[2]);

        return currFreq.writeToStack(result.copy());
    }

    @Override
    public boolean isSpecial() {
        return true;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnderStorageModContent.RECOLOUR_RECIPE_SERIALIZER.get();
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess pRegistryAccess) {
        return result;
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

    public static class Serializer implements RecipeSerializer<ReColourRecipe> {

        private static final Codec<ReColourRecipe> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(e -> e.group),
                        ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(e -> e.result)
                ).apply(builder, ReColourRecipe::new)
        );

        @Override
        public Codec<ReColourRecipe> codec() {
            return CODEC;
        }

        @Override
        public ReColourRecipe fromNetwork(FriendlyByteBuf buffer) {
            return new ReColourRecipe(buffer.readUtf(), buffer.readItem());
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, ReColourRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeItem(recipe.result);
        }
    }

}
