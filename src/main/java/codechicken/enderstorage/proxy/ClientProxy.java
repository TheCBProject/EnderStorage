package codechicken.enderstorage.proxy;

import codechicken.core.CCUpdateChecker;
import codechicken.enderstorage.client.render.RenderTileEnderChest;
import codechicken.enderstorage.client.render.RenderTileEnderTank;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.init.ModBlocks;
import codechicken.enderstorage.network.EnderStorageCPH;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.ItemModelMesher;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.registry.ClientRegistry;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        if (ConfigurationHandler.clientCheckUpdates) {
            CCUpdateChecker.updateCheck("EnderStorage");
        }
        super.init();
        PacketCustom.assignHandler(EnderStorageCPH.channel, new EnderStorageCPH());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderChest.class, new RenderTileEnderChest());
        ClientRegistry.bindTileEntitySpecialRenderer(TileEnderTank.class, new RenderTileEnderTank());
        ItemModelMesher mesher = Minecraft.getMinecraft().getRenderItem().getItemModelMesher();

        mesher.register(Item.getItemFromBlock(ModBlocks.blockEnderStorage), new ItemMeshDefinition() {
            @Override
            public ModelResourceLocation getModelLocation(ItemStack stack) {
                switch (stack.getItemDamage() >> 12) {
                case 0:
                    return new ModelResourceLocation("enderstorage:enderStorage", "type=enderChest");
                case 1:
                    return new ModelResourceLocation("enderstorage:enderStorage", "type=enderTank");
                }
                return null;
            }
        });
    }
}
