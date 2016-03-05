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
import net.minecraft.util.ITickable;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public abstract class TileFrequencyOwner extends TileEntity implements ITickable
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
        worldObj.markBlockForUpdate(pos);
    }

    public void setOwner(String username)
    {
        owner = username;
        reloadStorage();
        markDirty();
        worldObj.markBlockForUpdate(pos);
    }
    
    @Override
    public void update()
    {
        if(getStorage().getChangeCount() > changeCount)
        {
        	//TODO dont know what this is
//            worldObj.func_147453_f(pos, getBlockType());
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
        cuboids.add(new IndexedCuboid6(0, new Cuboid6(pos.getX(), pos.getY(), pos.getZ(), pos.getX()+1, pos.getY()+1, pos.getZ()+1)));
    }

    @Override
    public final Packet getDescriptionPacket()
    {
        PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 1);
        packet.writeCoord(pos.getX(), pos.getY(), pos.getZ());
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
