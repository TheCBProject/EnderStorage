package codechicken.enderstorage.client;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.model.ItemQuadBakery;
import codechicken.lib.model.bakedmodels.ModelProperties.PerspectiveProperties;
import codechicken.lib.model.bakery.generation.IItemBakery;
import codechicken.lib.texture.AtlasRegistrar;
import codechicken.lib.texture.IIconRegister;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

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
    public List<BakedQuad> bakeItemQuads(Direction face, ItemStack stack) {
        List<BakedQuad> quads = new ArrayList<>();
        if (face == null) {
            Frequency frequency = Frequency.readFromStack(stack);
            boolean open = EnderStorageManager.instance(true).getStorage(frequency, EnderItemStorage.TYPE).openCount() > 0;
            TextureAtlasSprite bagTexture = BAG_TEXTURES[frequency.hasOwner() ? 1 : 0][open ? 1 : 0];
            TextureAtlasSprite leftButton = COLOUR_TEXTURES[0][frequency.getLeft().getWoolMeta()];
            TextureAtlasSprite middleButton = COLOUR_TEXTURES[1][frequency.getMiddle().getWoolMeta()];
            TextureAtlasSprite rightButton = COLOUR_TEXTURES[2][frequency.getRight().getWoolMeta()];
            quads.addAll(ItemQuadBakery.bakeItem(bagTexture, leftButton, middleButton, rightButton));
        }
        return quads;
    }

    @Override
    public PerspectiveProperties getModelProperties(ItemStack stack) {
        return PerspectiveProperties.DEFAULT_ITEM;
    }

    @Override
    public void registerIcons(AtlasRegistrar registrar) {
        String POUCH_PREFIX = "enderstorage:items/pouch/";
        String BUTTONS_PREFIX = POUCH_PREFIX + "buttons/";
        String[] position_prefixes = { "left/", "middle/", "right/" };

        BAG_TEXTURES = new TextureAtlasSprite[2][2];
        COLOUR_TEXTURES = new TextureAtlasSprite[3][16];

        registrar.registerSprite(POUCH_PREFIX + "closed", e -> BAG_TEXTURES[0][0] = e);
        registrar.registerSprite(POUCH_PREFIX + "open", e -> BAG_TEXTURES[0][1] = e);
        registrar.registerSprite(POUCH_PREFIX + "owned_closed", e -> BAG_TEXTURES[1][0] = e);
        registrar.registerSprite(POUCH_PREFIX + "owned_open", e -> BAG_TEXTURES[1][1] = e);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            for (EnumColour colour : EnumColour.values()) {
                registrar.registerSprite(BUTTONS_PREFIX + position_prefixes[i] + colour.getSerializedName(), e -> COLOUR_TEXTURES[finalI][colour.ordinal()] = e);
            }
        }

    }
}
