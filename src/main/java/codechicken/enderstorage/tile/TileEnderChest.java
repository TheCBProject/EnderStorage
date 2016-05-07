package codechicken.enderstorage.tile;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.misc.EnderDyeButton;
import codechicken.enderstorage.misc.EnderKnobSlot;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.vec.Cuboid6;
import codechicken.lib.vec.Vector3;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.List;

public class TileEnderChest extends TileFrequencyOwner implements IInventory, ITickable {
    public double a_lidAngle;
    public double b_lidAngle;
    public int c_numOpen;
    public int rotation;

    private EnderItemStorage storage;
    public static EnderDyeButton[] buttons;

    static {
        buttons = new EnderDyeButton[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = new EnderDyeButton(i);
        }
    }

    public TileEnderChest(World world) {
        worldObj = world;
        c_numOpen = -1;
    }

    public TileEnderChest() {
    }

    @Override
    public void update() {
        super.update();

        //update compatiblity
        //I assume this is a converter between different versions of EnderStorage??
        //if (worldObj.getBlockMetadata(xCoord, yCoord, zCoord) != 0) {
        //    rotation = worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
        //    worldObj.setBlockMetadataWithNotify(xCoord, yCoord, zCoord, 0, 3);
        //}
        if (!worldObj.isRemote && (worldObj.getTotalWorldTime() % 20 == 0 || c_numOpen != storage.getNumOpen())) {
            c_numOpen = storage.getNumOpen();
            worldObj.addBlockEvent(getPos(), getBlockType(), 1, c_numOpen);
            worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        }

        b_lidAngle = a_lidAngle;
        a_lidAngle = MathHelper.approachLinear(a_lidAngle, c_numOpen > 0 ? 1 : 0, 0.1);

        if (b_lidAngle >= 0.5 && a_lidAngle < 0.5) {
            worldObj.playSound(null, getPos(), SoundEvents.block_chest_close, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        } else if (b_lidAngle == 0 && a_lidAngle > 0) {
            worldObj.playSound(null, getPos(), SoundEvents.block_chest_open, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            c_numOpen = type;
            return true;
        }
        return false;
    }

    public double getRadianLidAngle(float frame) {

        double a = MathHelper.interpolate(b_lidAngle, a_lidAngle, frame);
        a = 1.0F - a;
        a = 1.0F - a * a * a;
        return a * 3.141593 * -0.5;
    }

    public void reloadStorage() {
        storage = (EnderItemStorage) EnderStorageManager.instance(worldObj.isRemote).getStorage(frequency, "item");
    }

    @Override
    public EnderItemStorage getStorage() {
        return storage;
    }

    @Override
    public int getSizeInventory() {
        return storage.getSizeInventory();
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
        return storage.getStackInSlot(slot);
    }

    @Override
    public ItemStack decrStackSize(int slot, int count) {
        return storage.decrStackSize(slot, count);
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack itemStack) {
        storage.setInventorySlotContents(slot, itemStack);
    }

    @Override
    public String getName() {
        return "Ender Chest";
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        return true;
    }

    @Override
    public void writeToPacket(PacketCustom packet) {
        packet.writeByte(rotation);
    }

    @Override
    public void handleDescriptionPacket(PacketCustom desc) {
        super.handleDescriptionPacket(desc);
        rotation = desc.readUByte();
    }

    @Override
    public void onPlaced(EntityLivingBase entity) {
        rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
    }

    @Override
    public void writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("rot", (byte) rotation);
    }

    @Override
    public void readFromNBT(NBTTagCompound tag) {
        super.readFromNBT(tag);
        rotation = tag.getByte("rot");
    }

    @Override
    public boolean activate(EntityPlayer player, int subHit) {
        storage.openSMPGui(player, "tile.enderChest.name");
        return true;
    }

    @Override
    public IndexedCuboid6 getBlockBounds() {
        return new IndexedCuboid6(0, new Cuboid6(pos.getX() + 1 / 16D, pos.getY(), pos.getZ() + 1 / 16D, pos.getX() + 15 / 16D, pos.getY() + 14 / 16D, pos.getZ() + 15 / 16D).sub(Vector3.fromTile(this)));
    }

    @Override
    public List<IndexedCuboid6> getIndexedCuboids() {
        List<IndexedCuboid6> cuboids = new ArrayList<IndexedCuboid6>();

        // Remove other boxes if the chest has lid open.
        if (getRadianLidAngle(0) < 0) {
            return cuboids;
        }

        // DyeButtons.
        for (int button = 0; button < 3; button++) {
            EnderDyeButton ebutton = TileEnderChest.buttons[button].copy();
            ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, 0);
            ebutton.rotateMeta(rotation);

            cuboids.add(new IndexedCuboid6(button + 1, new Cuboid6(ebutton.getMin(), ebutton.getMax())));
        }

        //Lock Button.
        cuboids.add(new IndexedCuboid6(4, new Cuboid6(new EnderKnobSlot(rotation).getSelectionBB())));
        return cuboids;
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
    public boolean isItemValidForSlot(int i, ItemStack itemstack) {
        return true;
    }

    @Override
    public int comparatorInput() {
        return Container.calcRedstoneFromInventory(this);
    }

    //region Unused
    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
        return null;
    }

    @Override
    public ItemStack removeStackFromSlot(int index) {
        return null;
    }

    @Override
    public void openInventory(EntityPlayer player) {
    }

    @Override
    public void closeInventory(EntityPlayer player) {
    }

    @Override
    public int getField(int id) {
        return 0;
    }

    @Override
    public void setField(int id, int value) {
    }

    @Override
    public int getFieldCount() {
        return 0;
    }

    @Override
    public void clear() {
    }

    //endregion
}