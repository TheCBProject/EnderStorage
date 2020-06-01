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
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 15/12/2016.
 */
public class TankLayerRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private static final String[] UUID_STRINGS = {//
            "c85f3fd3-1754-45ec-ab3d-a33d6312dfef",//
            "c501d550-7e3c-463e-8a95-256f86d9a47d",//
            "cf3e2c7e-d703-48e0-808e-f139bf26ff9d",//
            "44ba40ef-fd8a-446f-834b-5aea42119c92"//
    };
    private static final Set<UUID> UUIDS = Arrays.stream(UUID_STRINGS)//
            .map(UUID::fromString)//
            .collect(Collectors.toSet());
    private static final Frequency BLANK = new Frequency();

    public TankLayerRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(AbstractClientPlayerEntity entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (UUIDS.contains(entity.getUniqueID())) {
            GlStateManager.pushMatrix();
            Matrix4 matrix4 = new Matrix4();

            matrix4.apply(new Rotation(MathHelper.torad * 180, new Vector3(1, 0, 0)));
            matrix4.apply(new Scale(0.5));

            matrix4.glApply();
            if (entity.isSneaking()) {
                GlStateManager.translated(0, -0.5, 0);
            }
            if (entity.isElytraFlying()) {
                headPitch = -45;
            }
            GlStateManager.rotated(netHeadYaw, 0, -1, 0);
            GlStateManager.rotated(headPitch, 1, 0, 0);
            GlStateManager.translated(0, 1, 0);
            RenderTileEnderTank.renderTank(CCRenderState.instance(), 0, (float) (MathHelper.torad * 90F), new Frequency(), -0.5, 0, -0.5, 0);
            FluidStack fluidStack = FluidUtils.water.copy();
            float bob = 0.45F + RenderUtils.getPearlBob(ClientUtils.getRenderTime()) * 2;
            fluidStack.setAmount((int) MathHelper.map(bob, 0.2, 0.6, 1000, 14000));
            RenderTileEnderTank.renderLiquid(fluidStack, -0.5, 0, -0.5);
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return false;
    }
}
