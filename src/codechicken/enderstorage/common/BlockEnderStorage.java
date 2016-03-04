package codechicken.enderstorage.common;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.storage.item.TileEnderChest;
import codechicken.enderstorage.storage.liquid.TileEnderTank;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.render.TextureUtils.IIconRegister;
import codechicken.lib.vec.BlockCoord;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.MovingObjectPosition.MovingObjectType;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.event.DrawBlockHighlightEvent;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockEnderStorage extends BlockContainer
{
    private RayTracer rayTracer = new RayTracer();

    public BlockEnderStorage() {
        super(Material.rock);
        setHardness(20F);
        setResistance(100F);
        setStepSound(soundTypeStone);
        setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        switch (metadata) {
            case 0:
                return new TileEnderChest();
            case 1:
                return new TileEnderTank();
        }
        return null;
    }

    @Override
    public ItemStack getPickBlock(MovingObjectPosition mop, World world, BlockPos pos) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
        //TODO This might not work
        return createItem(world.getBlockState(pos).getBlock().getMetaFromState(getDefaultState()), tile.freq, tile.owner);
    }

    private ItemStack createItem(int meta, int freq, String owner) {
        ItemStack stack = new ItemStack(this, 1, freq | meta << 12);
        if (!owner.equals("global")) {
            if (!stack.hasTagCompound())
                stack.setTagCompound(new NBTTagCompound());
            stack.getTagCompound().setString("owner", owner);
        }
        return stack;
    }

    @Override
    public int getRenderType() {
        return -1;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }
    

    @Override
    public boolean removedByPlayer(World world, BlockPos pos, EntityPlayer player, boolean willHarvest) {
        return willHarvest || super.removedByPlayer(world, pos, player, false);
    }
    

    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, TileEntity te) {
        super.harvestBlock(world, player, pos, state, te);
        world.setBlockToAir(pos);
    }

    @Override
    public ArrayList<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune) {
        ArrayList<ItemStack> ret = new ArrayList<ItemStack>();

        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
        if (tile != null) {
            ret.add(createItem(state.getBlock().getMetaFromState(state), tile.freq, EnderStorage.anarchyMode ? "global" : tile.owner));
            if(EnderStorage.anarchyMode && !tile.owner.equals("global"))
                ret.add(EnderStorage.getPersonalItem());
        }

        return ret;
    }
    
    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player,
    		EnumFacing side, float hitX, float hitY, float hitZ) {
        if (world.isRemote)
            return true;

        MovingObjectPosition hit = RayTracer.retraceBlock(world, player, pos);
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);

        if (hit == null)
            return false;

        if (hit.subHit == 4) {
            ItemStack item = player.inventory.getCurrentItem();
            if (player.isSneaking() && !tile.owner.equals("global")) {
                if (!player.capabilities.isCreativeMode && !player.inventory.addItemStackToInventory(EnderStorage.getPersonalItem()))
                    return false;

                tile.setOwner("global");
                return true;
            } else if (item != null && item.getItem() == EnderStorage.getPersonalItem().getItem()) {
                if (tile.owner.equals("global")) {
                    tile.setOwner(player.getDisplayNameString());
                    if (!player.capabilities.isCreativeMode)
                        item.stackSize--;
                    return true;
                }
            }
        } else if (hit.subHit >= 1 && hit.subHit <= 3) {
            ItemStack item = player.inventory.getCurrentItem();
            int dye = EnderStorageRecipe.getDyeType(item);
            if (dye != -1) {
                int currentfreq = tile.freq;
                int[] colours = EnderStorageManager.getColoursFromFreq(currentfreq);
                if (colours[hit.subHit - 1] == (~dye & 0xF)) return false;
                colours[hit.subHit - 1] = ~dye & 0xF;
                tile.setFreq(EnderStorageManager.getFreqFromColours(colours));
                if (!player.capabilities.isCreativeMode)
                    item.stackSize--;
                return true;
            }
        }

        return tile.activate(player, hit.subHit);
    }
    

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        ((TileFrequencyOwner) world.getTileEntity(pos)).onPlaced(placer);
    }
    

    @Override
    public void addCollisionBoxesToList(World world, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        super.addCollisionBoxesToList(world, pos, state, mask, list, collidingEntity);
    }   
    
    //TODO
//    @Override
//    public MovingObjectPosition collisionRayTrace(World world, BlockPos pos, Vec3 start, Vec3 end) {
//        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
//        if (tile == null)
//            return null;
//
//        List<IndexedCuboid6> cuboids = new LinkedList<IndexedCuboid6>();
//        tile.addTraceableCuboids(cuboids);
//        return rayTracer.rayTraceCuboid(new Vector3(start), new Vector3(end), (Cuboid6) cuboids, new BlockCoord(pos), this);
//    }

    @Override
    public void getSubBlocks(Item par1, CreativeTabs par2CreativeTabs, List list) {
        list.add(new ItemStack(this, 1, 0));
        list.add(new ItemStack(this, 1, 0x1000));
    }

    @Override
    public int getLightValue(IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileFrequencyOwner)
            return ((TileFrequencyOwner) tile).getLightValue();
        return 0;
    }
    @Override
    public AxisAlignedBB getCollisionBoundingBox(World world, BlockPos pos, IBlockState state) {
        setBlockBounds(0, 0, 0, 1, 1, 1);
        return super.getCollisionBoundingBox(world, pos, state);
    }

    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public void onBlockHighlight(DrawBlockHighlightEvent event) {
        if (event.target.typeOfHit == MovingObjectType.BLOCK && event.player.worldObj.getBlockState(event.target.getBlockPos()) == this)
            RayTracer.retraceBlock(event.player.worldObj, event.player, event.target.getBlockPos());
    }

    @Override
    public boolean canConnectRedstone(IBlockAccess world, BlockPos pos, EnumFacing side) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
        return tile.redstoneInteraction();
    }

    @Override
    public boolean hasComparatorInputOverride() {
        return true;
    }
    

    @Override
    public int getComparatorInputOverride(World world, BlockPos pos) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
        return tile.comparatorInput();
    }

    @Override
    public boolean rotateBlock(World world, BlockPos pos, EnumFacing axis) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
        return tile.rotate();
    }
}
