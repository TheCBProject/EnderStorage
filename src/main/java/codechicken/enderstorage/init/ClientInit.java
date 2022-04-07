package codechicken.enderstorage.init;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.EnderPouchBakery;
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
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.texture.SpriteRegistryHelper;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static codechicken.enderstorage.init.EnderStorageModContent.*;

/**
 * Created by covers1624 on 6/4/22.
 */
public class ClientInit {

    private static final CrashLock LOCK = new CrashLock("Already Initialized.");

    public static final SpriteRegistryHelper spriteHelper = new SpriteRegistryHelper();
    public static final ModelRegistryHelper modelHelper = new ModelRegistryHelper();

    public static void init() {
        LOCK.lock();
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        spriteHelper.addIIconRegister(EnderPouchBakery.INSTANCE);

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

        ModelResourceLocation invLocation = new ModelResourceLocation(ENDER_POUCH.get().getRegistryName(), "inventory");
        modelHelper.register(invLocation, new CCBakeryModel());
        ModelBakery.registerItemKeyGenerator(ENDER_POUCH.get(), stack -> {
            Frequency frequency = Frequency.readFromStack(stack);
            boolean open = EnderStorageManager.instance(true).getStorage(frequency, EnderItemStorage.TYPE).openCount() > 0;
            return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + frequency.toModelLoc() + "|" + open;
        });

        modelHelper.register(new ModelResourceLocation(ENDER_CHEST_ITEM.get().getRegistryName(), "inventory"), new EnderChestItemRender());
        modelHelper.register(new ModelResourceLocation(ENDER_TANK_ITEM.get().getRegistryName(), "inventory"), new EnderTankItemRender());
    }
}
