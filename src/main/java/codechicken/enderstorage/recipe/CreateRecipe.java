package codechicken.enderstorage.recipe;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.lib.colour.EnumColour;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.HolderLookup;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;

/**
 * Created by covers1624 on 1/11/19.
 */
public class CreateRecipe extends ShapedRecipe {

    public CreateRecipe(String group, ShapedRecipePattern pattern, ItemStack result) {
        super(group, CraftingBookCategory.MISC, pattern, result.copy(), true);
    }

    @Override
    public ItemStack assemble(CraftingInput inv, HolderLookup.Provider registries) {
        EnumColour colour = EnumColour.WHITE;
        // Create recipe only has a single wool.
        // We find it and set the colour to that.
        finish:
        for (int x = 0; x < inv.width(); x++) {
            for (int y = 0; y < inv.height(); y++) {
                ItemStack stack = inv.getItem(x, y);
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
        return frequency.writeToStack(super.assemble(inv, registries));
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnderStorageModContent.CREATE_RECIPE_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<CreateRecipe> {

        private static final MapCodec<CreateRecipe> CODEC = RecordCodecBuilder.mapCodec(builder -> builder.group(
                        Codec.STRING.optionalFieldOf("group", "").forGetter(e -> e.group),
                        ShapedRecipePattern.MAP_CODEC.forGetter(e -> e.pattern),
                        ItemStack.STRICT_CODEC.fieldOf("result").forGetter(e -> e.result)
                ).apply(builder, CreateRecipe::new)
        );

        private static final StreamCodec<RegistryFriendlyByteBuf, CreateRecipe> STREAM_CODEC = StreamCodec.composite(
                ByteBufCodecs.STRING_UTF8, e -> e.group,
                ShapedRecipePattern.STREAM_CODEC, e -> e.pattern,
                ItemStack.STREAM_CODEC, e -> e.result,
                CreateRecipe::new
        );

        @Override
        public MapCodec<CreateRecipe> codec() {
            return CODEC;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, CreateRecipe> streamCodec() {
            return STREAM_CODEC;
        }
    }
}
