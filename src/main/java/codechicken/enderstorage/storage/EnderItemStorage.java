package codechicken.enderstorage.storage;

import codechicken.core.ClientUtils;
import codechicken.core.IGuiPacketSender;
import codechicken.core.ServerUtils;
import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static codechicken.enderstorage.plugin.EnderItemStoragePlugin.configSize;
import static codechicken.enderstorage.plugin.EnderItemStoragePlugin.sizes;

public class EnderItemStorage extends AbstractEnderStorage implements IInventory {
    private ItemStack[] items;
    private int open;
    private int size;

    public EnderItemStorage(EnderStorageManager manager, String owner, Frequency freq) {
        super(manager, owner, freq);
        size = configSize;
        empty();
    }

    public void loadFromTag(NBTTagCompound tag) {
        size = tag.getByte("size");
        empty();
        InventoryUtils.readItemStacksFromTag(items, tag.getTagList("Items", 10));
        if (size != configSize) {
            alignSize();
        }
    }

    private void alignSize() {
        if (configSize > size) {
            ItemStack[] newItems = new ItemStack[sizes[configSize]];
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
            size = configSize;
            markDirty();
        } else {
            int numStacks = 0;
            for (ItemStack item : items) {
                if (item != null) {
                    numStacks++;
                }
            }

            if (numStacks <= sizes[configSize]) {
                ItemStack[] newItems = new ItemStack[sizes[configSize]];
                int copyTo = 0;
                for (ItemStack item : items) {
                    if (item != null) {
                        newItems[copyTo] = item;
                        copyTo++;
                    }
                }
                items = newItems;
                size = configSize;
                markDirty();
            }
        }
    }

    @Override
    public String type() {
        return "item";
    }

    public NBTTagCompound saveToTag() {
        if (size != configSize && open == 0) {
            alignSize();
        }

        NBTTagCompound compound = new NBTTagCompound();
        compound.setTag("Items", InventoryUtils.writeItemStacksToTag(items));
        compound.setByte("size", (byte) size);

        return compound;
    }

    public ItemStack getStackInSlot(int slot) {
        synchronized (this) {
            return items[slot];
        }
    }

    public ItemStack removeStackFromSlot(int slot) {
        synchronized (this) {
            return InventoryUtils.removeStackFromSlot(this, slot);
        }
    }

    public void setInventorySlotContents(int slot, ItemStack stack) {
        synchronized (this) {
            items[slot] = stack;
            markDirty();
        }
    }

    public void openInventory() {
        if (manager.client) {
            return;
        }

        synchronized (this) {
            open++;
            if (open == 1) {
                EnderStorageSPH.sendOpenUpdateTo(null, owner, freq, true);
            }
        }
    }

    public void closeInventory() {
        if (manager.client) {
            return;
        }

        synchronized (this) {
            open--;
            if (open == 0) {
                EnderStorageSPH.sendOpenUpdateTo(null, owner, freq, false);
            }
        }
    }

    public int getNumOpen() {
        return open;
    }

    @Override
    public int getSizeInventory() {
        return sizes[size];
    }

    public ItemStack decrStackSize(int slot, int size) {
        synchronized (this) {
            return InventoryUtils.decrStackSize(this, slot, size);
        }
    }

    @Override
    public int getInventoryStackLimit() {
        return 64;
    }

    @Override
    public void markDirty() {
        setDirty();
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer var1) {
        return true;
    }

    public void empty() {
        items = new ItemStack[getSizeInventory()];
    }

    public void openSMPGui(EntityPlayer player, final String name) {
        ServerUtils.openSMPContainer((EntityPlayerMP) player, new ContainerEnderItemStorage(player.inventory, this, false), new IGuiPacketSender() {
            @Override
            public void sendPacket(EntityPlayerMP player, int windowId) {
                PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 2);
                packet.writeByte(windowId);
                packet.writeString(owner);
                packet.writeNBTTagCompound(freq.toNBT());
                packet.writeString(name);
                packet.writeByte(size);

                packet.sendToPlayer(player);
            }
        });
    }

    public int getSize() {
        return size;
    }

    public int openCount() {
        return open;
    }

    public void setClientOpen(int i) {
        if (manager.client) {
            open = i;
        }
    }

    @SideOnly(Side.CLIENT)
    public void openClientGui(int windowID, InventoryPlayer playerInv, String name, int size) {
        this.size = size;
        empty();
        ClientUtils.openSMPGui(windowID, new GuiEnderItemStorage(playerInv, this, name));
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public ITextComponent getDisplayName() {
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
}