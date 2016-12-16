package codechicken.enderstorage.client.render.entity;

import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Scale;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 15/12/2016.
 */
public class TankLayerRenderer implements LayerRenderer<AbstractClientPlayer> {

    @Override
    public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if ("covers1624".equals(entity.getName()) || "chicken_bones".equals(entity.getName()) || "ecu".equals(entity.getName())) {
            GlStateManager.pushMatrix();
            Matrix4 matrix4 = new Matrix4();

            matrix4.apply(new Rotation(MathHelper.torad * 180, new Vector3(1, 0, 0)));
            matrix4.apply(new Scale(0.5));

            matrix4.glApply();
            if (entity.isSneaking()) {
                GlStateManager.translate(0, -0.5, 0);
            }
            if (entity.isElytraFlying()) {
                headPitch = -45;
            }
            GlStateManager.rotate(netHeadYaw, 0, -1, 0);
            GlStateManager.rotate(headPitch, 1, 0, 0);
            GlStateManager.translate(0, 1, 0);
            RenderTileEnderTank.renderTank(CCRenderState.instance(), 0, (float) (MathHelper.torad * 90F), new Frequency(), -0.5, 0, -0.5, 0);
            FluidStack fluidStack = FluidUtils.water.copy();
            float bob = 0.45F + RenderUtils.getPearlBob(ClientUtils.getRenderTime()) * 2;
            fluidStack.amount = (int) MathHelper.map(bob, 0.2, 0.6, 1000, 14000);
            RenderTileEnderTank.renderLiquid(fluidStack, -0.5, 0, -0.5);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
