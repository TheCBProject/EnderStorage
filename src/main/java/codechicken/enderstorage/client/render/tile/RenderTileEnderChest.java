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
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import codechicken.lib.vec.Rotation;
import codechicken.lib.vec.Translation;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.uv.UVTranslation;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Quaternion;

/**
 * Created by covers1624 on 4/12/2016.
 */
public class RenderTileEnderChest extends TileEntityRenderer<TileEnderChest> {

    private static final RenderType chestType = RenderType.entityCutout(new ResourceLocation("enderstorage:textures/enderchest.png"));
    private static final RenderType buttonType = RenderType.entitySolid(new ResourceLocation("enderstorage:textures/buttons.png"));
    private static final RenderType pearlType = CCModelLibrary.getIcos4RenderType(new ResourceLocation("enderstorage:textures/hedronmap.png"), false);
    private static final ModelEnderChest model = new ModelEnderChest();
    private static final RenderCustomEndPortal renderEndPortal = new RenderCustomEndPortal(0.626, 0.188, 0.812, 0.188, 0.812);

    public RenderTileEnderChest(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TileEnderChest enderChest, float partialTicks, MatrixStack mStack, IRenderTypeBuffer getter, int packedLight, int packedOverlay) {
        CCRenderState ccrs = CCRenderState.instance();
        ccrs.brightness = packedLight;
        ccrs.overlay = packedOverlay;
        double yToCamera = enderChest.getBlockPos().getY() - renderer.camera.getPosition().y;
        renderChest(ccrs, mStack, getter, enderChest.rotation, yToCamera, enderChest.getFrequency(), (float) enderChest.getRadianLidAngle(partialTicks), RenderUtils.getTimeOffset(enderChest.getBlockPos()));
    }

    public static void renderChest(CCRenderState ccrs, MatrixStack mStack, IRenderTypeBuffer getter, int rotation, double yToCamera, Frequency freq, float lidAngle, int pearlOffset) {
        Matrix4 mat = new Matrix4(mStack);
        if (lidAngle != 0) {//Micro optimization, lid closed, dont render starfield.
            renderEndPortal.render(mat, getter, yToCamera);
        }
        ccrs.reset();
        mStack.pushPose();
        mStack.translate(0, 1.0, 1.0);
        mStack.scale(1.0F, -1.0F, -1.0F);
        mStack.translate(0.5, 0.5, 0.5);
        mStack.mulPose(new Quaternion(0, rotation * 90, 0, true));
        mStack.translate(-0.5, -0.5, -0.5);
        model.chestLid.xRot = lidAngle;
        model.render(mStack, getter.getBuffer(chestType), ccrs.brightness, ccrs.overlay, freq.hasOwner());
        mStack.popPose();

        //Buttons
        ccrs.bind(buttonType, getter);
        EnumColour[] colours = freq.toArray();
        for (int i = 0; i < 3; i++) {
            CCModel button = ButtonModelLibrary.button.copy();
            button.apply(BlockEnderChest.buttonT[i]);
            button.apply(new Translation(0.5, 0, 0.5));
            button.apply(new Rotation(lidAngle, 1, 0, 0).at(new Vector3(0, 9D / 16D, 1 / 16D)));
            button.apply(new Rotation((-90 * (rotation)) * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0, 0.5)));
            button.render(ccrs, mat, new UVTranslation(0.25 * (colours[i].getWoolMeta() % 4), 0.25 * (colours[i].getWoolMeta() / 4)));
        }
        mat.translate(0.5, 0, 0.5);

        //Pearl
        if (lidAngle != 0) {//Micro optimization, lid closed, dont render pearl.
            double time = ClientUtils.getRenderTime() + pearlOffset;
            Matrix4 pearlMat = RenderUtils.getMatrix(mat.copy(), new Vector3(0, 0.2 + lidAngle * -0.5 + RenderUtils.getPearlBob(time), 0), new Rotation(time / 3, new Vector3(0, 1, 0)), 0.04);
            ccrs.brightness = 15728880;
            ccrs.bind(pearlType, getter);
            CCModelLibrary.icosahedron4.render(ccrs, pearlMat);
        }
        ccrs.reset();
    }
}
