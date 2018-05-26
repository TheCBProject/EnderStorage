package codechicken.enderstorage.client;

import codechicken.lib.model.DummyBakedModel;
import codechicken.lib.render.particle.IModelParticleProvider;
import codechicken.lib.texture.TextureUtils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nonnull;
import java.util.Collections;
import java.util.Set;

/**
 * Created by covers1624 on 27/05/18.
 */
public class ParticleDummyModel extends DummyBakedModel implements IModelParticleProvider, IResourceManagerReloadListener {

    public static final ParticleDummyModel INSTANCE = new ParticleDummyModel();
    private Set<TextureAtlasSprite> sprite;

    private Set<TextureAtlasSprite> getSprite() {
        if (sprite == null) {
            sprite = Collections.singleton(TextureUtils.getBlockTexture("obsidian"));
        }
        return sprite;
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        sprite = null;
    }

    @Override
    public Set<TextureAtlasSprite> getHitEffects(@Nonnull RayTraceResult traceResult, IBlockState state, IBlockAccess world, BlockPos pos) {
        return getSprite();
    }

    @Override
    public Set<TextureAtlasSprite> getDestroyEffects(IBlockState state, IBlockAccess world, BlockPos pos) {
        return getSprite();
    }

}
