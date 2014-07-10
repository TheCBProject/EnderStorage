package codechicken.enderstorage.storage.item;

import java.util.List;

import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import codechicken.enderstorage.EnderStorage;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.common.TileFrequencyOwner;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class TileEnderChest extends TileFrequencyOwner implements IInventory
{
    public double a_lidAngle;
    public double b_lidAngle;
    public int c_numOpen;
    public int rotation;
    
    private EnderItemStorage storage;
    public static EnderDyeButton[] buttons;
    
    static
    {
        buttons = new EnderDyeButton[3];
        for(int i = 0; i < 3; i++)
        {
            buttons[i] = new EnderDyeButton(i);
        }
    }
    
    public TileEnderChest(World world, int metaData)
    {
        worldObj = world;
        freq = metaData;
        c_numOpen = -1;
    }

    public TileEnderChest()
    {
    }

    public void updateEntity()
    {
        super.updateEntity();
        
        //update compatiblity
        if(worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 0)
        {
            rotation = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
            worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
        }
        
        if(!worldObj.isRemote && (worldObj.getTotalWorldTime() % 20 == 0 || c_numOpen != storage.getNumOpen()))
        {
            c_numOpen = storage.getNumOpen();
            worldObj.addBlockEvent(xCoord, yCoord, zCoord, EnderStorage.blockEnderChest, 1, c_numOpen);
        }
        
        b_lidAngle = a_lidAngle;
        a_lidAngle = MathHelper.approachLinear(a_lidAngle, c_numOpen > 0 ? 1 : 0, 0.1);
        
        if(b_lidAngle >= 0.5 && a_lidAngle < 0.5)
            worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.chestclosed", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        else if(b_lidAngle == 0 && a_lidAngle > 0)
            worldObj.playSoundEffect(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5, "random.chestopen", 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
    }

    @Override
    public boolean receiveClientEvent(int i, int j)
    {
        if(i == 1)
        {
            c_numOpen = j;
            return true;
        }
        return false;
    }

    public double getRadianLidAngle(float frame)
    {
        double a = MathHelper.interpolate(b_lidAngle, a_lidAngle, frame);
        a = 1.0F - a;
        a = 1.0F - a * a * a;
        return a * 3.141593 * -0.5;
    }

    public void reloadStorage()
    {
        storage = (EnderItemStorage) EnderStorageManager.instance(worldObj.isRemote).getStorage(owner, freq, "item");
    }
    
    @Override
    public EnderItemStorage getStorage()
    {
        return storage;
    }
    
    @Override
    public int getSizeInventory()
    {
        return storage.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int var1)
    {
        return storage.getStackInSlot(var1);
    }

    @Override
    public ItemStack decrStackSize(int var1, int var2)
    {
        return storage.decrStackSize(var1, var2);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int var1)
    {
        return storage.getStackInSlotOnClosing(var1);
    }

    @Override
    public void setInventorySlotContents(int var1, ItemStack var2)
    {
        storage.setInventorySlotContents(var1, var2);
    }
    
    @Override
    public String getInventoryName()
    {
        return "Ender Chest";
    }

    @Override
    public int getInventoryStackLimit()
    {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1)
    {
        return true;
    }

    @Override
    public void openInventory()
    {
    }

    @Override
    public void closeInventory()
    {
    }
    
    @Override
    public void writeToPacket(PacketCustom packet)
    {
        packet.writeByte(rotation);
    }

    @Override
    public void handleDescriptionPacket(PacketCustom desc)
    {
        super.handleDescriptionPacket(desc);
        rotation = desc.readUByte();
    }
    
    @Override
    public void onPlaced(EntityLivingBase entity)
    {
        rotation = (int)Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
    }
    
    @Override
    public void writeToNBT(NBTTagCompound tag)
    {
        super.writeToNBT(tag);
        tag.setByte("rot", (byte) rotation);
    }
    
    @Override
    public void readFromNBT(NBTTagCompound tag)
    {
        super.readFromNBT(tag);
        rotation = tag.getByte("rot");
    }
    
    @Override
    public boolean activate(EntityPlayer player, int subHit)
    {
        storage.openSMPGui(player, "tile.enderchest|0.name");
        return true;
    }
    
    @Override
    public void addTraceableCuboids(List<IndexedCuboid6> cuboids)
    {
        cuboids.add(new IndexedCuboid6(0, new Cuboid6(xCoord+1/16D, yCoord, zCoord+1/16D, xCoord+15/16D, yCoord+14/16D, zCoord+15/16D)));
        if(getRadianLidAngle(0) < 0)
            return;
        
        for(int button = 0; button < 3; button++)
        {
            EnderDyeButton ebutton = TileEnderChest.buttons[button].copy();
            ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, 0);
            ebutton.rotateMeta(rotation);
            
            cuboids.add(new IndexedCuboid6(button+1, new Cuboid6(ebutton.getMin(), ebutton.getMax()).add(Vector3.fromTileEntity(this))));
        }
        
        cuboids.add(new IndexedCuboid6(4, new Cuboid6(new EnderKnobSlot(rotation).getSelectionBB()).add(Vector3.fromTileEntity(this))));
    }
    
    @Override
    public boolean rotate()
    {
    	if(!worldObj.isRemote)
    	{
	        rotation = (rotation+1)%4;
	        PacketCustom.sendToChunk(getDescriptionPacket(), worldObj, xCoord>>4, zCoord>>4);
    	}
    	
    	return true;
    }
    
    @Override
    public boolean isItemValidForSlot(int i, ItemStack itemstack)
    {
        return true;
    }
    
    @Override
    public boolean hasCustomInventoryName()
    {
        return true;
    }
    
    @Override
    public int comparatorInput()
    {
        return Container.calcRedstoneFromInventory(this);
    }
}
