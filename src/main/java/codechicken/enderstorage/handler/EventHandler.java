package codechicken.enderstorage.handler;

import codechicken.enderstorage.init.ModBlocks;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.vec.IIndexedCuboidProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderGlobal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class EventHandler {

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        World world = event.getPlayer().worldObj;
        EntityPlayer player = event.getPlayer();
        BlockPos pos = event.getTarget().getBlockPos();
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(pos) instanceof IIndexedCuboidProvider) {

            IIndexedCuboidProvider provider = (IIndexedCuboidProvider) world.getTileEntity(pos);
            RayTraceResult hit = null;
            List<IndexedCuboid6> cuboids = provider.getIndexedCuboids();
            for (IndexedCuboid6 cuboid6 : cuboids) {

                RayTraceResult result = rayTrace(pos, RayTracer.getStartVec(player), RayTracer.getEndVec(player), cuboid6.aabb());
                if (result != null) {
                    setSubHit(result, cuboid6);
                    LogHelper.info(cuboid6.data);
                    hit = result;
                }
            }
            if (hit != null) {
                event.setCanceled(true);
                renderHitBox(player, hit, event.getPartialTicks());
            }
        }
    }

    private static void renderHitBox(EntityPlayer player, RayTraceResult result, float partialTicks) {
        if (!(result.hitInfo instanceof IndexedCuboid6)) {
            Minecraft.getMinecraft().renderGlobal.drawSelectionBox(player, result, 0, partialTicks);
        }
        LogHelper.info("Rendering Custom Box.");
        IndexedCuboid6 cuboid6 = (IndexedCuboid6) result.hitInfo;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(0.0F, 0.0F, 0.0F, 0.4F);
        GlStateManager.glLineWidth(2.0F);
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(false);

        double xPos = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
        double yPos = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
        double zPos = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
        RenderGlobal.drawSelectionBoundingBox(cuboid6.aabb().expandXyz(0.0020000000949949026D).offset(-xPos, -yPos, -zPos));
        GlStateManager.depthMask(true);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    //TODO Move to RayTracer.
    public static RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        Vec3d vec3d1 = end.subtract((double) pos.getX(), (double) pos.getY(), (double) pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : new RayTraceResult(raytraceresult.hitVec.addVector((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }

    //TODO Move to RayTracer.
    public static RayTraceResult setSubHit(RayTraceResult result, IndexedCuboid6 cuboid6) {
        if (cuboid6.data instanceof Integer) {
            result.subHit = (Integer) cuboid6.data;
        }
        result.hitInfo = cuboid6;
        return result;
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void textureStichEvent(TextureStitchEvent.Pre event) {
        //TODO
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void modelBakeEvent(ModelBakeEvent event) {
        //ModelResourceLocation location = new ModelResourceLocation("enderstorage:enderStorage", "type=enderChest");
        //event.getModelRegistry().putObject(location, new EnderChestItemRender());
    }

}
