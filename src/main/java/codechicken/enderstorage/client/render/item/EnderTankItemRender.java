package codechicken.enderstorage.client.render.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderTank;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import codechicken.lib.vec.Matrix4;
import com.google.common.collect.ImmutableMap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraftforge.fluids.FluidStack;

/**
 * Created by covers1624 on 4/27/2016.
 */
public class EnderTankItemRender implements IItemRenderer {

    @Override
    public void renderItem(ItemStack stack, TransformType transformType, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        Frequency freq = Frequency.readFromStack(stack);
        FluidStack fluid = TankSynchroniser.getClientLiquid(freq);
        Matrix4 mat = new Matrix4(mStack);
        RenderTileEnderTank.renderTank(ccrs, mat, getter, 2, (float) (MathHelper.torad * 90F), 0, freq, 0);
        mat.translate(-0.5, 0, -0.5);
        RenderTileEnderTank.renderFluid(ccrs, mat, getter, fluid);
    }

    @Override
    public ImmutableMap<TransformType, TransformationMatrix> getTransforms() {
        return TransformUtils.DEFAULT_BLOCK;
    }

    @Override
    public boolean isAmbientOcclusion() {
        return false;
    }

    @Override
    public boolean isGui3d() {
        return false;
    }

    @Override
    public boolean isSideLit() {
        return false;
    }
}
