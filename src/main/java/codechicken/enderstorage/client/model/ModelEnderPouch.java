package codechicken.enderstorage.client.model;

import codechicken.enderstorage.api.Colour;
import codechicken.enderstorage.reference.Reference;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Maps;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.IPerspectiveAwareModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by covers1624 on 5/12/2016.
 * Mostly based off the DynBucket model.
 */
public class ModelEnderPouch implements IModel {

    public static final IModel MODEL = new ModelEnderPouch();

    private final ResourceLocation basePouch;
    private final ResourceLocation leftButton;
    private final ResourceLocation middleButton;
    private final ResourceLocation rightButton;
    private final boolean isDummy;

    public ModelEnderPouch() {
        this.basePouch = null;
        this.leftButton = null;
        this.middleButton = null;
        this.rightButton = null;
        this.isDummy = true;
    }

    public ModelEnderPouch(ResourceLocation basePouch, ResourceLocation leftButton, ResourceLocation middleButton, ResourceLocation rightButton) {

        this.basePouch = basePouch;
        this.leftButton = leftButton;
        this.middleButton = middleButton;
        this.rightButton = rightButton;
        this.isDummy = false;
    }

    @Override
    public Collection<ResourceLocation> getDependencies() {
        return ImmutableList.of();
    }

    @Override
    public Collection<ResourceLocation> getTextures() {
        ImmutableSet.Builder<ResourceLocation> builder = ImmutableSet.builder();
        String pouchLocation = Reference.MOD_PREFIX + "items/pouch/";
        builder.add(new ResourceLocation(pouchLocation + "closed"));
        builder.add(new ResourceLocation(pouchLocation + "open"));
        builder.add(new ResourceLocation(pouchLocation + "owned_closed"));
        builder.add(new ResourceLocation(pouchLocation + "owned_open"));
        builder.addAll(addAllColours(pouchLocation + "buttons/left/"));
        builder.addAll(addAllColours(pouchLocation + "buttons/middle/"));
        builder.addAll(addAllColours(pouchLocation + "buttons/right/"));
        return builder.build();
    }

    private List<ResourceLocation> addAllColours(String locationParent) {
        ArrayList<ResourceLocation> locations = new ArrayList<ResourceLocation>();
        for (Colour colour : Colour.values()) {
            locations.add(new ResourceLocation(locationParent + colour.getMinecraftName()));
        }
        return locations;
    }

    @Override
    public IBakedModel bake(IModelState state, VertexFormat format, Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {
        ImmutableMap<TransformType, TRSRTransformation> transformMap = IPerspectiveAwareModel.MapWrapper.getTransforms(state);

        ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
        TextureAtlasSprite particle = null;
        if (!isDummy) {
            particle = bakedTextureGetter.apply(basePouch);
            ImmutableList<ResourceLocation> textures = ImmutableList.of(basePouch, leftButton, middleButton, rightButton);
            IBakedModel layerModel = new ItemLayerModel(textures).bake(state, format, bakedTextureGetter);
            builder.addAll(layerModel.getQuads(null, null, 0));
        }

        return new BakedEnderPouchModel(this, builder.build(), particle, format, Maps.immutableEnumMap(transformMap), Maps.<String, IBakedModel>newHashMap());
    }

    @Override
    public IModelState getDefaultState() {
        return TRSRTransformation.identity();
    }

    /**
     * Should be passed a map of textures.
     * <p/>
     * Base > Texture
     * Left > Texture
     * Middle > Texture
     * Right > Texture
     *
     * @param textures
     * @return
     */
    @Deprecated//May not be needed.
    public IModel retexture(ImmutableMap<String, String> textures) {
        ResourceLocation base = basePouch;
        ResourceLocation left = leftButton;
        ResourceLocation middle = middleButton;
        ResourceLocation right = rightButton;
        if (textures.containsKey("base")) {
            base = new ResourceLocation(textures.get("base"));
        }
        if (textures.containsKey("left")) {
            left = new ResourceLocation(textures.get("left"));
        }
        if (textures.containsKey("middle")) {
            middle = new ResourceLocation(textures.get("middle"));
        }
        if (textures.containsKey("right")) {
            right = new ResourceLocation(textures.get("right"));
        }
        //Return corrected model.
        return new ModelEnderPouch(base, left, middle, right);
    }
}