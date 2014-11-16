package codechicken.enderstorage.storage.item;

import java.util.List;

import codechicken.lib.render.SpriteSheetManager;
import codechicken.lib.render.SpriteSheetManager.SpriteSheet;
import codechicken.enderstorage.api.EnderStorageManager;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class ItemEnderPouch extends Item
{
    @SideOnly(Side.CLIENT)
    private SpriteSheet spriteSheet;
    
    public ItemEnderPouch()
    {
        setMaxStackSize(1);
        setHasSubtypes(true);
        setCreativeTab(CreativeTabs.tabDecorations);
    }
    
    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended)
    {
        if(stack.hasTagCompound() && !stack.getTagCompound().getString("owner").equals("global"))
            list.add(stack.getTagCompound().getString("owner"));
    }
    
    @Override
    public boolean onItemUseFirst(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote)
            return false;
        
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileEnderChest && player.isSneaking())
        {
            TileEnderChest chest = (TileEnderChest)tile;
            stack.setItemDamage(chest.freq);
            if(!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("owner", chest.owner);
            return true;
        }
        return false;
    }
    
    @Override
    public ItemStack onItemRightClick(ItemStack item, World world, EntityPlayer player)
    {
        if(world.isRemote || player.isSneaking())
            return item;
        
        ((EnderItemStorage) EnderStorageManager.instance(world.isRemote)
                .getStorage(getOwner(item), item.getItemDamage() & 0xFFF, "item"))
                .openSMPGui(player, item.getUnlocalizedName()+".name");
        return item;
    }

    public String getOwner(ItemStack stack)
    {
        return stack.hasTagCompound() ? stack.getTagCompound().getString("owner") : "global";
    }
    
    @Override
    public int getRenderPasses(int metadata)
    {
        return 4;
    }
    
    @Override
    public IIcon getIcon(ItemStack stack, int renderPass)
    {
        return spriteSheet.getSprite(getIconIndex(stack, renderPass));
    }
    
    public int getIconIndex(ItemStack stack, int renderPass)
    {
        if(renderPass == 0)
        {
            int i = 0;
            if(((EnderItemStorage) EnderStorageManager.instance(true)
                .getStorage(getOwner(stack), stack.getItemDamage() & 0xFFF, "item"))
                .openCount() > 0)
                i|=1;
            if(!getOwner(stack).equals("global"))
                i|=2;
            return i;
        }
        
        return renderPass*16+EnderStorageManager.getColourFromFreq(stack.getItemDamage() & 0xFFF, renderPass-1);
    }
    
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
    
    @Override
    public void registerIcons(IIconRegister register)
    {
        spriteSheet = SpriteSheetManager.getSheet(new ResourceLocation("enderstorage", "textures/enderpouch.png"));
        spriteSheet.requestIndicies(0, 1, 2, 3);
        for(int i = 16; i < 64; i++)
            spriteSheet.requestIndicies(i);
        spriteSheet.registerIcons(register);
    }
}
