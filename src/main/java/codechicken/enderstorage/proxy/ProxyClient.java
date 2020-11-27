package codechicken.enderstorage.proxy;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.EnderPouchBakery;
import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.client.render.entity.TankLayerRenderer;
import codechicken.enderstorage.client.render.item.EnderChestItemRender;
import codechicken.enderstorage.client.render.item.EnderTankItemRender;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.init.ModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.model.ModelRegistryHelper;
import codechicken.lib.model.bakery.CCBakeryModel;
import codechicken.lib.model.bakery.ModelBakery;
import codechicken.lib.texture.SpriteRegistryHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.model.ModelResourceLocation;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static codechicken.enderstorage.init.ModContent.*;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ProxyClient extends Proxy {

    public static SpriteRegistryHelper spriteHelper = new SpriteRegistryHelper();
    public static ModelRegistryHelper modelHelper = new ModelRegistryHelper();

    static {
        spriteHelper.addIIconRegister(EnderPouchBakery.INSTANCE);
    }

    @Override
    public void clientSetup(FMLClientSetupEvent event) {
        ScreenManager.registerFactory(ModContent.containerItemStorage, GuiEnderItemStorage::new);

        ClientRegistry.bindTileEntityRenderer(tileEnderChestType, RenderTileEnderChest::new);
        ClientRegistry.bindTileEntityRenderer(tileEnderTankType, RenderTileEnderTank::new);

        if (!EnderStorageConfig.disableCreatorVisuals) {
            for (PlayerRenderer renderPlayer : Minecraft.getInstance().getRenderManager().getSkinMap().values()) {
                renderPlayer.addLayer(new TankLayerRenderer(renderPlayer));
            }
        }

        ModelResourceLocation invLocation = new ModelResourceLocation(itemEnderPouch.getRegistryName(), "inventory");
        modelHelper.register(invLocation, new CCBakeryModel());
        ModelBakery.registerItemKeyGenerator(itemEnderPouch, stack -> {
            Frequency frequency = Frequency.readFromStack(stack);
            boolean open = EnderStorageManager.instance(true).getStorage(frequency, EnderItemStorage.TYPE).openCount() > 0;
            return ModelBakery.defaultItemKeyGenerator.generateKey(stack) + "|" + frequency.toModelLoc() + "|" + open;
        });

        modelHelper.register(new ModelResourceLocation(itemEnderChest.getRegistryName(), "inventory"), new EnderChestItemRender());
        modelHelper.register(new ModelResourceLocation(itemEnderTank.getRegistryName(), "inventory"), new EnderTankItemRender());
    }
}
