//package codechicken.enderstorage.init;
//
//import net.minecraft.data.DataGenerator;
//import net.minecraftforge.client.model.generators.BlockStateProvider;
//import net.minecraftforge.client.model.generators.ExistingFileHelper;
//import net.minecraftforge.client.model.generators.ItemModelProvider;
//import net.minecraftforge.client.model.generators.ModelFile;
//import net.minecraftforge.eventbus.api.SubscribeEvent;
//import net.minecraftforge.fml.common.Mod;
//import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;
//
//import javax.annotation.Nonnull;
//
//import static codechicken.enderstorage.EnderStorage.MOD_ID;
//
///**
// * Created by covers1624 on 4/25/20.
// */
//@Mod.EventBusSubscriber (modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
//public class DataGenerators {
//
//    @SubscribeEvent
//    public static void gatherDataGenerators(GatherDataEvent event) {
//        DataGenerator gen = event.getGenerator();
//        ExistingFileHelper files = event.getExistingFileHelper();
//        if (event.includeClient()) {
//            gen.addProvider(new BlockStates(gen, files));
//            gen.addProvider(new ItemModels(gen, files));
//        }
//    }
//
//    private static class ItemModels extends ItemModelProvider {
//
//        public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
//            super(generator, MOD_ID, existingFileHelper);
//        }
//
//        @Override
//        protected void registerModels() {
//            getBuilder("ender_chest")//
//                    .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
//            getBuilder("ender_tank")//
//                    .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
//            getBuilder("ender_pouch")//
//                    .parent(new ModelFile.UncheckedModelFile("builtin/generated"));
//        }
//
//        @Override
//        public String getName() {
//            return "Ender Storage Item models.";
//        }
//    }
//
//    private static class BlockStates extends BlockStateProvider {
//
//        public BlockStates(DataGenerator gen, ExistingFileHelper exFileHelper) {
//            super(gen, MOD_ID, exFileHelper);
//        }
//
//        @Nonnull
//        @Override
//        public String getName() {
//            return "Ender Storage BlockStates.";
//        }
//
//        @Override
//        protected void registerStatesAndModels() {
//            ModelFile model = models()//
//                    .withExistingParent("dummy", "block")//
//                    .texture("particle", "minecraft:block/obsidian");
//            simpleBlock(ModContent.blockEnderChest, model);
//            simpleBlock(ModContent.blockEnderTank, model);
//        }
//    }
//}
