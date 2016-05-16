package codechicken.enderstorage.handler;

/**
 * Created by covers1624 on 4/11/2016.
 */
public class EventHandler {

    /*@SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        World world = event.getPlayer().worldObj;
        EntityPlayer player = event.getPlayer();
        BlockPos pos = event.getTarget().getBlockPos();
        if (event.getTarget().typeOfHit == RayTraceResult.Type.BLOCK && world.getTileEntity(pos) instanceof IIndexedCuboidProvider) {
            RayTracer rayTracer = new RayTracer();
            IIndexedCuboidProvider provider = (IIndexedCuboidProvider) world.getTileEntity(pos);
            RayTraceResult hit = rayTracer.rayTraceCuboids(new Vector3(RayTracer.getStartVec(player)), new Vector3(RayTracer.getEndVec(player)), provider.getIndexedCuboids(), new BlockCoord(event.getTarget().getBlockPos()));
            if (hit != null) {
                event.setCanceled(true);
                renderHitBox(player, provider, hit, event.getPartialTicks());
            }
        }
    }


    private static void renderHitBox(EntityPlayer player, IIndexedCuboidProvider provider, RayTraceResult result, float partialTicks) {
        IndexedCuboid6 cuboid6 = provider.getIndexedCuboids().get(result.subHit - 1);
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

    }*/
}
