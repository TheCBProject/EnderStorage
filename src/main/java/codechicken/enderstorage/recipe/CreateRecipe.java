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
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapedRecipePattern;

/**
 * Created by covers1624 on 1/11/19.
 */
public class CreateRecipe extends ShapedRecipe {

    public CreateRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
        super(group, CraftingBookCategory.MISC, pattern, result.copy(), true);
    }

    @Override
    public ItemStack assemble(CraftingContainer inv, RegistryAccess pRegistryAccess) {
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
        return frequency.writeToStack(super.assemble(inv, pRegistryAccess));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnderStorageModContent.CREATE_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<CreateRecipe> {

        private static final Codec<CreateRecipe> CODEC = RecordCodecBuilder.create(builder -> builder.group(
                        ExtraCodecs.strictOptionalField(Codec.STRING, "group", "").forGetter(e -> e.group),
                        ShapedRecipePattern.MAP_CODEC.forGetter(e -> e.pattern),
                        ItemStack.ITEM_WITH_COUNT_CODEC.fieldOf("result").forGetter(e -> e.result)
                ).apply(builder, CreateRecipe::new)
        );

        @Override
        public Codec<CreateRecipe> codec() {
            return CODEC;
        }

        @Override
        public CreateRecipe fromNetwork(FriendlyByteBuf buffer) {
            return new CreateRecipe(
                    buffer.readUtf(),
                    ShapedRecipePattern.fromNetwork(buffer),
                    buffer.readItem()
            );
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CreateRecipe recipe) {
            buffer.writeUtf(recipe.group);
            recipe.pattern.toNetwork(buffer);
            buffer.writeItem(recipe.result);
        }
    }
}
