package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.Shaders;
import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.client.render.entity.TankLayerRenderer;
import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.model.ModelRegistryHelper;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static codechicken.enderstorage.init.EnderStorageModContent.*;

/**
 * Created by covers1624 on 6/4/22.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static final ModelRegistryHelper modelHelper = new ModelRegistryHelper();

    public static void init() {
        LOCK.lock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientInit::onRegisterRenderers);
        bus.addListener(ClientInit::onAddRenderLayers);
        bus.addListener(ClientInit::onClientSetupEvent);
        Shaders.init();
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ENDER_CHEST_TILE.get(), RenderTileEnderChest::new);
        BlockEntityRenderers.register(ENDER_TANK_TILE.get(), RenderTileEnderTank::new);
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    private static void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        if (!EnderStorageConfig.disableCreatorVisuals) {
            for (String skin : event.getSkins()) {
                var skinRenderer = (LivingEntityRenderer) event.getSkin(skin);
                assert skinRenderer != null;
                skinRenderer.addLayer(new TankLayerRenderer(skinRenderer));
            }
        }
    }

    private static void onClientSetupEvent(FMLClientSetupEvent event) {
        MenuScreens.register(ENDER_ITEM_STORAGE.get(), GuiEnderItemStorage::new);

        event.enqueueWork(ClientInit::registerPredicates);

        modelHelper.register(new ModelResourceLocation(ENDER_CHEST_ITEM.getId(), "inventory"), new EnderChestItemRender());
        modelHelper.register(new ModelResourceLocation(ENDER_TANK_ITEM.getId(), "inventory"), new EnderTankItemRender());
    }

    private static void registerPredicates() {
        ItemProperties.register(
                ENDER_POUCH.get(),
                new ResourceLocation(MOD_ID, "owned"),
                (ClampedItemPropertyFunction) (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).hasOwner() ? 1 : 0
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                new ResourceLocation(MOD_ID, "open"),
                (ClampedItemPropertyFunction) (pStack, pLevel, pEntity, pSeed) -> EnderStorageManager.instance(true).getStorage(Frequency.readFromStack(pStack), EnderItemStorage.TYPE).openCount()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                new ResourceLocation(MOD_ID, "left"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).getLeft().ordinal()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                new ResourceLocation(MOD_ID, "middle"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).getMiddle().ordinal()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                new ResourceLocation(MOD_ID, "right"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).getRight().ordinal()
        );
    }


}
