package codechicken.enderstorage.tile;

import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.misc.EnderDyeButton;
import codechicken.enderstorage.misc.EnderKnobSlot;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
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
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class TileEnderChest extends TileFrequencyOwner implements IInventory {
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

        if (!worldObj.isRemote && (worldObj.getTotalWorldTime() % 20 == 0 || c_numOpen != storage.getNumOpen())) {
            c_numOpen = storage.getNumOpen();
            worldObj.addBlockEvent(getPos(), getBlockType(), 1, c_numOpen);
            worldObj.notifyNeighborsOfStateChange(pos, getBlockType());
        }

        b_lidAngle = a_lidAngle;
        a_lidAngle = MathHelper.approachLinear(a_lidAngle, c_numOpen > 0 ? 1 : 0, 0.1);

        if (b_lidAngle >= 0.5 && a_lidAngle < 0.5) {
            worldObj.playSound(null, getPos(), SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
        } else if (b_lidAngle == 0 && a_lidAngle > 0) {
            worldObj.playSound(null, getPos(), SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, worldObj.rand.nextFloat() * 0.1F + 0.9F);
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
    @Nonnull
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
    public void writeToPacket(MCDataOutput packet) {
        super.writeToPacket(packet);
        packet.writeByte(rotation);
    }

    @Override
    public void readFromPacket(MCDataInput packet) {
        super.readFromPacket(packet);
        rotation = packet.readUByte();
    }

    @Override
    public void onPlaced(EntityLivingBase entity) {
        rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound tag) {
        super.writeToNBT(tag);
        tag.setByte("rot", (byte) rotation);
        return tag;
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

        cuboids.add((IndexedCuboid6)getBlockBounds().copy().add(new Vector3(getPos())));

        // Remove other boxes if the chest has lid open.
        if (getRadianLidAngle(0) < 0) {
            return cuboids;
        }

        // DyeButtons.
        for (int button = 0; button < 3; button++) {
            EnderDyeButton ebutton = TileEnderChest.buttons[button].copy();
            ebutton.rotate(0, 0.5625, 0.0625, 1, 0, 0, 0);
            ebutton.rotateMeta(rotation);

            cuboids.add(new IndexedCuboid6(button + 1, new Cuboid6(ebutton.getMin(), ebutton.getMax()).add(new Vector3(getPos()))));
        }

        //Lock Button.
        cuboids.add(new IndexedCuboid6(4, new Cuboid6(new EnderKnobSlot(rotation).getSelectionBB()).add(new Vector3(getPos()))));
        return cuboids;
    }

    @Override
    public boolean rotate() {
        if (!worldObj.isRemote) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getUpdatePacket(), worldObj, pos.getX() >> 4, pos.getZ() >> 4);
        }

        return true;
    }

    @Override
    public boolean isItemValidForSlot(int i, @Nonnull ItemStack itemstack) {
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
    @Nonnull
    public ITextComponent getDisplayName() {
        return new TextComponentString("");
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

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        return capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY){
            return (T) new InvWrapper(this);
        }
        return super.getCapability(capability, facing);
    }
}
