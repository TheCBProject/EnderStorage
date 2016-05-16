package codechicken.enderstorage.client.render;

import codechicken.enderstorage.api.Frequency;
import codechicken.lib.render.IItemRenderer;
import codechicken.lib.render.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Created by covers1624 on 4/27/2016.
 */
public class EnderTankItemRender implements IItemRenderer {
    @Override
    public void renderItem(ItemStack item) {
        GlStateManager.pushMatrix();
        //GlStateManager.disableLighting();

        Frequency frequency = Frequency.fromItemStack(item);
        FluidStack fluidStack = null;
        if (item.getItem() != null && item.getItem() instanceof IFluidContainerItem) {
            IFluidContainerItem fluidContainerItem = (IFluidContainerItem) item.getItem();
            fluidStack = fluidContainerItem.getFluid(item);
        }
        RenderTileEnderTank.renderTank(2, 0F, frequency, 0, 0, 0, 0);
        if (fluidStack != null) {
            //TODO
            //RenderTileEnderTank.renderLiquid(fluidStack, 0, 0, 0);
        }
        //GlStateManager.enableLighting();
        GlStateManager.popMatrix();
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        return null;
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
    public boolean isBuiltInRenderer() {
        return true;
    }

    @Override
    public TextureAtlasSprite getParticleTexture() {
        return null;
    }

    @Override
    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return ItemOverrideList.NONE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return MapWrapper.handlePerspective(this, TransformUtils.DEFAULT_BLOCK.getTransforms(), cameraTransformType);
    }
}
