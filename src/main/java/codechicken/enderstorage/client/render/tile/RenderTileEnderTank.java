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
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.opengl.GL11;

import java.util.Map;

public class RenderTileEnderTank extends TileEntityRenderer<TileEnderTank> {

    private static final RenderType baseType = RenderType.getEntityCutout(new ResourceLocation("enderstorage:textures/endertank.png"));
    private static final RenderType buttonType = RenderType.getEntitySolid(new ResourceLocation("enderstorage:textures/buttons.png"));
    private static final RenderType pearlType = CCModelLibrary.getIcos4RenderType(new ResourceLocation("enderstorage:textures/hedronmap.png"), false);

    public static final CCModel tankModel;
    public static final CCModel valveModel;
    public static final CCModel[] buttons;
    public static final RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.1205, 0.24, 0.76, 0.24, 0.76);

    static {
        Map<String, CCModel> models = OBJParser.parseModels(new ResourceLocation("enderstorage:models/endertank.obj"), GL11.GL_QUADS, new SwapYZ());
        Transformation fix = new Translation(-0.0099 - 0.5, 0, -0.0027 - 0.5);
        valveModel = models.remove("Valve").apply(fix).computeNormals();
        tankModel = CCModel.combine(models.values()).apply(fix).computeNormals().shrinkUVs(0.004);

        buttons = new CCModel[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = ButtonModelLibrary.button.copy().apply(BlockEnderTank.buttonT[i].with(new Translation(-0.5, 0, -0.5)));
        }
    }

    public RenderTileEnderTank(TileEntityRendererDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(TileEnderTank enderTank, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        float valveRot = (float) MathHelper.interpolate(enderTank.pressure_state.b_rotate, enderTank.pressure_state.a_rotate, partialTicks) * 0.01745F;
        int pearlOffset = RenderUtils.getTimeOffset(enderTank.getPos());
        Matrix4 mat = new Matrix4(mStack);
        double yToCamera = enderTank.getPos().getY() - renderDispatcher.renderInfo.getProjectedView().y;
        renderTank(ccrs, mat.copy(), getter, enderTank.rotation, valveRot, yToCamera, enderTank.getFrequency(), pearlOffset);
        renderFluid(ccrs, mat, getter, enderTank.liquid_state.c_liquid);
        ccrs.reset();
    }

    public static void renderTank(CCRenderState ccrs, Matrix4 mat, IRenderTypeBuffer getter, int rotation, float valveRot, double yToCamera, Frequency freq, int pearlOffset) {
        renderEndPortal.render(mat, getter, yToCamera);
        ccrs.reset();
        mat.translate(0.5, 0, 0.5);
        mat.rotate((-90 * (rotation + 2)) * MathHelper.torad, Vector3.Y_POS);
        ccrs.bind(baseType, getter);
        tankModel.render(ccrs, mat);
        Matrix4 valveMat = mat.copy().apply(new Rotation(valveRot, Vector3.Z_POS).at(new Vector3(0, 0.4165, 0)));
        valveModel.render(ccrs, valveMat, new UVTranslation(0, freq.hasOwner() ? 13 / 64D : 0));

        ccrs.bind(buttonType, getter);
        EnumColour[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            //noinspection IntegerDivisionInFloatingPointContext
            buttons[i].render(ccrs, mat, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() / 4)));
        }

        double time = ClientUtils.getRenderTime() + pearlOffset;
        Matrix4 pearlMat = RenderUtils.getMatrix(mat.copy(), new Vector3(0, 0.45 + RenderUtils.getPearlBob(time) * 2, 0), new Rotation(time / 3, Vector3.Y_POS), 0.04);
        ccrs.brightness = 15728880;
        ccrs.bind(pearlType, getter);
        CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
        ccrs.reset();
    }

    public static void renderFluid(CCRenderState ccrs, Matrix4 mat, IRenderTypeBuffer getter, FluidStack stack) {
        RenderUtils.renderFluidCuboid(ccrs, mat, RenderUtils.getFluidRenderType(), getter, stack, new Cuboid6(0.22, 0.12, 0.22, 0.78, 0.121 + 0.63, 0.78), stack.getAmount() / (16D * FluidUtils.B), 0.75);
    }
}
