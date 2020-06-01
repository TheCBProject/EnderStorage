package codechicken.enderstorage.client.render.tile;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.block.BlockEnderChest;
import codechicken.enderstorage.client.model.ButtonModelLibrary;
import codechicken.enderstorage.client.model.ModelEnderChest;
import codechicken.enderstorage.client.render.RenderCustomEndPortal;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.CCModel;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.render.RenderUtils;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

/**
 * Created by covers1624 on 4/12/2016.
 */
public class RenderTileEnderChest extends TileEntityRenderer<TileEnderChest> {

    private static final ModelEnderChest model = new ModelEnderChest();
    private static final RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.626, 0.188, 0.812, 0.188, 0.812);

    @Override
    public void render(TileEnderChest enderChest, double x, double y, double z, float partialTicks, int destroyStage) {
        renderChest(enderChest.rotation, enderChest.getFrequency(), x, y, z, RenderUtils.getTimeOffset(enderChest.getPos()), (float) enderChest.getRadianLidAngle(partialTicks));
    }

    public static void renderChest(int rotation, Frequency freq, double x, double y, double z, int offset, float lidAngle) {
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.reset();

        renderEndPortal.render(x, y, z, info.renderInfo);
        GlStateManager.color4f(1, 1, 1, 1);

        TextureUtils.changeTexture("enderstorage:textures/enderchest.png");
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color4f(1, 1, 1, 1);
        GlStateManager.translated(x, y + 1.0, z + 1.0F);
        GlStateManager.scalef(1.0F, -1F, -1F);
        GlStateManager.translatef(0.5F, 0.5F, 0.5F);
        GlStateManager.rotatef(rotation * 90, 0.0F, 1.0F, 0.0F);
        GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
        model.chestLid.rotateAngleX = lidAngle;
        model.render(freq.hasOwner());
        GlStateManager.popMatrix();

        //Buttons
        GlStateManager.pushMatrix();
        GlStateManager.translated(x, y, z);
        TextureUtils.changeTexture("enderstorage:textures/buttons.png");
        EnumColour[] colours = freq.toArray();
        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_NORMAL);
        for (int i = 0; i < 3; i++) {
            CCModel button = ButtonModelLibrary.button.copy();
            button.apply(BlockEnderChest.buttonT[i]);
            button.apply(new Translation(0.5, 0, 0.5));
            button.apply(new Rotation(lidAngle, 1, 0, 0).at(new Vector3(0, 9D / 16D, 1 / 16D)));
            button.apply(new Rotation((-90 * (rotation)) * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0, 0.5)));
            button.render(ccrs, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() / 4)));
        }
        ccrs.draw();
        GlStateManager.popMatrix();

        //Pearl
        GlStateManager.disableLighting();
        TextureUtils.changeTexture("enderstorage:textures/hedronmap.png");
        GlStateManager.pushMatrix();

        double time = ClientUtils.getRenderTime() + offset;
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.2 + lidAngle * -0.5 + RenderUtils.getPearlBob(time), z + 0.5), new Rotation(time / 3, new Vector3(0, 1, 0)), 0.04);
        ccrs.startDrawing(7, DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL);
        CCModelLibrary.icosahedron7.render(ccrs, pearlMat);
        ccrs.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }
}
