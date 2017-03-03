package codechicken.enderstorage.client.render.entity;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.lib.fluid.FluidUtils;
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

import java.util.UUID;

/**
 * Created by covers1624 on 15/12/2016.
 */
public class TankLayerRenderer implements LayerRenderer<AbstractClientPlayer> {

    UUID uuid1 = UUID.fromString("c85f3fd3-1754-45ec-ab3d-a33d6312dfef");
    UUID uuid2 = UUID.fromString("c501d550-7e3c-463e-8a95-256f86d9a47d");
    UUID uuid3 = UUID.fromString("cf3e2c7e-d703-48e0-808e-f139bf26ff9d");
    UUID uuid4 = UUID.fromString("44ba40ef-fd8a-446f-834b-5aea42119c92");

    @Override
    public void doRenderLayer(AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        if (uuid1.equals(entity.getUniqueID()) || uuid2.equals(entity.getUniqueID()) || uuid3.equals(entity.getUniqueID()) || uuid4.equals(entity.getUniqueID())) {
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
