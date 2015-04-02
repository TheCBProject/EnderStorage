package codechicken.enderstorage.storage.liquid;

import codechicken.core.fluid.ExtendedFluidTank;
import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;
import net.minecraftforge.fluids.FluidStack;

public class EnderLiquidStorage extends AbstractEnderStorage implements IFluidHandler
{
    public static final int CAPACITY = 16 * FluidUtils.B;

    private class Tank extends ExtendedFluidTank
    {
        public Tank(int capacity) {
            super(capacity);
        }

        @Override
        public void onLiquidChanged() {
            setDirty();
        }
    }

    private Tank tank;

    public EnderLiquidStorage(EnderStorageManager manager, String owner, int freq) {
        super(manager, owner, freq);
        tank = new Tank(CAPACITY);
    }

    public void loadFromTag(NBTTagCompound tag) {
        tank.fromTag(tag.getCompoundTag("tank"));
    }

    @Override
    public String type() {
        return "liquid";
    }

    public NBTTagCompound saveToTag() {
        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("tank", tank.toTag());

        return compound;
    }

    @Override
    public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    @Override
    public boolean canFill(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public boolean canDrain(ForgeDirection from, Fluid fluid) {
        return true;
    }

    @Override
    public FluidTankInfo[] getTankInfo(ForgeDirection from) {
        return new FluidTankInfo[]{tank.getInfo()};
    }

    public FluidStack getFluid() {
        return tank.getFluid();
    }
}