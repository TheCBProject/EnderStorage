package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.enderstorage.util.LogHelper;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidContainerItem;
import net.minecraftforge.fml.common.FMLCommonHandler;

import java.util.List;

import static codechicken.enderstorage.reference.VariantReference.enderBlockNames;

public class ItemEnderStorage extends ItemBlock implements IFluidContainerItem {
    public ItemEnderStorage(Block block) {
        super(block);
        setHasSubtypes(true);
    }

    @Override
    public int getMetadata(int stackMeta) {
        return stackMeta;
    }

    public Frequency getFreq(ItemStack stack) {
        return Frequency.fromItemStack(stack);
    }

    @Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState newState) {
        if (super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, newState)) {
            TileFrequencyOwner tile = (TileFrequencyOwner) world.getTileEntity(pos);
            tile.setFreq(getFreq(stack));
            return true;
        }
        return false;
    }

    @Override
    public String getUnlocalizedName(ItemStack stack) {
        return "tile." + enderBlockNames[getMetadata(stack.getItemDamage())];
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean extended) {
        Frequency frequency = Frequency.fromItemStack(stack);
        if (frequency.owner != null) {
            list.add(frequency.owner);
        }
        list.add(String.format("%s/%s/%s", frequency.getLocalizedLeft(), frequency.getLocalizedMiddle(), frequency.getLocalizedRight()));
    }

    private EnderLiquidStorage getLiquidStorage(ItemStack stack) {
        return (EnderLiquidStorage) EnderStorageManager.instance(FMLCommonHandler.instance().getSide().isClient()).getStorage(getFreq(stack), "liquid");
    }

    @Override
    public FluidStack getFluid(ItemStack container) {
        if (getMetadata(container.getItemDamage()) == 1) {
            FluidStack fluid = getLiquidStorage(container).getFluid();
            LogHelper.info("Liquid{ Name: %s, Amount: %s }", fluid.getFluid().getName(), fluid.amount);
            return fluid;
        }

        return null;
    }

    @Override
    public int getCapacity(ItemStack container) {
        if (getMetadata(container.getItemDamage()) == 1) {
            return EnderLiquidStorage.CAPACITY;
        }

        return 0;
    }

    @Override
    public int fill(ItemStack container, FluidStack resource, boolean doFill) {
        if (getMetadata(container.getItemDamage()) == 1) {
            return getLiquidStorage(container).fill(null, resource, doFill);
        }

        return 0;
    }

    @Override
    public FluidStack drain(ItemStack container, int maxDrain, boolean doDrain) {
        if (getMetadata(container.getItemDamage()) == 1) {
            return getLiquidStorage(container).drain(null, maxDrain, doDrain);
        }

        return null;
    }
}
