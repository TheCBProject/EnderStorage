//package codechicken.enderstorage.storage.item;
//
//import org.lwjgl.opengl.GL11;
//
//import codechicken.lib.render.IItemRenderer;
//import net.minecraft.client.renderer.Tessellator;
//import net.minecraft.item.ItemStack;
//
//public class ItemEnderPouchRenderer implements IItemRenderer
//{
//    @Override
//    public boolean handleRenderType(ItemStack item, ItemRenderType type)
//    {
//        return true;
//    }
//
//    @Override
//    public boolean shouldUseRenderHelper(ItemRenderType type, ItemStack item, ItemRendererHelper helper)
//    {
//        return helper == ItemRendererHelper.ENTITY_BOBBING ;
//    }
//
//    @SuppressWarnings("incomplete-switch")
//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
//    {
//        GL11.glPushMatrix();
//        switch(type)
//        {
//            case INVENTORY:
//                GL11.glScalef(16, 16, 0);
//                break;
//            case EQUIPPED:
//            case ENTITY:
//                GL11.glTranslatef(1, 1, 0);
//                GL11.glScalef(-1, -1, 1);
//        }
//        Tessellator.instance.startDrawingQuads();
//        Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 0);
//        Tessellator.instance.addVertexWithUV(0, 1, 0, 0, 0.0625);
//        Tessellator.instance.addVertexWithUV(1, 1, 0, 0.0625, 0.0625);
//        Tessellator.instance.addVertexWithUV(1, 0, 0, 0.0625, 0);
//        Tessellator.instance.addVertexWithUV(0, 0, 0, 0, 0);
//        Tessellator.instance.addVertexWithUV(1, 0, 0, 0.0625, 0);
//        Tessellator.instance.addVertexWithUV(1, 1, 0, 0.0625, 0.0625);
//        Tessellator.instance.addVertexWithUV(0, 1, 0, 0, 0.0625);
//        Tessellator.instance.draw();
//        GL11.glPopMatrix();
//    }
//}
