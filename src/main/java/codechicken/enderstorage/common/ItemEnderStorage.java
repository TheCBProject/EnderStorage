package codechicken.enderstorage.common;

import java.util.List;

import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.storage.liquid.EnderLiquidStorage;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ItemEnderStorage extends ItemBlock implements IFluidContainerItem
{
    public ItemEnderStorage(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int par1) {
        return par1 >> 12;
    }

    public String getOwner(ItemStack stack) {
        return stack.hasTagCompound() ? stack.getTagCompound().getString("owner") : "global";
    }

    public int getFreq(ItemStack stack) {
        return stack.getItemDamage() & 0xFFF;
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side,float hitX, float hitY, float hitZ, IBlockState newState) {
        if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
            tile.setFreq(getFreq(stack));
            tile.setOwner(getOwner(stack));

            return true;
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return super.getUnlocalizedName() + "|" + getMetadata(stack.getItemDamage());
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List list, boolean extended) {
        if (!getOwner(stack).equals("global"))
            list.add(getOwner(stack));
    }

    private EnderLiquidStorage getLiquidStorage(ItemStack stack) {
        return (EnderLiquidStorage) EnderStorageManager.instance(FMLCommonHandler.instance().getEffectiveSide().isClient())
                .getStorage(getOwner(stack), getFreq(stack), "liquid");
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        if(getMetadata(container.getItemDamage()) == 1)
            return getLiquidStorage(container).getFluid();

        return null;
    }

    @Override
    public int getCapacity(ItemStack container) {
        if(getMetadata(container.getItemDamage()) == 1)
            return EnderLiquidStorage.CAPACITY;

        return 0;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if(getMetadata(container.getItemDamage()) == 1)
            return getLiquidStorage(container).fill(null, resource, doFill);

        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if(getMetadata(container.getItemDamage()) == 1)
            return getLiquidStorage(container).drain(null, maxDrain, doDrain);

        return null;
    }
}
