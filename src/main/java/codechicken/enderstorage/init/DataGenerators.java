package codechicken.enderstorage.init;

import codechicken.lib.datagen.ItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.forge.event.lifecycle.GatherDataEvent;

import javax.annotation.Nonnull;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 4/25/20.
 */
@Mod.EventBusSubscriber (modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class DataGenerators {

    @SubscribeEvent
    public static void gatherDataGenerators(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        ExistingFileHelper files = event.getExistingFileHelper();
        gen.addProvider(event.includeClient(), new BlockStates(gen, files));
        gen.addProvider(event.includeClient(), new ItemModels(gen, files));
    }

    private static class ItemModels extends ItemModelProvider {

        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
            super(generator, MOD_ID, existingFileHelper);
        }

        @Override
        protected void registerModels() {
            generated(EnderStorageModContent.ENDER_CHEST_ITEM).noTexture();
            generated(EnderStorageModContent.ENDER_TANK_ITEM).noTexture();
            generated(EnderStorageModContent.ENDER_POUCH).noTexture();
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
            simpleBlock(EnderStorageModContent.ENDER_CHEST_BLOCK.get(), model);
            simpleBlock(EnderStorageModContent.ENDER_TANK_BLOCK.get(), model);
        }
    }
}
