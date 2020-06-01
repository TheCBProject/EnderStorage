package codechicken.enderstorage.client.render.tile;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.block.BlockEnderTank;
import codechicken.enderstorage.client.model.ButtonModelLibrary;
import codechicken.enderstorage.client.render.RenderCustomEndPortal;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.fluid.FluidUtils;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.*;
import codechicken.lib.texture.TextureUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;

import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_COLOR_NORMAL;

public class RenderTileEnderTank extends TileEntityRenderer<TileEnderTank> {

    public static CCModel tankModel;
    public static CCModel valveModel;
    public static CCModel[] buttons;
    public static RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.1205, 0.24, 0.76, 0.24, 0.76);

    static {
        Map<String, CCModel> models = OBJParser.parseModels(new ResourceLocation("enderstorage", "models/endertank.obj"), new SwapYZ());
        Transformation fix = new Translation(-0.0099 - 0.5, 0, -0.0027 - 0.5);
        valveModel = models.remove("Valve").apply(fix).computeNormals();
        tankModel = CCModel.combine(models.values()).apply(fix).computeNormals().shrinkUVs(0.004);

        buttons = new CCModel[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = ButtonModelLibrary.button.copy().apply(BlockEnderTank.buttonT[i].with(new Translation(-0.5, 0, -0.5)));
        }
    }

    @Override
    public void render(TileEnderTank enderTank, double x, double y, double z, float partialTicks, int breakProgress) {
        CCRenderState ccrs = CCRenderState.instance();
        //CCRenderState.setBrightness(enderTank.getWorld(), enderTank.getPos());
        renderTank(ccrs, enderTank.rotation, (float) MathHelper.interpolate(enderTank.pressure_state.b_rotate, enderTank.pressure_state.a_rotate, partialTicks) * 0.01745F, enderTank.getFrequency(), x, y, z, RenderUtils.getTimeOffset(enderTank.getPos()));
        renderLiquid(enderTank.liquid_state.c_liquid, x, y, z);
    }

    public static void renderTank(CCRenderState ccrs, int rotation, float valve, Frequency freq, double x, double y, double z, int offset) {
        ccrs.reset();
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        renderEndPortal.render(x, y, z, info.renderInfo);
        GlStateManager.color4f(1, 1, 1, 1);

        GlStateManager.pushMatrix();
        GlStateManager.translated(x + 0.5, y, z + 0.5);
        GlStateManager.rotated(-90 * (rotation + 2), 0, 1, 0);

        TextureUtils.changeTexture("enderstorage:textures/endertank.png");
        ccrs.startDrawing(4, POSITION_TEX_COLOR_NORMAL);
        tankModel.render(ccrs);
        valveModel.render(ccrs, new Rotation(valve, new Vector3(0, 0, 1)).at(new Vector3(0, 0.4165, 0)), new UVTranslation(0, freq.hasOwner() ? 13 / 64D : 0));
        ccrs.draw();

        TextureUtils.changeTexture("enderstorage:textures/buttons.png");
        ccrs.startDrawing(7, POSITION_TEX_COLOR_NORMAL);
        EnumColour[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            buttons[i].render(ccrs, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() / 4)));
        }
        ccrs.draw();
        GlStateManager.popMatrix();

        double time = ClientUtils.getRenderTime() + offset;
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.45 + RenderUtils.getPearlBob(time) * 2, z + 0.5), new Rotation(time / 3, new Vector3(0, 1, 0)), 0.04);

        GlStateManager.disableLighting();
        TextureUtils.changeTexture("enderstorage:textures/hedronmap.png");
        ccrs.startDrawing(4, POSITION_TEX_COLOR_NORMAL);
        CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
        ccrs.draw();
        GlStateManager.enableLighting();
        ccrs.reset();
    }

    public static void renderLiquid(FluidStack liquid, double x, double y, double z) {
        RenderUtils.renderFluidCuboidGL(liquid, new Cuboid6(0.22, 0.12, 0.22, 0.78, 0.121 + 0.63, 0.78).add(new Vector3(x, y, z)), liquid.getAmount() / (16D * FluidUtils.B), 0.75);
    }
}
