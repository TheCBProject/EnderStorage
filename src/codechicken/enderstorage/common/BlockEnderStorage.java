package codechicken.enderstorage.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Vector3;
import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.storage.item.TileEnderChest;
import codechicken.enderstorage.storage.liquid.TileEnderTank;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.block.BlockContainer;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.block.material.Material;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.util.ForgeDirection;

public class BlockEnderStorage extends BlockContainer
{
    private RayTracer rayTracer = new RayTracer();
    
    public BlockEnderStorage()
    {
        super(Material.rock);
        setHardness(20F);
        setResistance(100F);
        setStepSound(soundTypeStone);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata)
    {
        switch(metadata)
        {
            case 0:
                return new TileEnderChest();
            case 1:
                return new TileEnderTank();
        }
        return null;
    }
    
    @Override
    public ItemStack getPickBlock(MovingObjectPosition mop, World world, int x, int y, int z)
    {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        return createItem(world.getBlockMetadata(x, y, z), tile.freq, tile.owner);
    }
    
    private ItemStack createItem(int meta, int freq, String owner)
    {
        ItemStack stack = new ItemStack(this, 1, freq | meta << 12);
        if(!owner.equals("global"))
        {
            if(!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("owner", owner);
        }
        return stack;
    }

    public int getRenderType()
    {
        return -1;
    }
    
    public int idDropped(int i, Random random, int j)
    {
        return 0;
    }
    
    public boolean isOpaqueCube()
    {
        return false;
    }

    public boolean renderAsNormalBlock()
    {
        return false;
    }

    @Override
    public boolean removedByPlayer(World world, EntityPlayer player, int x, int y, int z)
    {
        if(!player.capabilities.isCreativeMode && !world.isRemote)
            dropBlockAsItem(world, x, y, z, world.getBlockMetadata(x, y, z), 0);
        
        world.setBlockToAir(x, y, z);
        return true;
    }
    
    @Override
    public int quantityDropped(int meta, int fortune, Random random)
    {
        return 0;
    }
    
    public ArrayList<ItemStack> getBlockDropped(World world, int i, int j, int k, int md, int fortune)
    {
        ArrayList<ItemStack> ai = new ArrayList<ItemStack>();
        if(world.isRemote)
            return ai;
        
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(i, j, k);
        if(tile != null)
            ai.add(createItem(md, tile.freq, tile.owner));
        
        return ai;
    }
    
    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float hitX, float hitY, float hitZ)
    {
        if(world.isRemote)
            return true;
        
        MovingObjectPosition hit = RayTracer.retraceBlock(world, player, x, y, z);
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        
        if(hit == null)
            return false;
        
        if(hit.subHit == 4)
        {
            ItemStack item = player.inventory.getCurrentItem();
            if(player.isSneaking() && !tile.owner.equals("global"))
            {
                if(!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(EnderStorage.getPersonalItem()))
                    return false;
                
                tile.setOwner("global");
                return true;
            }
            else if(item != null && item.getItem() == EnderStorage.getPersonalItem().getItem())
            {
                if(tile.owner.equals("global"))
                {
                    tile.setOwner(player.getCommandSenderName());
                    if(!player.capabilities.isCreativeMode)
                        item.stackSize--;
                    return true;
                }
            }
        }
        else if(hit.subHit >= 1 && hit.subHit <= 3)
        {
            ItemStack item = player.inventory.getCurrentItem();
            int dye = EnderStorageRecipe.getDyeType(item);
            if(dye != -1)
            {
                int currentfreq = tile.freq;
                int[] colours = EnderStorageManager.getColoursFromFreq(currentfreq);
                if(colours[hit.subHit -1] == (~dye & 0xF))return false;
                colours[hit.subHit - 1] = ~dye & 0xF;
                tile.setFreq(EnderStorageManager.getFreqFromColours(colours));
                if(!player.capabilities.isCreativeMode)
                    item.stackSize--;
                return true;
            }
        }        
        
        return tile.activate(player, hit.subHit);
    }

    @Override
    public void onBlockPlacedBy(World world, int i, int j, int k, EntityLivingBase entity, ItemStack item)
    {
        ((TileFrequencyOwner)world.getTileEntity(i, j, k)).onPlaced(entity);
    }
    
    @Override
    public void addCollisionBoxesToList(World world, int i, int j, int k, AxisAlignedBB axisalignedbb, List arraylist, Entity entity)
    {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        super.addCollisionBoxesToList(world, i, j, k, axisalignedbb, arraylist, entity);
    }
    
    @Override
    public float getPlayerRelativeBlockHardness(EntityPlayer player, World world, int x, int y, int z)
    {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        return ForgeHooks.blockStrength(tile.invincible() ? Blocks.bedrock : this, player, world, 0, 0, 0);
    }
    
    @Override
    public MovingObjectPosition collisionRayTrace(World world, int x, int y, int z, Vec3 start, Vec3 end)
    {
        TileFrequencyOwner tile = (TileFrequencyOwner)world.getTileEntity(x, y, z);
        if(tile == null)
            return null;
        
        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
        tile.addTraceableCuboids(cuboids);
        return rayTracer.rayTraceCuboids(new Vector3(start), new Vector3(end), cuboids, new BlockCoord(x, y, z), this);
    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List list)
    {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 0x1000));
    }
    
    @Override
    public int getLightValue(IBlockAccess world, int x, int y, int z)
    {
        TileEntity tile = world.getTileEntity(x, y, z);
        if(tile instanceof TileFrequencyOwner)
            return ((TileFrequencyOwner)tile).getLightValue();
        return 0;
    }
    
    @Override
    public AxisAlignedBB getCollisionBoundingBoxFromPool(World world, int x, int y, int z)
    {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        return super.getCollisionBoundingBoxFromPool(world, x, y, z);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event)
    {
        if(event.target.typeOfHit == MovingObjectType.BLOCK && event.player.worldObj.getBlock(event.target.blockX, event.target.blockY, event.target.blockZ) == this)
            RayTracer.retraceBlock(event.player.worldObj, event.player, event.target.blockX, event.target.blockY, event.target.blockZ);
    }
    
    @Override
    public boolean canConnectRedstone(IBlockAccess world, int x, int y, int z, int side)
    {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        return tile.redstoneInteraction();
    }
    
    @Override
    public IIcon getIcon(int par1, int par2)
    {
        return Blocks.obsidian.getIcon(par1, par2);
    }
    
    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister par1IconRegister)
    {
    }

    @Override
    public boolean hasComparatorInputOverride()
    {
        return true;
    }

    @Override
    public int getComparatorInputOverride(World world, int x, int y, int z, int par5)
    {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        return tile.comparatorInput();
    }
    
    @Override
    public boolean rotateBlock(World world, int x, int y, int z, ForgeDirection axis) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(x, y, z);
        return tile.rotate();
    }
}
