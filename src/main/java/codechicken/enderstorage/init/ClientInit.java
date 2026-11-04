package codechicken.enderstorage.init;

import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.client.model.BagFrequencySelectProperty;
import codechicken.enderstorage.client.model.BagOpenModelCondition;
import codechicken.enderstorage.client.model.BagOwnedModelCondition;
import codechicken.enderstorage.client.render.RenderCustomEndPortal;
import codechicken.enderstorage.client.render.entity.TankLayerRenderer;
import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.ClientTankSynchronizer;
import codechicken.enderstorage.network.EnderStorageNetworkClient;
import com.google.common.reflect.TypeToken;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.Entity;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.renderstate.RegisterRenderStateModifiersEvent;

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
        modBus.addListener(ClientInit::onRegisterSpecialModelRenderers);
        modBus.addListener(ClientInit::onAddRenderLayers);
        modBus.addListener(ClientInit::onRegisterRenderStateModifiers);
        modBus.addListener(ClientInit::onRegisterMenuScreens);
        modBus.addListener(ClientInit::onRegisterSelectModelProperties);
        modBus.addListener(ClientInit::onRegisterConditionalItemModelProperties);

        RenderCustomEndPortal.init(modBus);
        ClientTankSynchronizer.init(modBus);
        EnderStorageNetworkClient.initClient();
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
        BlockEntityRenderers.register(ENDER_CHEST_TILE.get(), RenderTileEnderChest::new);
        BlockEntityRenderers.register(ENDER_TANK_TILE.get(), RenderTileEnderTank::new);
    }

    private static void onRegisterSpecialModelRenderers(RegisterSpecialModelRendererEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "ender_chest"), EnderChestItemRender.Unbaked.MAP_CODEC);
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "ender_tank"), EnderTankItemRender.Unbaked.MAP_CODEC);
    }

    private static void onAddRenderLayers(EntityRenderersEvent.AddLayers event) {
        if (!EnderStorageConfig.disableCreatorVisuals) {
            for (var skin : event.getSkins()) {
                var skinRenderer = event.getPlayerRenderer(skin);
                assert skinRenderer != null;
                skinRenderer.addLayer(new TankLayerRenderer(skinRenderer));
            }
        }
    }

    private static void onRegisterRenderStateModifiers(RegisterRenderStateModifiersEvent event) {
        event.registerEntityModifier(new TypeToken<EntityRenderer<Entity, EntityRenderState>>() { }, (player, state) -> {
            state.setRenderData(TankLayerRenderer.PLAYER_UUID, player.getUUID());
        });
    }

    private static void onRegisterMenuScreens(RegisterMenuScreensEvent event) {
        event.register(ENDER_ITEM_STORAGE.get(), GuiEnderItemStorage::new);
    }

    private static void onRegisterSelectModelProperties(RegisterSelectItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "bag/frequency"), BagFrequencySelectProperty.TYPE);
    }

    private static void onRegisterConditionalItemModelProperties(RegisterConditionalItemModelPropertyEvent event) {
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "bag/open"), BagOpenModelCondition.MAP_CODEC);
        event.register(Identifier.fromNamespaceAndPath(MOD_ID, "bag/owned"), BagOwnedModelCondition.MAP_CODEC);
    }
}
