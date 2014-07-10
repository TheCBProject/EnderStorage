package codechicken.enderstorage.common;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemEnderStorage extends ItemBlock
{
    public ItemEnderStorage(Block block)
    {
        super(block);
        setHasSubtypes(true);
    }
    
    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if(super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
            TileFrequencyOwner tile = (TileFrequencyOwner)world.getTileEntity(x, y, z);
            tile.setFreq(stack.getItemDamage() & 0xFFF);
            if(stack.hasTagCompound())
                tile.setOwner(stack.getTagCompound().getString("owner"));
            
            return true;
        }
        return false;
    }
    
    @Override
    public int getMetadata(int par1)
    {
        return par1 >> 12;
    }
    
    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        return super.getUnlocalizedName()+"|"+getMetadata(stack.getItemDamage());
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended)
    {
        if(stack.hasTagCompound() && !stack.getTagCompound().getString("owner").equals("global"))
            list.add(stack.getTagCompound().getString("owner"));
    }
}
