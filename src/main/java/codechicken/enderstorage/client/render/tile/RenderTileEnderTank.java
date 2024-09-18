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
import codechicken.lib.render.model.OBJParser;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.*;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.fluids.FluidStack;

import java.util.Map;

public class RenderTileEnderTank implements BlockEntityRenderer<TileEnderTank> {

    private static final RenderType baseType = RenderType.entityCutout(new ResourceLocation("enderstorage:textures/endertank.png"));
    private static final RenderType buttonType = RenderType.entitySolid(new ResourceLocation("enderstorage:textures/buttons.png"));
    private static final RenderType pearlType = CCModelLibrary.getIcos4RenderType(new ResourceLocation("enderstorage:textures/hedronmap.png"));

    public static final CCModel tankModel;
    public static final CCModel valveModel;
    public static final CCModel[] buttons;
    public static final RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.1205, 0.24, 0.76, 0.24, 0.76);

    static {
        Map<String, CCModel> models = new OBJParser(new ResourceLocation("enderstorage:models/endertank.obj"))
                .quads()
                .swapYZ()
                .parse();
        Transformation fix = new Translation(-0.0099 - 0.5, 0, -0.0027 - 0.5);
        valveModel = models.remove("Valve").apply(fix).computeNormals();
        tankModel = CCModel.combine(models.values()).apply(fix).computeNormals().shrinkUVs(0.004);

        buttons = new CCModel[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = ButtonModelLibrary.button.copy().apply(BlockEnderTank.buttonT[i].with(new Translation(-0.5, 0, -0.5)));
        }
    }

    public RenderTileEnderTank(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(TileEnderTank enderTank, float partialTicks, PoseStack mStack, MultiBufferSource source, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        float valveRot = (float) MathHelper.interpolate(enderTank.pressure_state.b_rotate, enderTank.pressure_state.a_rotate, partialTicks) * 0.01745F;
        int pearlOffset = RenderUtils.getTimeOffset(enderTank.getBlockPos());
        Matrix4 mat = new Matrix4(mStack);
        renderTank(ccrs, mat.copy(), source, enderTank.rotation, valveRot, enderTank.getFrequency(), pearlOffset);
        renderFluid(ccrs, mat, source, enderTank.liquid_state.c_liquid);
        ccrs.reset();
    }

    public static void renderTank(CCRenderState ccrs, Matrix4 mat, MultiBufferSource buffers, int rotation, float valveRot, Frequency freq, int pearlOffset) {
        renderEndPortal.render(mat, buffers);
        ccrs.reset();
        mat.translate(0.5, 0, 0.5);
        mat.rotate((-90 * (rotation + 2)) * MathHelper.torad, Vector3.Y_POS);
        ccrs.bind(baseType, buffers);
        tankModel.render(ccrs, mat);
        Matrix4 valveMat = mat.copy().apply(new Rotation(valveRot, Vector3.Z_POS).at(new Vector3(0, 0.4165, 0)));
        valveModel.render(ccrs, valveMat, new UVTranslation(0, freq.hasOwner() ? 13 / 64D : 0));

        ccrs.bind(buttonType, buffers);
        EnumColour[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            //noinspection IntegerDivisionInFloatingPointContext
            buttons[i].render(ccrs, mat, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() / 4)));
        }

        double time = ClientUtils.getRenderTime() + pearlOffset;
        Matrix4 pearlMat = RenderUtils.getMatrix(mat.copy(), new Vector3(0, 0.45 + RenderUtils.getPearlBob(time) * 2, 0), new Rotation(time / 3, Vector3.Y_POS), 0.04);
        ccrs.brightness = 15728880;
        ccrs.bind(pearlType, buffers);
        CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
        ccrs.reset();
    }

    public static void renderFluid(CCRenderState ccrs, Matrix4 mat, MultiBufferSource getter, FluidStack stack) {
        RenderUtils.renderFluidCuboid(ccrs, mat, RenderUtils.getFluidRenderType(), getter, stack, new Cuboid6(0.22, 0.12, 0.22, 0.78, 0.121 + 0.63, 0.78), stack.getAmount() / (16D * FluidUtils.B), 0.75);
    }
}
