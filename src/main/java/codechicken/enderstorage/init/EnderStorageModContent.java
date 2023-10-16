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
import net.covers1624.quack.util.CrashLock;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 29/10/19.
 */
public class EnderStorageModContent {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");
    private static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MOD_ID);
    private static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MOD_ID);
    private static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, MOD_ID);
    private static final DeferredRegister<MenuType<?>> MENU_TYPES = DeferredRegister.create(ForgeRegistries.MENU_TYPES, MOD_ID);
    private static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, MOD_ID);

    private static final BlockBehaviour.Properties blockProps = Block.Properties.of()
            .mapColor(MapColor.STONE)
            .strength(20, 100);
    public static final RegistryObject<BlockEnderChest> ENDER_CHEST_BLOCK = BLOCKS.register("ender_chest", () -> new BlockEnderChest(blockProps));
    public static final RegistryObject<BlockEnderTank> ENDER_TANK_BLOCK = BLOCKS.register("ender_tank", () -> new BlockEnderTank(blockProps));

    public static final RegistryObject<ItemEnderStorage> ENDER_CHEST_ITEM = ITEMS.register("ender_chest", () -> new ItemEnderStorage(ENDER_CHEST_BLOCK.get()));
    public static final RegistryObject<ItemEnderStorage> ENDER_TANK_ITEM = ITEMS.register("ender_tank", () -> new ItemEnderStorage(ENDER_TANK_BLOCK.get()));

    public static final RegistryObject<ItemEnderPouch> ENDER_POUCH = ITEMS.register("ender_pouch", ItemEnderPouch::new);

    public static final RegistryObject<BlockEntityType<TileEnderChest>> ENDER_CHEST_TILE = BLOCK_ENTITY_TYPES.register("ender_chest", () ->
            BlockEntityType.Builder.of(TileEnderChest::new, ENDER_CHEST_BLOCK.get()).build(null)
    );
    public static final RegistryObject<BlockEntityType<TileEnderTank>> ENDER_TANK_TILE = BLOCK_ENTITY_TYPES.register("ender_tank", () ->
            BlockEntityType.Builder.of(TileEnderTank::new, ENDER_TANK_BLOCK.get()).build(null)
    );

    public static final RegistryObject<MenuType<ContainerEnderItemStorage>> ENDER_ITEM_STORAGE = MENU_TYPES.register("ender_item_storage", () ->
            ICCLContainerType.create(ContainerEnderItemStorage::new)
    );

    public static final RegistryObject<RecipeSerializer<CreateRecipe>> CREATE_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("create_recipe",
            CreateRecipe.Serializer::new
    );
    public static final RegistryObject<RecipeSerializer<ReColourRecipe>> RECOLOUR_RECIPE_SERIALIZER = RECIPE_SERIALIZERS.register("recolour_recipe",
            ReColourRecipe.Serializer::new
    );

    public static void init() {
        LOCK.lock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        BLOCKS.register(bus);
        ITEMS.register(bus);
        BLOCK_ENTITY_TYPES.register(bus);
        MENU_TYPES.register(bus);
        RECIPE_SERIALIZERS.register(bus);
        bus.addListener(EnderStorageModContent::onCreativeTabBuild);
    }

    private static void onCreativeTabBuild(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            event.accept(ENDER_POUCH);
            event.accept(ENDER_CHEST_BLOCK);
            event.accept(ENDER_TANK_BLOCK);
        }
    }
}
