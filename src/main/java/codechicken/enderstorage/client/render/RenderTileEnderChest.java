package codechicken.enderstorage.client.render;

import codechicken.core.ClientUtils;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.RenderUtils;
import codechicken.enderstorage.client.model.ModelEnderChest;
import codechicken.enderstorage.client.model.RenderCustomEndPortal;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.misc.EnderDyeButton;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.render.CCModelLibrary;
import codechicken.lib.render.CCRenderState;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Vector3;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

/**
 * Created by covers1624 on 4/12/2016.
 */
public class RenderTileEnderChest extends TileEntitySpecialRenderer<TileEnderChest> {
    private static ModelEnderChest model = new ModelEnderChest();
    public static final double phi = 1.618034;

    private static RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.626, 0.188, 0.812, 0.188, 0.812);

    @Override
    public void renderTileEntityAt(TileEnderChest enderChest, double x, double y, double z, float partialTicks, int destroyStage) {

        CCRenderState.reset();
        CCRenderState.setBrightness(enderChest.getWorld(), enderChest.getPos());
        boolean owned = !enderChest.owner.equals("global");
        int rotation = enderChest.rotation;
        Frequency freq = enderChest.frequency;
        int offset = RenderUtils.getTimeOffset(enderChest.getPos());
        float lidAngle = (float) enderChest.getRadianLidAngle(partialTicks);
        renderChest(rotation, freq, owned, x, y, z, offset, lidAngle);
    }

    public static void renderChest(int rotation, Frequency freq, boolean owned, double x, double y, double z, int offset, float lidAngle) {
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        renderEndPortal.render(x, y, z, 0, info.entityX, info.entityY, info.entityZ, info.renderEngine);
        GlStateManager.color(1, 1, 1, 1);

        CCRenderState.changeTexture("enderstorage:textures/enderchest.png");
        GlStateManager.pushMatrix();
        GlStateManager.enableRescaleNormal();
        GlStateManager.color(1, 1, 1, 1);
        GlStateManager.translate(x, y + 1.0, z + 1.0F);
        GlStateManager.scale(1.0F, -1F, -1F);
        GlStateManager.translate(0.5F, 0.5F, 0.5F);
        GlStateManager.rotate(rotation * 90, 0.0F, 1.0F, 0.0F);
        GlStateManager.translate(-0.5F, -0.5F, -0.5F);
        model.chestLid.rotateAngleX = lidAngle;
        model.render(owned);
        GlStateManager.popMatrix();

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        renderButtons(freq, rotation, lidAngle);
        GlStateManager.popMatrix();

        double time = ClientUtils.getRenderTime() + offset;
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.2 + lidAngle * -0.5 + RenderUtils.getPearlBob(time), z + 0.5), new Rotation(time / 3, new Vector3(0, 1, 0)), 0.04);

        GlStateManager.disableLighting();
        CCRenderState.changeTexture("enderstorage:textures/hedronmap.png");
        GlStateManager.pushMatrix();
        CCRenderState.startDrawing(4, DefaultVertexFormats.POSITION_TEX_NORMAL);
        CCRenderState.pullBuffer();
        CCModelLibrary.icosahedron4.render(pearlMat);
        CCRenderState.draw();
        GlStateManager.popMatrix();
        GlStateManager.enableLighting();
    }

    private static void renderButtons(Frequency freq, int rot, double lidAngle) {
        CCRenderState.changeTexture("enderstorage:textures/buttons.png");

        drawButton(0, freq.left, rot, lidAngle);
        drawButton(1, freq.middle, rot, lidAngle);
        drawButton(2, freq.right, rot, lidAngle);
    }

    private static void drawButton(int button, int colour, int rot, double lidAngle) {
        float texx = 0.25F * (colour % 4);
        float texy = 0.25F * (colour / 4);

        GL11.glPushMatrix();

        EnderDyeButton ebutton = TileEnderChest.buttons[button].copy();
        ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, lidAngle);
        ebutton.rotateMeta(rot);
        Vector3[] verts = ebutton.verts;

        Tessellator tessellator = Tessellator.getInstance();
        tessellator.getBuffer().begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        addVecWithUV(verts[7], texx + 0.0938, texy + 0.0625);
        addVecWithUV(verts[3], texx + 0.0938, texy + 0.1875);
        addVecWithUV(verts[2], texx + 0.1562, texy + 0.1875);
        addVecWithUV(verts[6], texx + 0.1562, texy + 0.0625);

        addVecWithUV(verts[4], texx + 0.0938, texy + 0.0313);
        addVecWithUV(verts[7], texx + 0.0938, texy + 0.0313);
        addVecWithUV(verts[6], texx + 0.1562, texy + 0.0624);
        addVecWithUV(verts[5], texx + 0.1562, texy + 0.0624);

        addVecWithUV(verts[0], texx + 0.0938, texy + 0.2186);
        addVecWithUV(verts[1], texx + 0.1562, texy + 0.2186);
        addVecWithUV(verts[2], texx + 0.1562, texy + 0.1876);
        addVecWithUV(verts[3], texx + 0.0938, texy + 0.1876);

        addVecWithUV(verts[6], texx + 0.1563, texy + 0.0626);
        addVecWithUV(verts[2], texx + 0.1563, texy + 0.1874);
        addVecWithUV(verts[1], texx + 0.1874, texy + 0.1874);
        addVecWithUV(verts[5], texx + 0.1874, texy + 0.0626);

        addVecWithUV(verts[7], texx + 0.0937, texy + 0.0626);
        addVecWithUV(verts[4], texx + 0.0626, texy + 0.0626);
        addVecWithUV(verts[0], texx + 0.0626, texy + 0.1874);
        addVecWithUV(verts[3], texx + 0.0937, texy + 0.1874);
        tessellator.draw();

        GlStateManager.popMatrix();
    }

    private static void addVecWithUV(Vector3 vec, double u, double v) {
        Tessellator.getInstance().getBuffer().pos(vec.x, vec.y, vec.z).tex(u, v).endVertex();
    }
}
