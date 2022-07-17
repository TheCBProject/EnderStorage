package codechicken.enderstorage.client.render.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.lib.math.MathHelper;
import codechicken.lib.model.PerspectiveModelState;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 4/27/2016.
 */
public class EnderTankItemRender implements IItemRenderer {

    @Override
    public void renderItem(ItemStack stack, TransformType transformType, PoseStack poseStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        Frequency freq = Frequency.readFromStack(stack);
        FluidStack fluid = TankSynchroniser.getClientLiquid(freq);
        Matrix4 mat = new Matrix4(poseStack);
        RenderTileEnderTank.renderTank(ccrs, mat, source, 2, (float) (MathHelper.torad * 90F), freq, 0);
        mat.translate(-0.5, 0, -0.5);
        RenderTileEnderTank.renderFluid(ccrs, mat, source, fluid);
    }

    @Override
    public PerspectiveModelState getModelState() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean useAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean usesBlockLight() {
        return false;
    }
}
