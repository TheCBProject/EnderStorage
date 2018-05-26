package codechicken.enderstorage.proxy;

import codechicken.enderstorage.client.EnderPouchBakery;
import codechicken.enderstorage.client.ParticleDummyModel;
import codechicken.enderstorage.client.render.entity.TankLayerRenderer;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.init.ModBlocks;
import codechicken.enderstorage.init.ModItems;
import codechicken.enderstorage.network.EnderStorageCPH;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ResourceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ProxyClient extends Proxy {

    @Override
    public void preInit() {
        super.preInit();
        TextureUtils.addIconRegister(EnderPouchBakery.INSTANCE);
        ModBlocks.registerModels();
        ModItems.registerModels();
        RenderTileEnderTank.loadModel();
        ResourceUtils.registerReloadListener(ParticleDummyModel.INSTANCE);
    }

    @Override
    public void init() {
        super.init();
        PacketCustom.assignHandler(EnderStorageCPH.channel, new EnderStorageCPH());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderChest.class, new RenderTileEnderChest());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderTank.class, new RenderTileEnderTank());

        for (RenderPlayer renderPlayer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) {
            renderPlayer.addLayer(new TankLayerRenderer());
        }
    }
}
