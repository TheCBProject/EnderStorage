package codechicken.enderstorage.client.render.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.render.tile.RenderTileEnderChest;
import codechicken.lib.render.item.IItemRenderer;
import codechicken.lib.util.TransformUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.IModelState;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 4/27/2016.
 */
public class EnderChestItemRender implements IItemRenderer {

    @Override
    public void renderItem(ItemStack item, TransformType transformType) {

        GlStateManager.pushMatrix();

        Frequency frequency = Frequency.fromItemStack(item);
        RenderTileEnderChest.renderChest(2, frequency, 0, 0, 0, 0, 0F);

        //Fixes issues with inventory rendering.
        //The Portal renderer modifies blend and disables it.
        //Vanillas inventory relies on the fact that items don't modify gl so it never bothers to set it again.
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.popMatrix();
    }

    @Override
    public IModelState getTransforms() {
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
}
