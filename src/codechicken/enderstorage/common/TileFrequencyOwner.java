package codechicken.enderstorage.common;

import java.util.List;

import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.internal.EnderStorageSPH;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class TileFrequencyOwner extends TileEntity
{
    public static Cuboid6 selection_button = new Cuboid6(-1/16D, 0, -2/16D, 1/16D, 1/16D, 2/16D);
    
    public int freq;
    public String owner = "global";
    private int changeCount;
    
    @Override
    public void validate()
    {
        super.validate();
        if(!(worldObj instanceof WorldServer) == worldObj.isRemote)
            reloadStorage();
    }

    public void setFreq(int i)
    {
        freq = i;
        reloadStorage();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }

    public void setOwner(String username)
    {
        owner = username;
        reloadStorage();
        markDirty();
        worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
    }
    
    @Override
    public void updateEntity()
    {
        if(getStorage().getChangeCount() > changeCount)
        {
            worldObj.func_147453_f(xCoord, yCoord, zCoord, getBlockType());
            changeCount = getStorage().getChangeCount();
        }
    }
    
    public abstract void reloadStorage();
    
    public abstract AbstractEnderStorage getStorage();

    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        freq = tag.getInteger("freq");
        owner = tag.getString("owner");
    }

    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setInteger("freq", freq);
        tag.setString("owner", owner);
    }

    public boolean activate(EntityPlayer player, int subHit)
    {
        return false;
    }

    public void onPlaced(EntityLivingBase entity)
    {
    }

    public boolean invincible()
    {
        return false;
    }

    public MovingObjectPosition rayTrace(World world, Vec3 vec3d, Vec3 vec3d1, MovingObjectPosition fullblock)
    {
        return fullblock;
    }

    public void addTraceableCuboids(List<IndexedCuboid6> cuboids)
    {
        cuboids.add(new IndexedCuboid6(0, new Cuboid6(xCoord, yCoord, zCoord, xCoord+1, yCoord+1, zCoord+1)));
    }

    @Override
    public final Packet getDescriptionPacket()
    {
        PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 1);
        packet.writeCoord(xCoord, yCoord, zCoord);
        packet.writeShort(freq);
        packet.writeString(owner);
        writeToPacket(packet);
        return packet.toPacket();
    }
    
    public void writeToPacket(PacketCustom packet)
    {
    }

    public void handleDescriptionPacket(PacketCustom desc)
    {
        freq = desc.readUShort();
        owner = desc.readString();
    }

    public int getLightValue()
    {
        return 0;
    }

    public boolean redstoneInteraction()
    {
        return false;
    }

    public int comparatorInput()
    {
        return 0;
    }

    public boolean rotate() {
        return false;
    }
}
