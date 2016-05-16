package codechicken.enderstorage.client;

import codechicken.enderstorage.api.Colour;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.model.EnderPouchModelBakery;
import codechicken.enderstorage.reference.Reference;
import codechicken.enderstorage.repack.covers1624.lib.util.ArrayUtils;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.render.TextureUtils;
import codechicken.lib.render.TransformUtils;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.client.resources.IResourceManagerReloadListener;
import net.minecraft.util.ResourceLocation;

import java.util.*;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class EnderPouchModelLoader implements TextureUtils.IIconRegister, IResourceManagerReloadListener {

    public static final ImmutableList<String> bagModelVariants;
    public static final Map<String, IBakedModel> modelCache = new HashMap<String, IBakedModel>();
    private static boolean cacheGenerated = false;

    static {
        ImmutableList.Builder<String> builder = ImmutableList.builder();
        for (int l = 0; l < 16; l++) {
            for (int m = 0; m < 16; m++) {
                for (int r = 0; r < 16; r++) {
                    for (int owned = 0; owned < 2; owned++) {
                        for (int open = 0; open < 2; open++) {
                            Frequency frequency = new Frequency(l, m, r);
                            if (owned != 0) {
                                frequency.setOwner("dummy");
                            }
                            builder.add(frequency.toModelLoc() + ",open=" + (open != 0));
                        }
                    }
                }
            }
        }
        bagModelVariants = builder.build();
    }

    @Override
    public void onResourceManagerReload(IResourceManager resourceManager) {
        clearCache();
    }

    private static void clearCache() {
        synchronized (modelCache) {
            if (cacheGenerated) {
                cacheGenerated = false;
                modelCache.clear();
            }
        }
    }

    public static IBakedModel getModel(String key) {
        if (!modelCache.containsKey(key)) {
            IBakedModel model = generateModel(key);
            if (model == null) {
                return null;
            }
            modelCache.put(key, model);
        }
        return modelCache.get(key);
    }

    private static IBakedModel generateModel(String key) {
        Map<String, String> values = ArrayUtils.convertKeyValueArrayToMap(key.split(","));
        if (!ArrayUtils.containsKeys(values, "owned", "open", "left", "middle", "right")) {
            LogHelper.warn("Invalid key for EnderPouch model [%s]!", key);
            return null;
        }
        boolean owned = Boolean.parseBoolean(values.get("owned"));
        boolean open = Boolean.parseBoolean(values.get("open"));
        String pouchPrefix = Reference.MOD_PREFIX + "items/pouch/";
        //button,colour.
        String buttonsPrefix = pouchPrefix + "buttons/%s/%s";
        ResourceLocation leftButton = new ResourceLocation(String.format(buttonsPrefix, "left", values.get("left")));
        ResourceLocation middleButton = new ResourceLocation(String.format(buttonsPrefix, "middle", values.get("middle")));
        ResourceLocation rightButton = new ResourceLocation(String.format(buttonsPrefix, "right", values.get("right")));
        ResourceLocation bagLocation;
        if (open) {
            if (owned) {
                bagLocation = new ResourceLocation(pouchPrefix + "owned_open");
            } else {
                bagLocation = new ResourceLocation(pouchPrefix + "open");
            }
        } else {
            if (owned) {
                bagLocation = new ResourceLocation(pouchPrefix + "owned_closed");
            } else {
                bagLocation = new ResourceLocation(pouchPrefix + "closed");
            }
        }
        Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
            @Override
            public TextureAtlasSprite apply(ResourceLocation input) {
                return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
            }
        };
        EnderPouchModelBakery correctModel = new EnderPouchModelBakery(bagLocation, leftButton, middleButton, rightButton);
        return correctModel.bake(TransformUtils.DEFAULT_ITEM, DefaultVertexFormats.ITEM, bakedTextureGetter);
    }

    @Override
    public void registerIcons(TextureMap textureMap) {
        for (ResourceLocation location : getTextures()) {
            textureMap.registerSprite(location);
        }
    }

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

}
