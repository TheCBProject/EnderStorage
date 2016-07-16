package codechicken.enderstorage.client.render.tile;

import codechicken.core.ClientUtils;
import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.model.ButtonModelLibrary;
import codechicken.enderstorage.client.render.RenderCustomEndPortal;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.*;
import codechicken.lib.render.uv.UVTranslation;
import codechicken.lib.vec.*;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Map;

import static net.minecraft.client.renderer.GlStateManager.*;
import static net.minecraft.client.renderer.vertex.DefaultVertexFormats.POSITION_TEX_NORMAL;

public class RenderTileEnderTank extends TileEntitySpecialRenderer<TileEnderTank> {
    static CCModel tankModel;
    static CCModel valveModel;
    static CCModel[] buttons;
    static RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.1205, 0.24, 0.76, 0.24, 0.76);

    static {
        Map<String, CCModel> models = CCOBJParser.parseObjModels(new ResourceLocation("enderstorage", "models/endertank.obj"), new SwapYZ());
        ArrayList<CCModel> tankParts = new ArrayList<CCModel>();
        tankParts.add(models.get("Blazerod1"));
        tankParts.add(models.get("Blazerod2"));
        tankParts.add(models.get("Blazerod3"));
        tankParts.add(models.get("Blazerod4"));
        tankParts.add(models.get("Top"));
        tankParts.add(models.get("Top2"));
        tankParts.add(models.get("Base"));
        tankParts.add(models.get("Glass"));
        tankParts.add(models.get("Valvebase"));

        Transformation fix = new Translation(-0.0099 - 0.5, 0, -0.0027 - 0.5);

        tankModel = CCModel.combine(tankParts).apply(fix).computeNormals();
        valveModel = models.get("Valve").apply(fix).computeNormals();

        buttons = new CCModel[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = ButtonModelLibrary.button.copy().apply(TileEnderTank.buttonT[i].with(new Translation(-0.5, 0, -0.5)));
        }
    }

    @Override
    public void renderTileEntityAt(TileEnderTank enderTank, double x, double y, double z, float partialTicks, int breakProgress) {

        CCRenderState.reset();
        CCRenderState.setBrightness(enderTank.getWorld(), enderTank.getPos());
        renderTank(enderTank.rotation, (float) MathHelper.interpolate(enderTank.pressure_state.b_rotate, enderTank.pressure_state.a_rotate, partialTicks) * 0.01745F, enderTank.frequency, x, y, z, RenderUtils.getTimeOffset(enderTank.getPos()));
        renderLiquid(enderTank.liquid_state.c_liquid, x, y, z);
    }

    public static void renderTank(int rotation, float valve, Frequency freq, double x, double y, double z, int offset) {
        TileEntityRendererDispatcher info = TileEntityRendererDispatcher.instance;
        renderEndPortal.render(x, y, z, 0, info.entityX, info.entityY, info.entityZ, info.renderEngine);
        color(1, 1, 1, 1);

        enableRescaleNormal();
        pushMatrix();
        translate(x + 0.5, y, z + 0.5);
        rotate(-90 * (rotation + 2), 0, 1, 0);

        TextureUtils.changeTexture("enderstorage:textures/endertank.png");
        CCRenderState.startDrawing(4, DefaultVertexFormats.BLOCK);
        tankModel.render();
        valveModel.render(new Rotation(valve, new Vector3(0, 0, 1)).at(new Vector3(0, 0.4165, 0)), new UVTranslation(0, freq.hasOwner() ? 13 / 64D : 0));
        CCRenderState.draw();

        TextureUtils.changeTexture("enderstorage:textures/buttons.png");
        CCRenderState.startDrawing(7, POSITION_TEX_NORMAL);
        int[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            buttons[i].render(new UVTranslation(0.25 * (colours[i] % 4), 0.25 * (colours[i] / 4)));
        }
        CCRenderState.draw();
        popMatrix();

        double time = ClientUtils.getRenderTime() + offset;
        Matrix4 pearlMat = RenderUtils.getMatrix(new Vector3(x + 0.5, y + 0.45 + RenderUtils.getPearlBob(time) * 2, z + 0.5), new Rotation(time / 3, new Vector3(0, 1, 0)), 0.04);

        disableLighting();
        TextureUtils.changeTexture("enderstorage:textures/hedronmap.png");
        CCRenderState.startDrawing(4, POSITION_TEX_NORMAL);
        CCModelLibrary.icosahedron4.render(pearlMat);
        CCRenderState.draw();
        enableLighting();
    }

    public static void renderLiquid(FluidStack liquid, double x, double y, double z) {
        RenderUtils.renderFluidCuboidGL(liquid, new Cuboid6(0.22, 0.12, 0.22, 0.78, 0.121 + 0.63, 0.78).add(new Vector3(x, y, z)), liquid.amount / (16D * FluidUtils.B), 0.75);
    }
}