package codechicken.enderstorage.client;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.reference.Reference;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.model.PerspectiveAwareModelProperties;
import codechicken.lib.model.bakery.ItemModelBakery;
import codechicken.lib.model.blockbakery.IItemBakery;
import codechicken.lib.texture.TextureUtils.IIconRegister;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 17/02/2017.
 */
public class EnderPouchBakery implements IItemBakery, IIconRegister {

    public static final EnderPouchBakery INSTANCE = new EnderPouchBakery();

    // [Ownded(0/1)][Open(0/1)]
    public static TextureAtlasSprite[][] BAG_TEXTURES;
    // [Button(0/1/2)(l/m/r)][Colour(EnumColour.ordinal)]
    public static TextureAtlasSprite[][] COLOUR_TEXTURES;

    @Override
    public List<BakedQuad> bakeItemQuads(EnumFacing face, ItemStack stack) {
        List<BakedQuad> quads = new ArrayList<BakedQuad>();
        if (face == null) {
            Frequency frequency = Frequency.fromItemStack(stack);
            boolean open = ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(frequency, "item")).openCount() > 0;
            TextureAtlasSprite bagTexture = BAG_TEXTURES[frequency.hasOwner() ? 1 : 0][open ? 1 : 0];
            quads.addAll(ItemModelBakery.bakeItem(ImmutableList.of(bagTexture, COLOUR_TEXTURES[0][frequency.left], COLOUR_TEXTURES[1][frequency.middle], COLOUR_TEXTURES[2][frequency.right])));
        }
        return quads;
    }

    @Override
    public PerspectiveAwareModelProperties getModelProperties(ItemStack stack) {
        return PerspectiveAwareModelProperties.DEFAULT_ITEM;
    }

    @Override
    public void registerIcons(TextureMap map) {

        String POUCH_PREFIX = Reference.MOD_PREFIX + "items/pouch/";
        String BUTTONS_PREFIX = POUCH_PREFIX + "buttons/";
        String[] position_prefixes = { "left/", "middle/", "right/" };

        BAG_TEXTURES = new TextureAtlasSprite[2][2];
        COLOUR_TEXTURES = new TextureAtlasSprite[3][16];

        BAG_TEXTURES[0][0] = register(map, POUCH_PREFIX + "closed");
        BAG_TEXTURES[0][1] = register(map, POUCH_PREFIX + "open");
        BAG_TEXTURES[1][0] = register(map, POUCH_PREFIX + "owned_closed");
        BAG_TEXTURES[1][1] = register(map, POUCH_PREFIX + "owned_open");

        for (int i = 0; i < 3; i++) {
            for (EnumColour colour : EnumColour.values()) {
                COLOUR_TEXTURES[i][colour.ordinal()] = register(map, BUTTONS_PREFIX + position_prefixes[i] + colour.getMinecraftName());
            }
        }

    }

    // Bouncer because reasons.
    private static TextureAtlasSprite register(TextureMap map, String sprite) {
        return map.registerSprite(new ResourceLocation(sprite));
    }
}
