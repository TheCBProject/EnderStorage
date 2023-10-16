package codechicken.enderstorage.init;

import codechicken.lib.colour.EnumColour;
import codechicken.lib.datagen.ItemModelProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.BlockTagsProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;
import java.util.concurrent.CompletableFuture;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static codechicken.enderstorage.init.EnderStorageModContent.*;

/**
 * Created by covers1624 on 4/25/20.
 */
@Mod.EventBusSubscriber (modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherDataGenerators(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput output = gen.getPackOutput();
        ExistingFileHelper files = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        gen.addProvider(event.includeClient(), new BlockStates(output, files));
        gen.addProvider(event.includeClient(), new ItemModels(output, files));
        gen.addProvider(event.includeServer(), new BlockTagGen(output, lookupProvider, files));
    }

    private static class ItemModels extends ItemModelProvider {

        public ItemModels(PackOutput output, ExistingFileHelper existingFileHelper) {
            super(output, MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            generated(ENDER_CHEST_ITEM).noTexture();
            generated(ENDER_TANK_ITEM).noTexture();

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

        @Nonnull
        @Override
        public String getName() {
            return "EnderStorage BlockStates";
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
}
