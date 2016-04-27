package codechicken.enderstorage.tile;

import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.*;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

import java.util.ArrayList;
import java.util.List;

import static codechicken.lib.vec.Vector3.center;

public class TileEnderTank extends TileFrequencyOwner implements IFluidHandler {

    public class EnderTankState extends TankSynchroniser.TankState {
        @Override
        public void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 5);
            packet.writeCoord(pos.getX(), pos.getY(), pos.getZ());
            packet.writeFluidStack(s_liquid);
            packet.sendToChunk(worldObj, pos.getX() >> 4, pos.getZ() >> 4);
        }

        @Override
        public void onLiquidChanged() {
            worldObj.checkLight(pos);
        }
    }

    public class PressureState {
        public boolean invert_redstone;
        public boolean a_pressure;
        public boolean b_pressure;

        public double a_rotate;
        public double b_rotate;

        public void update(boolean client) {
            if (client) {
                b_rotate = a_rotate;
                a_rotate = MathHelper.approachExp(a_rotate, approachRotate(), 0.5, 20);
            } else {
                b_pressure = a_pressure;
                a_pressure = worldObj.isBlockPowered(getPos()) != invert_redstone;
                if (a_pressure != b_pressure) {
                    sendSyncPacket();
                }
            }
        }

        public double approachRotate() {
            return a_pressure ? -90 : 90;
        }

        private void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 6);
            packet.writeCoord(pos.getX(), pos.getY(), pos.getZ());
            packet.writeBoolean(a_pressure);
            packet.sendToChunk(worldObj, pos.getX() >> 4, pos.getZ() >> 4);
        }

        public void invert() {
            invert_redstone = !invert_redstone;
            worldObj.getChunkFromChunkCoords(pos.getX(), pos.getZ()).setChunkModified();
        }
    }

    private static Cuboid6[] selectionBoxes = new Cuboid6[4];
    public static Transformation[] buttonT = new Transformation[3];

    static {
        for (int i = 0; i < 3; i++) {
            buttonT[i] = new Scale(0.6).with(new Translation(0.35 + i * 0.15, 0.91, 0.5));
            selectionBoxes[i] = selection_button.copy().apply(buttonT[i]);
        }
        selectionBoxes[3] = new Cuboid6(0.358, 0.268, 0.05, 0.662, 0.565, 0.15);
    }

    public int rotation;
    public EnderTankState liquid_state = new EnderTankState();
    public PressureState pressure_state = new PressureState();

    private EnderLiquidStorage storage;
    private boolean described;

    @Override
    public void update() {
        super.update();

        pressure_state.update(worldObj.isRemote);
        if (pressure_state.a_pressure) {
            ejectLiquid();
        }

        liquid_state.update(worldObj.isRemote);
    }

    private void ejectLiquid() {
        for (EnumFacing side : EnumFacing.values()) {
            TileEntity tile = worldObj.getTileEntity(getPos().offset(side));
            if (!(tile instanceof IFluidHandler)) {
                continue;
            }

            IFluidHandler c = (IFluidHandler) tile;
            FluidStack liquid = drain(null, 100, false);
            if (liquid == null) {
                continue;
            }
            int qty = c.fill(side.getOpposite(), liquid, true);
            if (qty > 0) {
                drain(null, qty, true);
            }
        }
    }

    public void reloadStorage() {
        storage = (EnderLiquidStorage) EnderStorageManager.instance(worldObj.isRemote).getStorage(owner, frequency, "liquid");
        if (!worldObj.isRemote) {
            liquid_state.reloadStorage(storage);
        }
    }

    @Override
    public EnderLiquidStorage getStorage() {
        return storage;
    }

    @Override
    public int fill(EnumFacing from, FluidStack resource, boolean doFill) {
        return storage.fill(from, resource, doFill);
    }

    @Override
    public FluidStack drain(EnumFacing from, int maxDrain, boolean doDrain) {
        return storage.drain(from, maxDrain, doDrain);
    }

    @Override
    public FluidStack drain(EnumFacing from, FluidStack resource, boolean doDrain) {
        return storage.drain(from, resource, doDrain);
    }

    @Override
    public boolean canDrain(EnumFacing from, Fluid fluid) {
        return storage.canDrain(from, fluid);
    }

    @Override
    public boolean canFill(EnumFacing from, Fluid fluid) {
        return storage.canFill(from, fluid);
    }

    @Override
    public FluidTankInfo[] getTankInfo(EnumFacing from) {
        if (worldObj.isRemote) {
            return new FluidTankInfo[] { new FluidTankInfo(liquid_state.s_liquid, EnderLiquidStorage.CAPACITY) };
        }

        return storage.getTankInfo(from);
    }

    @Override
    public void onPlaced(EntityLivingBase entity) {
        rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
        pressure_state.b_rotate = pressure_state.a_rotate = pressure_state.approachRotate();
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("rot", (byte) rotation);
        tag.setBoolean("ir", pressure_state.invert_redstone);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        rotation = tag.getByte("rot");
        pressure_state.invert_redstone = tag.getBoolean("ir");
    }

    @Override
    public void writeToPacket(PacketCustom packet) {
        packet.writeByte(rotation);
        packet.writeFluidStack(liquid_state.s_liquid);
        packet.writeBoolean(pressure_state.a_pressure);
    }

    @Override
    public void handleDescriptionPacket(PacketCustom desc) {
        super.handleDescriptionPacket(desc);
        rotation = desc.readUByte();
        liquid_state.s_liquid = desc.readFluidStack();
        pressure_state.a_pressure = desc.readBoolean();
        if (!described) {
            liquid_state.c_liquid = liquid_state.s_liquid;
            pressure_state.b_rotate = pressure_state.a_rotate = pressure_state.approachRotate();
        }
        described = true;
    }

    @Override
    public boolean activate(EntityPlayer player, int subHit) {
        if (subHit == 4) {
            pressure_state.invert();
            return true;
        }
        return FluidUtils.fillTankWithContainer(this, player) || FluidUtils.emptyTankIntoContainer(this, player, storage.getFluid());
    }

    @Override
    public IndexedCuboid6 getBlockBounds() {
        return new IndexedCuboid6(0, new Cuboid6(0.15, 0, 0.15, 0.85, 0.916, 0.85));
    }

    @Override
    public List<IndexedCuboid6> getIndexedCuboids() {
        ArrayList<IndexedCuboid6> cuboids = new ArrayList<IndexedCuboid6>();
        for (int i = 0; i < 4; i++) {
            cuboids.add(new IndexedCuboid6(i + 1, selectionBoxes[i].copy().apply(Rotation.quarterRotations[rotation ^ 2].at(center))));
        }
        return cuboids;
    }

    @Override
    public int getLightValue() {
        if (liquid_state.s_liquid.amount > 0) {
            return FluidUtils.getLuminosity(liquid_state.c_liquid, liquid_state.s_liquid.amount / 16D);
        }

        return 0;
    }

    @Override
    public boolean redstoneInteraction() {
        return true;
    }

    public void sync(PacketCustom packet) {
        if (packet.getType() == 5) {
            liquid_state.sync(packet.readFluidStack());
        } else if (packet.getType() == 6) {
            pressure_state.a_pressure = packet.readBoolean();
        }
    }

    @Override
    public boolean rotate() {
        if (!worldObj.isRemote) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getDescriptionPacket(), worldObj, pos.getX() >> 4, pos.getZ() >> 4);
        }

        return true;
    }

    @Override
    public int comparatorInput() {
        FluidTankInfo tank = storage.getTankInfo(null)[0];
        return tank.fluid.amount * 14 / tank.capacity + (tank.fluid.amount > 0 ? 1 : 0);
    }
}
