package codechicken.enderstorage.tile;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.network.EnderStorageCPH;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.packet.ICustomPacketTile;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.ICuboidProvider;
import codechicken.lib.vec.Cuboid6;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class TileFrequencyOwner extends TileEntity implements ICuboidProvider, ITickable, ICustomPacketTile {
    public static Cuboid6 selection_button = new Cuboid6(-1 / 16D, 0, -2 / 16D, 1 / 16D, 1 / 16D, 2 / 16D);

    public Frequency frequency = new Frequency();
    private int changeCount;

    public void setFreq(Frequency frequency) {
        this.frequency = frequency;
        markDirty();
        IBlockState state = worldObj.getBlockState(pos);
        worldObj.notifyBlockUpdate(pos, state, state, 3);
        if (!worldObj.isRemote) {
            sendUpdatePacket();
        }
    }
    @Override
    public void update() {
        if (getStorage().getChangeCount() > changeCount) {
            worldObj.updateComparatorOutputLevel(pos, getBlockType());
            changeCount = getStorage().getChangeCount();
        }
    }

    public abstract AbstractEnderStorage getStorage();

    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        frequency.readNBT(tag.getCompoundTag("Frequency"));
    }

    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setTag("Frequency", frequency.toNBT());
        return tag;
    }

    public boolean activate(EntityPlayer player, int subHit, ItemStack stack) {
        return false;
    }

    public void onPlaced(EntityLivingBase entity) {
    }

    protected void sendUpdatePacket() {
        writeToCCPacket().sendToChunk(worldObj, getPos().getX() >> 4, getPos().getZ() >> 4);
    }

    public RayTraceResult rayTrace(World world, Vec3d vec3d, Vec3d vec3d1, RayTraceResult fullBlock) {
        return fullBlock;
    }

    public PacketCustom writeToCCPacket() {
        PacketCustom packet = new PacketCustom(EnderStorageCPH.channel, 1);
        writeToPacket(packet);
        return packet;
    }

    @Override
    public final SPacketUpdateTileEntity getUpdatePacket() {
        return writeToCCPacket().toTilePacket(getPos());
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        return writeToCCPacket().toNBTTag(super.getUpdateTag());
    }

    public void writeToPacket(MCDataOutput packet) {
        packet.writeNBTTagCompound(frequency.toNBT());
    }

    public void readFromPacket(MCDataInput packet) {
        frequency.readNBT(packet.readNBTTagCompound());
    }

    @Override
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity pkt) {
        readFromPacket(PacketCustom.fromTilePacket(pkt));
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
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
