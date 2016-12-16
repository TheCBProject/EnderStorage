package codechicken.enderstorage.storage;

import codechicken.core.fluid.ExtendedFluidTank;
import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.FluidTankProperties;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.IFluidTankProperties;

public class EnderLiquidStorage extends AbstractEnderStorage implements IFluidHandler {
    public static final int CAPACITY = 16 * FluidUtils.B;

    private class Tank extends ExtendedFluidTank {
        public Tank(int capacity) {
            super(capacity);
        }

        @Override
        public void onLiquidChanged() {
            setDirty();
        }
    }

    private Tank tank;

    public EnderLiquidStorage(EnderStorageManager manager, Frequency freq) {
        super(manager, freq);
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

    public FluidStack getFluid() {
        return tank.getFluid();
    }

    @Override
    public int fill(FluidStack resource, boolean doFill) {
        return tank.fill(resource, doFill);
    }

    @Override
    public FluidStack drain(FluidStack resource, boolean doDrain) {
        return tank.drain(resource, doDrain);
    }

    @Override
    public FluidStack drain(int maxDrain, boolean doDrain) {
        return tank.drain(maxDrain, doDrain);
    }

    @Override
    public IFluidTankProperties[] getTankProperties() {
        return new IFluidTankProperties[] {new FluidTankProperties(tank.getInfo().fluid, tank.getInfo().capacity)};
    }
}
