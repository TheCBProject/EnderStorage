package codechicken.enderstorage.init;

import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
import codechicken.enderstorage.recipe.CreateRecipe;
import codechicken.enderstorage.recipe.ReColourRecipe;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.datagen.ItemModelProvider;
import codechicken.lib.datagen.recipe.RecipeProvider;
import codechicken.lib.util.CCLTags;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.model.generators.BlockStateProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static codechicken.enderstorage.init.EnderStorageModContent.*;

/**
 * Created by covers1624 on 4/25/20.
 */
public class DataGenerators {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init(IEventBus modBus) {
        LOCK.lock();

        modBus.addListener(DataGenerators::gatherDataGenerators);
    }

    private static void gatherDataGenerators(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper files = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new BlockStates(output, files));
        gen.addProvider(event.includeClient(), new ItemModels(output, files));
        gen.addProvider(event.includeServer(), new BlockTagGen(output, lookupProvider, files));
        gen.addProvider(event.includeServer(), new Recipes(output));
    }

    private static class ItemModels extends ItemModelProvider {

        public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            clazz(ENDER_CHEST_ITEM, EnderChestItemRender.class);
            clazz(ENDER_TANK_ITEM, EnderTankItemRender.class);

            CompositeLoaderBuilder bag = generated(ENDER_POUCH)
                    .noTexture()
                    .customLoader(CompositeLoaderBuilder::ccl)
                    .nested("bag", e -> {
                        e.parent(GENERATED).noTexture();
                        e.override(o -> {
                            o.predicate(modLoc("open"), 0);
                            o.predicate(modLoc("owned"), 0);
                            o.model("ender_pouch_closed", m -> m.parent(GENERATED).texture("layer0", modLoc("item/pouch/closed")));
                        });
                        e.override(o -> {
                            o.predicate(modLoc("open"), 1);
                            o.predicate(modLoc("owned"), 0);
                            o.model("ender_pouch_open", m -> m.parent(GENERATED).texture("layer0", modLoc("item/pouch/open")));
                        });
                        e.override(o -> {
                            o.predicate(modLoc("open"), 0);
                            o.predicate(modLoc("owned"), 1);
                            o.model("ender_pouch_owned_closed", m -> m.parent(GENERATED).texture("layer0", modLoc("item/pouch/owned_closed")));
                        });
                        e.override(o -> {
                            o.predicate(modLoc("open"), 1);
                            o.predicate(modLoc("owned"), 1);
                            o.model("ender_pouch_owned_open", m -> m.parent(GENERATED).texture("layer0", modLoc("item/pouch/owned_open")));
                        });
                    });
            for (String side : new String[] { "left", "middle", "right" }) {
                bag.nested(side, e -> {
                    e.parent(GENERATED).noTexture();
                    for (EnumColour colour : EnumColour.values()) {
                        String col = colour.getSerializedName();
                        e.override(o -> {
                            o.predicate(modLoc(side), colour.ordinal());
                            o.model("ender_pouch_button_" + side + "_" + col, m -> {
                                m.parent(GENERATED).texture(modLoc("item/pouch/buttons/" + side + "/" + col));
                            });
                        });
                    }
                });
            }
        }

        @Override
        public String getName() {
            return "EnderStorage Item models";
        }
    }

    private static class BlockStates extends BlockStateProvider {

        public BlockStates(PackOutput output, ExistingFileHelper exFileHelper) {
            super(output, MOD_ID, exFileHelper);
        }

        @Override
        protected void registerStatesAndModels() {
            ModelFile model = models()
                    .withExistingParent("dummy", "block")
                    .texture("particle", "minecraft:block/obsidian");
            simpleBlock(ENDER_CHEST_BLOCK.get(), model);
            simpleBlock(ENDER_TANK_BLOCK.get(), model);
        }
    }

    private static class BlockTagGen extends BlockTagsProvider {

        public BlockTagGen(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, @Nullable ExistingFileHelper existingFileHelper) {
            super(output, lookupProvider, MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags(HolderLookup.Provider pProvider) {
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(ENDER_CHEST_BLOCK.get())
                    .add(ENDER_TANK_BLOCK.get());
        }
    }

    private static class Recipes extends RecipeProvider {

        public Recipes(PackOutput output) {
            super(output, MOD_ID);
        }

        @Override
        protected void registerRecipes() {
            customShaped(ENDER_POUCH, (group, category, pattern, stack, showNotification) -> new CreateRecipe(group, pattern, stack))
                    .key('P', Tags.Items.ENDER_PEARLS)
                    .key('L', Tags.Items.LEATHER)
                    .key('B', Items.BLAZE_POWDER)
                    .key('W', CCLTags.Items.WOOL)
                    .patternLine("BLB")
                    .patternLine("LPL")
                    .patternLine("BWB");

            customShaped(ENDER_CHEST_ITEM, (group, category, pattern, stack, showNotification) -> new CreateRecipe(group, pattern, stack))
                    .key('P', Tags.Items.ENDER_PEARLS)
                    .key('O', Tags.Items.OBSIDIAN)
                    .key('C', Tags.Items.CHESTS_WOODEN)
                    .key('B', Items.BLAZE_ROD)
                    .key('W', CCLTags.Items.WOOL)
                    .patternLine("BWB")
                    .patternLine("OCO")
                    .patternLine("BPB");
            customShaped(ENDER_TANK_ITEM, (group, category, pattern, stack, showNotification) -> new CreateRecipe(group, pattern, stack))
                    .key('P', Tags.Items.ENDER_PEARLS)
                    .key('O', Tags.Items.OBSIDIAN)
                    .key('C', Items.CAULDRON)
                    .key('B', Items.BLAZE_ROD)
                    .key('W', CCLTags.Items.WOOL)
                    .patternLine("BWB")
                    .patternLine("OCO")
                    .patternLine("BPB");

            special(new ResourceLocation(MOD_ID, "recolour_ender_pouch"), () -> new ReColourRecipe(new ItemStack(ENDER_POUCH.get())));
            special(new ResourceLocation(MOD_ID, "recolour_ender_chest"), () -> new ReColourRecipe(new ItemStack(ENDER_CHEST_ITEM.get())));
            special(new ResourceLocation(MOD_ID, "recolour_ender_tank"), () -> new ReColourRecipe(new ItemStack(ENDER_TANK_ITEM.get())));
        }
    }
}
