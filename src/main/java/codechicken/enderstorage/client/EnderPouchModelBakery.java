package codechicken.enderstorage.client;

import codechicken.enderstorage.client.model.BakedEnderPouchModel;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class EnderPouchModelBakery {

    private final ResourceLocation basePouch;
    private final ResourceLocation leftButton;
    private final ResourceLocation middleButton;
    private final ResourceLocation rightButton;

    public EnderPouchModelBakery(ResourceLocation basePouch, ResourceLocation leftButton, ResourceLocation middleButton, ResourceLocation rightButton) {

        this.basePouch = basePouch;
        this.leftButton = leftButton;
        this.middleButton = middleButton;
        this.rightButton = rightButton;
    }

    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        TextureAtlasSprite particle = bakedTextureGetter.apply(basePouch);
        ImmutableList<ResourceLocation> textures = ImmutableList.of(basePouch, leftButton, middleButton, rightButton);
        IBakedModel layerModel = new ItemLayerModel(textures).bake(state, format, bakedTextureGetter);
        builder.addAll(layerModel.getQuads(null, null, 0));

        return new BakedEnderPouchModel(builder.build(), particle, format, Maps.immutableEnumMap(transformMap));
    }
}