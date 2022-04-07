package codechicken.enderstorage.client.render.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.world.item.ItemStack;

/**
 * Created by covers1624 on 4/27/2016.
 */
public class EnderChestItemRender implements IItemRenderer {

    private final RenderTileEnderChest tileRender = new RenderTileEnderChest(null);

    //    @Override
    //    public void renderItem(ItemStack item, TransformType transformType) {
    //        GlStateManager.pushMatrix();
    //
    //        Frequency frequency = Frequency.readFromStack(item);
    //        RenderTileEnderChest.renderChest(2, frequency, 0, 0, 0, 0, 0F);
    //
    //        //Fixes issues with inventory rendering.
    //        //The Portal renderer modifies blend and disables it.
    //        //Vanillas inventory relies on the fact that items don't modify gl so it never bothers to set it again.
    //        GlStateManager.enableBlend();
    //        GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
    //        GlStateManager.popMatrix();
    //    }

    @Override
    public void renderItem(ItemStack stack, TransformType transformType, PoseStack poseStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();
        Frequency freq = Frequency.readFromStack(stack);
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        tileRender.renderChest(ccrs, poseStack, source, 2, freq, 0, 0);
    }

    @Override
    public ModelState getModelTransform() {
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
