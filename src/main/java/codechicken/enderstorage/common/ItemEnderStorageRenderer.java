//package codechicken.enderstorage.common;
//
//import codechicken.enderstorage.storage.liquid.TankSynchroniser;
//import codechicken.lib.render.CCRenderState;
//import codechicken.lib.render.IItemRenderer;
//import codechicken.lib.vec.Vector3;
//import net.minecraft.item.ItemStack;
//
//public class ItemEnderStorageRenderer implements IItemRenderer
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
//        return true;
//    }
//
//    @Override
//    public void renderItem(ItemRenderType type, ItemStack item, Object... data)
//    {
//        Vector3 d = new Vector3();
//        if(type != ItemRenderType.EQUIPPED_FIRST_PERSON && type != ItemRenderType.EQUIPPED)
//            d.add(-0.5, -0.5, -0.5);
//        
//        int freq = item.getItemDamage()&0xFFF;
//        String owner = item.hasTagCompound() ? item.getTagCompound().getString("owner") : "global";
//        int rotation = 0;
//        if(type == ItemRenderType.ENTITY)
//            rotation = 3;
//        
//        switch(item.getItemDamage()>>12)
//        {
//            case 0:
//                EnderChestRenderer.renderChest(rotation, freq, !owner.equals("global"), d.x, d.y, d.z, 0, 0);
//            break;
//            case 1:
//                CCRenderState.reset();
//                CCRenderState.pullLightmap();
//                CCRenderState.useNormals = true;
//                EnderTankRenderer.renderTank(rotation, 0, freq, !owner.equals("global"), d.x, d.y, d.z, 0);
//                EnderTankRenderer.renderLiquid(TankSynchroniser.getClientLiquid(freq, owner), d.x, d.y, d.z);
//            break;
//        }
//    }
//}
