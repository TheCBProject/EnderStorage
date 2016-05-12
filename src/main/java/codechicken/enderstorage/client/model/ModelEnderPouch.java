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
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

import javax.vecmath.Vector3f;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by covers1624 on 5/12/2016.
 * Mostly based off the DynBucket model.
 * Well not really, but that was my inspiration.
 */
public class ModelEnderPouch implements IModel {

    public static final IModel MODEL = new ModelEnderPouch();
    private static final TRSRTransformation flipX = new TRSRTransformation(null, null, new Vector3f(-1, 1, 1), null);

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

        return new BakedEnderPouchModel(builder.build(), particle, format, Maps.immutableEnumMap(transformMap));
    }

    @Override
    public IModelState getDefaultState() {
        TRSRTransformation thirdperson = get(0, 3, 1, 0, 0, 0, 0.55f);
        TRSRTransformation firstperson = get(1.13f, 3.2f, 1.13f, 0, -90, 25, 0.68f);
        ImmutableMap.Builder<TransformType, TRSRTransformation> builder = ImmutableMap.builder();
        builder.put(TransformType.GROUND, get(0, 2, 0, 0, 0, 0, 0.5f));
        builder.put(TransformType.HEAD, get(0, 13, 7, 0, 180, 0, 1));
        builder.put(TransformType.THIRD_PERSON_RIGHT_HAND, thirdperson);
        builder.put(TransformType.THIRD_PERSON_LEFT_HAND, leftify(thirdperson));
        builder.put(TransformType.FIRST_PERSON_RIGHT_HAND, firstperson);
        builder.put(TransformType.FIRST_PERSON_LEFT_HAND, leftify(firstperson));
        return new SimpleModelState(builder.build());
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

    private static TRSRTransformation get(float tx, float ty, float tz, float ax, float ay, float az, float s) {
        return TRSRTransformation.blockCenterToCorner(new TRSRTransformation(new Vector3f(tx / 16, ty / 16, tz / 16), TRSRTransformation.quatFromXYZDegrees(new Vector3f(ax, ay, az)), new Vector3f(s, s, s), null));
    }

    private static TRSRTransformation leftify(TRSRTransformation transform) {
        return TRSRTransformation.blockCenterToCorner(flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(flipX));
    }
}