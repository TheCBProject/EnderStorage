package codechicken.enderstorage.client.model;

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
import java.util.Map;

/**
 * Created by covers1624 on 5/12/2016.
 * Mostly based off the DynBucket model.
 */
public class BakedEnderPouchModel implements IPerspectiveAwareModel {

    @Deprecated
    private final ModelEnderPouch parent;
    @Deprecated
    private final Map<String, IBakedModel> cache; // contains all the baked models since they'll never change
    private final ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms;
    private final ImmutableList<BakedQuad> quads;
    private final TextureAtlasSprite particle;
    private final VertexFormat format;

    public BakedEnderPouchModel(ModelEnderPouch parent, ImmutableList<BakedQuad> quads, TextureAtlasSprite particle, VertexFormat format, ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms, Map<String, IBakedModel> cache) {
        this.quads = quads;
        this.particle = particle;
        this.format = format;
        this.parent = parent;
        this.transforms = transforms;
        this.cache = cache;
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

    @Deprecated
    public Map<String, IBakedModel> getCache() {
        return cache;
    }

    @Deprecated
    public ModelEnderPouch getParent() {
        return parent;
    }

    public VertexFormat getFormat() {
        return format;
    }
}
