package codechicken.enderstorage.tile;

import codechicken.enderstorage.init.ModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.enderstorage.network.TankSynchroniser;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.lib.capability.CapabilityCache;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.fluid.FluidUtils;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.EmptyFluidHandler;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class TileEnderTank extends TileFrequencyOwner {

    public int rotation;
    public final EnderTankState liquid_state = new EnderTankState();
    public final PressureState pressure_state = new PressureState();
    private final CapabilityCache capCache = new CapabilityCache();
    private LazyOptional<IFluidHandler> fluidHandler = LazyOptional.empty();

    private boolean described;

    public TileEnderTank() {
        super(ModContent.tileEnderTankType);
    }

    @Override
    public void tick() {
        super.tick();
        capCache.tick();
        pressure_state.update(world.isRemote);
        if (pressure_state.a_pressure) {
            ejectLiquid();
        }

        liquid_state.update(world.isRemote);
    }

    @Override
    public void setWorldAndPos(World world, BlockPos pos) {
        super.setWorldAndPos(world, pos);
        capCache.setWorldPos(getWorld(), getPos());
    }

    @Override
    public void onNeighborChange(BlockPos from) {
        capCache.onNeighborChanged(from);
    }

    private void ejectLiquid() {
        IFluidHandler source = getStorage();
        for (Direction side : Direction.BY_INDEX) {
            IFluidHandler dest = capCache.getCapabilityOr(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, side, EmptyFluidHandler.INSTANCE);
            FluidStack drain = source.drain(100, IFluidHandler.FluidAction.SIMULATE);
            if (!drain.isEmpty()) {
                int qty = dest.fill(drain, IFluidHandler.FluidAction.EXECUTE);
                if (qty > 0) {
                    source.drain(qty, IFluidHandler.FluidAction.EXECUTE);
                }
            }
        }
    }

    @Override
    public void onFrequencySet() {
        if (world == null) {
            return;
        }
        if (!world.isRemote) {
            liquid_state.setFrequency(frequency);
        }
        fluidHandler.invalidate();
        fluidHandler = LazyOptional.of(this::getStorage);
    }

    @Override
    public void remove() {
        super.remove();
        fluidHandler.invalidate();
    }

    @Override
    public EnderLiquidStorage getStorage() {
        return EnderStorageManager.instance(world.isRemote).getStorage(frequency, EnderLiquidStorage.TYPE);
    }

    @Override
    public void onPlaced(LivingEntity entity) {
        rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
        pressure_state.b_rotate = pressure_state.a_rotate = pressure_state.approachRotate();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.putByte("rot", (byte) rotation);
        tag.putBoolean("ir", pressure_state.invert_redstone);
        return tag;
    }

    @Override
    public void func_230337_a_(BlockState state, CompoundNBT tag) {
        super.func_230337_a_(state, tag);
        liquid_state.setFrequency(frequency);
        rotation = tag.getByte("rot") & 3;
        pressure_state.invert_redstone = tag.getBoolean("ir");
    }

    @Override
    public void writeToPacket(MCDataOutput packet) {
        super.writeToPacket(packet);
        packet.writeByte(rotation);
        packet.writeFluidStack(liquid_state.s_liquid);
        packet.writeBoolean(pressure_state.a_pressure);
    }

    @Override
    public void readFromPacket(MCDataInput packet) {
        super.readFromPacket(packet);
        liquid_state.setFrequency(frequency);
        rotation = packet.readUByte() & 3;
        liquid_state.s_liquid = packet.readFluidStack();
        pressure_state.a_pressure = packet.readBoolean();
        if (!described) {
            liquid_state.c_liquid = liquid_state.s_liquid;
            pressure_state.b_rotate = pressure_state.a_rotate = pressure_state.approachRotate();
        }
        described = true;
    }

    @Override
    public boolean activate(PlayerEntity player, int subHit, Hand hand) {
        if (subHit == 4) {
            pressure_state.invert();
            return true;
        }
        return FluidUtil.interactWithFluidHandler(player, hand, getStorage());
    }

    @Override
    public int getLightValue() {
        if (liquid_state.s_liquid.getAmount() > 0) {
            return FluidUtils.getLuminosity(liquid_state.c_liquid, liquid_state.s_liquid.getAmount() / 16D);
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
        if (!world.isRemote) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getUpdatePacket(), world, pos.getX() >> 4, pos.getZ() >> 4);
        }

        return true;
    }

    @Override
    public int comparatorInput() {
        IFluidTank tank = getStorage();
        FluidStack fluid = tank.getFluid();
        if (fluid == null) {
            fluid = FluidStack.EMPTY;
        }
        return fluid.getAmount() * 14 / tank.getCapacity() + (fluid.getAmount() > 0 ? 1 : 0);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!removed && cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public class EnderTankState extends TankSynchroniser.TankState {

        @Override
        public void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, 5);
            packet.writePos(getPos());
            packet.writeFluidStack(s_liquid);
            packet.sendToChunk(TileEnderTank.this);
        }

        @Override
        public void onLiquidChanged() {
            world.getChunkProvider().getLightManager().checkBlock(pos);
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
                a_pressure = world.isBlockPowered(getPos()) != invert_redstone;
                if (a_pressure != b_pressure) {
                    sendSyncPacket();
                }
            }
        }

        public double approachRotate() {
            return a_pressure ? -90 : 90;
        }

        private void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, 6);
            packet.writePos(getPos());
            packet.writeBoolean(a_pressure);
            packet.sendToChunk(TileEnderTank.this);
        }

        public void invert() {
            invert_redstone = !invert_redstone;
            world.getChunk(pos).setModified(true);
        }
    }
}
