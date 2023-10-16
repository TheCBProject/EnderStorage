package codechicken.enderstorage.tile;

import codechicken.enderstorage.init.EnderStorageModContent;
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
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.IFluidTank;
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

    public TileEnderTank(BlockPos pos, BlockState state) {
        super(EnderStorageModContent.ENDER_TANK_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();
        capCache.tick();
        pressure_state.update(level.isClientSide);
        if (pressure_state.a_pressure) {
            ejectLiquid();
        }

        liquid_state.update(level.isClientSide);
    }

    @Override
    public void setLevel(Level p_155231_) {
        super.setLevel(p_155231_);
        capCache.setWorldPos(getLevel(), getBlockPos());
    }

    @Override
    public void onNeighborChange(BlockPos from) {
        capCache.onNeighborChanged(from);
    }

    private void ejectLiquid() {
        IFluidHandler source = getStorage();
        for (Direction side : Direction.BY_3D_DATA) {
            IFluidHandler dest = capCache.getCapabilityOr(ForgeCapabilities.FLUID_HANDLER, side, EmptyFluidHandler.INSTANCE);
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
        if (level == null) {
            return;
        }
        if (!level.isClientSide) {
            liquid_state.setFrequency(frequency);
        }
        fluidHandler.invalidate();
        fluidHandler = LazyOptional.of(this::getStorage);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        fluidHandler.invalidate();
    }

    @Override
    public EnderLiquidStorage getStorage() {
        return EnderStorageManager.instance(level.isClientSide).getStorage(frequency, EnderLiquidStorage.TYPE);
    }

    @Override
    public void onPlaced(LivingEntity entity) {
        rotation = entity != null ? (int) Math.floor(entity.getYRot() * 4 / 360 + 2.5D) & 3 : 0;
        pressure_state.b_rotate = pressure_state.a_rotate = pressure_state.approachRotate();
        if (!level.isClientSide) {
            sendUpdatePacket();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("rot", (byte) rotation);
        tag.putBoolean("ir", pressure_state.invert_redstone);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
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
    public boolean activate(Player player, int subHit, InteractionHand hand) {
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

    @Override
    public boolean rotate() {
        if (!level.isClientSide) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getUpdatePacket(), level, worldPosition.getX() >> 4, worldPosition.getZ() >> 4);
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
        if (!remove && cap == ForgeCapabilities.FLUID_HANDLER) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    public class EnderTankState extends TankSynchroniser.TankState {

        @Override
        public void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_LIQUID_SYNC);
            packet.writePos(getBlockPos());
            packet.writeFluidStack(s_liquid);
            packet.sendToChunk(TileEnderTank.this);
        }

        @Override
        public void onLiquidChanged() {
            level.getChunkSource().getLightEngine().checkBlock(worldPosition);
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
                a_pressure = level.hasNeighborSignal(getBlockPos()) != invert_redstone;
                if (a_pressure != b_pressure) {
                    sendSyncPacket();
                }
            }
        }

        public double approachRotate() {
            return a_pressure ? -90 : 90;
        }

        private void sendSyncPacket() {
            PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_PRESSURE_SYNC);
            packet.writePos(getBlockPos());
            packet.writeBoolean(a_pressure);
            packet.sendToChunk(TileEnderTank.this);
        }

        public void invert() {
            invert_redstone = !invert_redstone;
            level.getChunk(worldPosition).setUnsaved(true);
        }
    }
}
