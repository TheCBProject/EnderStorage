package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.Shaders;
import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.client.render.entity.TankLayerRenderer;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.ClampedItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static codechicken.enderstorage.init.EnderStorageModContent.*;

/**
 * Created by covers1624 on 6/4/22.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static void init(IEventBus modBus) {
        LOCK.lock();

        modBus.addListener(ClientInit::onRegisterRenderers);
        modBus.addListener(ClientInit::onAddRenderLayers);
        modBus.addListener(ClientInit::onRegisterMenuScreens);
        modBus.addListener(ClientInit::onClientSetupEvent);
        Shaders.init(modBus);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ENDER_CHEST_TILE.get(), RenderTileEnderChest::new);
        BlockEntityRenderers.register(ENDER_TANK_TILE.get(), RenderTileEnderTank::new);
    }

    @SuppressWarnings ({ "rawtypes", "unchecked" })
    private static void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        if (!EnderStorageConfig.disableCreatorVisuals) {
            for (PlayerSkin.Model skin : event.getSkins()) {
                var skinRenderer = (LivingEntityRenderer) event.getSkin(skin);
                assert skinRenderer != null;
                skinRenderer.addLayer(new TankLayerRenderer(skinRenderer));
            }
        }
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ENDER_ITEM_STORAGE.get(), GuiEnderItemStorage::new);
    }

    private static void onClientSetupEvent(FMLClientSetupEvent event) {
        event.enqueueWork(ClientInit::registerPredicates);
    }

    private static void registerPredicates() {
        ItemProperties.register(
                ENDER_POUCH.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "owned"),
                (ClampedItemPropertyFunction) (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).hasOwner() ? 1 : 0
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "open"),
                (ClampedItemPropertyFunction) (pStack, pLevel, pEntity, pSeed) -> EnderStorageManager.instance(true).getStorage(Frequency.readFromStack(pStack), EnderItemStorage.TYPE).openCount()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "left"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).left().ordinal()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "middle"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).middle().ordinal()
        );
        ItemProperties.register(
                ENDER_POUCH.get(),
                ResourceLocation.fromNamespaceAndPath(MOD_ID, "right"),
                (pStack, pLevel, pEntity, pSeed) -> Frequency.readFromStack(pStack).right().ordinal()
        );
    }
}
