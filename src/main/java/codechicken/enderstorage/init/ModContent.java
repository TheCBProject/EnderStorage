package codechicken.enderstorage.init;

import codechicken.enderstorage.block.BlockEnderChest;
import codechicken.enderstorage.block.BlockEnderTank;
import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.enderstorage.item.ItemEnderPouch;
import codechicken.enderstorage.item.ItemEnderStorage;
import codechicken.enderstorage.recipe.CreateRecipe;
import codechicken.enderstorage.recipe.ReColourRecipe;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.inventory.container.ICCLContainerType;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.ObjectHolder;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 29/10/19.
 */
@ObjectHolder (MOD_ID)
@Mod.EventBusSubscriber (modid = MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModContent {

    //region Blocks.
    @ObjectHolder ("ender_chest")
    public static BlockEnderChest blockEnderChest;

    @ObjectHolder ("ender_tank")
    public static BlockEnderTank blockEnderTank;
    //endregion

    //region ItemBlocks
    @ObjectHolder ("ender_chest")
    public static ItemEnderStorage itemEnderChest;

    @ObjectHolder ("ender_tank")
    public static ItemEnderStorage itemEnderTank;
    //endregion

    //region Items
    @ObjectHolder ("ender_pouch")
    public static ItemEnderPouch itemEnderPouch;
    //endregion

    //region TileTypes
    @ObjectHolder ("ender_chest")
    public static TileEntityType<TileEnderChest> tileEnderChestType;

    @ObjectHolder ("ender_tank")
    public static TileEntityType<TileEnderTank> tileEnderTankType;
    //endregion

    //region Container Types.
    @ObjectHolder ("item_storage")
    public static ContainerType<ContainerEnderItemStorage> containerItemStorage;
    //endregion

    //region RecipeSerializers
    @ObjectHolder ("create_recipe")
    public static IRecipeSerializer<CreateRecipe> createRecipeSerializer;

    @ObjectHolder ("recolour_recipe")
    public static IRecipeSerializer<ReColourRecipe> reColourRecipeSerializer;
    //endregion

    @SubscribeEvent
    public static void onRegisterBlocks(RegistryEvent.Register<Block> event) {
        IForgeRegistry<Block> registry = event.getRegistry();

        Block.Properties properties = Block.Properties.of(Material.STONE)//
                .strength(20, 100);
        registry.register(new BlockEnderChest(properties).setRegistryName("ender_chest"));
        registry.register(new BlockEnderTank(properties).setRegistryName("ender_tank"));
    }

    @SubscribeEvent
    public static void onRegisterItems(RegistryEvent.Register<Item> event) {
        IForgeRegistry<Item> registry = event.getRegistry();

        // ItemBlocks
        registry.register(new ItemEnderStorage(blockEnderChest).setRegistryName("ender_chest"));
        registry.register(new ItemEnderStorage(blockEnderTank).setRegistryName("ender_tank"));

        //Items
        registry.register(new ItemEnderPouch().setRegistryName(MOD_ID, "ender_pouch"));
    }

    @SubscribeEvent
    public static void onRegisterTiles(RegistryEvent.Register<TileEntityType<?>> event) {
        IForgeRegistry<TileEntityType<?>> registry = event.getRegistry();
        registry.register(TileEntityType.Builder.of(TileEnderChest::new, blockEnderChest).build(null).setRegistryName("ender_chest"));
        registry.register(TileEntityType.Builder.of(TileEnderTank::new, blockEnderTank).build(null).setRegistryName("ender_tank"));
    }

    @SubscribeEvent
    public static void onRegisterContainers(RegistryEvent.Register<ContainerType<?>> event) {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();
        registry.register(ICCLContainerType.create(ContainerEnderItemStorage::new).setRegistryName("item_storage"));
    }

    @SubscribeEvent
    public static void onRegisterRecipeSerializers(RegistryEvent.Register<IRecipeSerializer<?>> event) {
        IForgeRegistry<IRecipeSerializer<?>> registry = event.getRegistry();
        registry.register(new CreateRecipe.Serializer().setRegistryName("create_recipe"));
        registry.register(new ReColourRecipe.Serializer().setRegistryName("recolour_recipe"));
    }
}
