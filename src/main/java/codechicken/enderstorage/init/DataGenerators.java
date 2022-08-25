package codechicken.enderstorage.init;

import codechicken.lib.datagen.ItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ConfiguredModel;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

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
        ExistingFileHelper files = event.getExistingFileHelper();
        if (event.includeClient()) {
            gen.addProvider(new BlockStates(gen, files));
            gen.addProvider(new ItemModels(gen, files));
        }
        if (event.includeServer()) {
            gen.addProvider(new BlockTagGen(gen, files));
        }
    }

    private static class ItemModels extends ItemModelProvider {

        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            generated(ENDER_CHEST_ITEM).noTexture();
            generated(ENDER_TANK_ITEM).noTexture();
            generated(ENDER_POUCH).noTexture();
        }

        @Override
        public String getName() {
            return "EnderStorage Item models";
        }
    }

    private static class BlockStates extends BlockStateProvider {

        public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
            super(gen, MOD_ID, exFileHelper);
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

        public BlockTagGen(DataGenerator gen, @Nullable ExistingFileHelper existingFileHelper) {
            super(gen, MOD_ID, existingFileHelper);
        }

        @Override
        protected void addTags() {
            tag(BlockTags.MINEABLE_WITH_PICKAXE)
                    .add(ENDER_CHEST_BLOCK.get())
                    .add(ENDER_TANK_BLOCK.get());
        }
    }
}
