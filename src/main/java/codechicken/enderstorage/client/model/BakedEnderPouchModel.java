package codechicken.enderstorage.client.model;

import codechicken.enderstorage.client.BakedEnderPouchOverrideHandler;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.common.model.TRSRTransformation;
import org.apache.commons.lang3.tuple.Pair;

import javax.vecmath.Matrix4f;
import java.util.List;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class BakedEnderPouchModel implements IPerspectiveAwareModel {

    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
    private final ImmutableList<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    private final VertexFormat format;

    public BakedEnderPouchModel(ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms) {
        this.quads = quads;
        this.particle = particle;
        this.format = format;
        this.transforms = transforms;
    }

    @Override
    public ItemOverrideList getOverrides() {
        return BakedEnderPouchOverrideHandler.INSTANCE;
    }

    @Override
    public Pair<? extends IBakedModel, Matrix4f> handlePerspective(ItemCameraTransforms.TransformType cameraTransformType) {
        return IPerspectiveAwareModel.MapWrapper.handlePerspective(this, transforms, cameraTransformType);
    }

    @Override
    public List<BakedQuad> getQuads(IBlockState state, EnumFacing side, long rand) {
        if (side == null) {
            return quads;
        }
        return ImmutableList.of();
    }

    public boolean isAmbientOcclusion() {
        return true;
    }

    public boolean isGui3d() {
        return false;
    }

    public boolean isBuiltInRenderer() {
        return false;
    }

    public TextureAtlasSprite getParticleTexture() {
        return particle;
    }

    public ItemCameraTransforms getItemCameraTransforms() {
        return ItemCameraTransforms.DEFAULT;
    }

    public VertexFormat getFormat() {
        return format;
    }
}
