package codechicken.enderstorage.client;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.model.ModelEnderPouch;
import codechicken.enderstorage.reference.Reference;
import codechicken.enderstorage.repack.covers1624.lib.util.ArrayUtils;
import codechicken.enderstorage.util.LogHelper;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by covers1624 on 5/12/2016.
 */
public class EnderPouchModelLoader implements ICustomModelLoader {

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
    public boolean accepts(ResourceLocation modelLocation) {
        return modelLocation.getResourceDomain().contains(Reference.MOD_ID.toLowerCase()) && modelLocation.getResourcePath().contains("enderPouch");
    }

    @Override
    public IModel loadModel(ResourceLocation modelLocation) throws Exception {
        checkCacheGen();
        return ModelEnderPouch.MODEL;
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

    public static void checkCacheGen() {
        synchronized (modelCache) {
            if (!cacheGenerated) {
                LogHelper.info("Starting EnderPouch Model generation.");
                for (String variant : bagModelVariants) {
                    Map<String, String> values = ArrayUtils.convertKeyValueArrayToMap(variant.split(","));
                    boolean owned = Boolean.parseBoolean(values.get("owned"));
                    boolean open = Boolean.parseBoolean(values.get("open"));
                    String pouchPrefix = Reference.MOD_PREFIX + "items/pouch/";
                    //button,colour.
                    String buttonsPrefix = pouchPrefix + "%s/%s";
                    ResourceLocation leftButton = new ResourceLocation(String.format(buttonsPrefix, "left", values.get("left")));
                    ResourceLocation middleButton = new ResourceLocation(String.format(buttonsPrefix, "middle", values.get("middle")));
                    ResourceLocation rightButton = new ResourceLocation(String.format(buttonsPrefix, "right", values.get("right")));
                    ResourceLocation bagLocation = null;
                    if (owned && open) {
                        bagLocation = new ResourceLocation(pouchPrefix + "owned_open");
                    } else if (!owned && open) {
                        bagLocation = new ResourceLocation(pouchPrefix + "open");
                    } else if (!owned && !open) {
                        bagLocation = new ResourceLocation(pouchPrefix + "closed");
                    } else if (owned && !open) {
                        bagLocation = new ResourceLocation(pouchPrefix + "owned_closed");
                    }
                    Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
                        @Override
                        public TextureAtlasSprite apply(ResourceLocation input) {
                            return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(input.toString());
                        }
                    };
                    ModelEnderPouch correctModel = new ModelEnderPouch(bagLocation, leftButton, middleButton, rightButton);
                    IBakedModel cachedModel = correctModel.bake(ModelEnderPouch.MODEL.getDefaultState(), DefaultVertexFormats.ITEM, bakedTextureGetter);
                    modelCache.put(variant, cachedModel);
                }
                LogHelper.info("Finished EnderPouch Model generation.");
            }
        }
    }
}
