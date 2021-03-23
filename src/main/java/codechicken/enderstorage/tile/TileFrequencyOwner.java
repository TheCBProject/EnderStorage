package codechicken.enderstorage.tile;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.network.EnderStorageNetwork;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.vec.Cuboid6;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public abstract class TileFrequencyOwner extends TileEntity implements ITickableTileEntity {

    public static Cuboid6 selection_button = new Cuboid6(-1 / 16D, 0, -2 / 16D, 1 / 16D, 1 / 16D, 2 / 16D);

    protected Frequency frequency = new Frequency();
    private int changeCount;

    public TileFrequencyOwner(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFreq(Frequency frequency) {
        this.frequency = frequency;
        onFrequencySet();
        setChanged();
        BlockState state = level.getBlockState(worldPosition);
        level.sendBlockUpdated(worldPosition, state, state, 3);
        if (!level.isClientSide) {
            sendUpdatePacket();
        }
    }

    @Override
    public void tick() {
        if (getStorage().getChangeCount() > changeCount) {
            level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
            changeCount = getStorage().getChangeCount();
        }
    }

    public abstract AbstractEnderStorage getStorage();

    public void onFrequencySet() {

    }

    @Override
    public void load(BlockState state, CompoundNBT tag) {
        super.load(state, tag);
        frequency.set(new Frequency(tag.getCompound("Frequency")));
    }

    @Override
    public CompoundNBT save(CompoundNBT tag) {
        super.save(tag);
        tag.put("Frequency", frequency.writeToNBT(new CompoundNBT()));
        return tag;
    }

    @Override
    public void setLevelAndPosition(World p_226984_1_, BlockPos p_226984_2_) {
        super.setLevelAndPosition(p_226984_1_, p_226984_2_);
        onFrequencySet();
    }

    public boolean activate(PlayerEntity player, int subHit, Hand hand) {
        return false;
    }

    public void onNeighborChange(BlockPos from) {
    }

    public void onPlaced(LivingEntity entity) {
    }

    protected void sendUpdatePacket() {
        createPacket().sendToChunk(level, getBlockPos().getX() >> 4, getBlockPos().getZ() >> 4);
    }

    public PacketCustom createPacket() {
        PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, 1);
        writeToPacket(packet);
        return packet;
    }

    @Override
    public final SUpdateTileEntityPacket getUpdatePacket() {
        return createPacket().toTilePacket(getBlockPos());
    }

    @Override
    public CompoundNBT getUpdateTag() {
        return createPacket().writeToNBT(super.getUpdateTag());
    }

    public void writeToPacket(MCDataOutput packet) {
        frequency.writeToPacket(packet);
    }

    public void readFromPacket(MCDataInput packet) {
        frequency.set(Frequency.readFromPacket(packet));
        onFrequencySet();
    }

    @Override
    public void onDataPacket(NetworkManager net, SUpdateTileEntityPacket pkt) {
        readFromPacket(PacketCustom.fromTilePacket(pkt));
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT tag) {
        readFromPacket(PacketCustom.fromNBTTag(tag));
    }

    public int getLightValue() {
        return 0;
    }

    public boolean redstoneInteraction() {
        return false;
    }

    public int comparatorInput() {
        return 0;
    }

    public boolean rotate() {
        return false;
    }
}
