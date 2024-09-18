package codechicken.enderstorage.client.render.entity;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.lib.fluid.FluidUtils;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Vector3;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Created by covers1624 on 15/12/2016.
 */
public class TankLayerRenderer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    private static final String[] UUID_STRINGS = {
            "c85f3fd3-1754-45ec-ab3d-a33d6312dfef",
            "c501d550-7e3c-463e-8a95-256f86d9a47d",
            "cf3e2c7e-d703-48e0-808e-f139bf26ff9d",
            "44ba40ef-fd8a-446f-834b-5aea42119c92"
    };
    private static final Set<UUID> UUIDS = Arrays.stream(UUID_STRINGS)
            .map(UUID::fromString)
            .collect(Collectors.toSet());
    private static final Frequency BLANK = new Frequency();

    public TankLayerRenderer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack mStack, MultiBufferSource buffers, int packedLight, AbstractClientPlayer entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        if (UUIDS.contains(entity.getUUID())) {
            CCRenderState ccrs = CCRenderState.instance();
            ccrs.brightness = packedLight;
            ccrs.overlay = OverlayTexture.NO_OVERLAY;
            Matrix4 mat = new Matrix4(mStack);
            mat.rotate(MathHelper.torad * 180, Vector3.X_POS);
            mat.scale(0.5);
            if (entity.isCrouching()) {
                mat.translate(0, -0.5, 0);
            }
            if (entity.isFallFlying()) {
                headPitch = -45;
            }
            mat.rotate(netHeadYaw * MathHelper.torad, Vector3.Y_NEG);
            mat.rotate(headPitch * MathHelper.torad, Vector3.X_POS);
            mat.translate(-0.5, 1, -0.5);
            RenderTileEnderTank.renderTank(ccrs, mat, buffers, 0, (float) (MathHelper.torad * 90F), BLANK, 0);

            FluidStack stack = FluidUtils.water.copy();
            float bob = 0.45F + RenderUtils.getPearlBob(ClientUtils.getRenderTime()) * 2;
            stack.setAmount((int) MathHelper.map(bob, 0.2, 0.6, 1000, 14000));
            mat.translate(-0.5, 0, -0.5);
            RenderTileEnderTank.renderFluid(ccrs, mat, buffers, stack);
        }
    }
}
