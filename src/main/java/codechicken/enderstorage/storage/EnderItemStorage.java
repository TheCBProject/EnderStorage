package codechicken.enderstorage.storage;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.gui.GuiEnderItemStorage;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.network.EnderStorageSPH;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.inventory.InventoryUtils;
import codechicken.lib.util.ArrayUtils;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.util.ServerUtils;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.SimpleNamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;

public class EnderItemStorage extends AbstractEnderStorage implements IInventory {

    public static final EnderStorageManager.StorageType<EnderItemStorage> TYPE = new EnderStorageManager.StorageType<>("item");

    public static final int[] sizes = new int[] { 9, 27, 54 };

    private ItemStack[] items;
    private int open;
    private int size;

    public EnderItemStorage(EnderStorageManager manager, Frequency freq) {
        super(manager, freq);
        size = EnderStorageConfig.storageSize;
        empty();
    }

    @Override
    public void clearStorage() {
        synchronized (this) {
            empty();
            setDirty();
        }
    }

    public void loadFromTag(CompoundNBT tag) {
        size = tag.getByte("size");
        empty();
        InventoryUtils.readItemStacksFromTag(items, tag.getList("Items", 10));
        if (size != EnderStorageConfig.storageSize) {
            alignSize();
        }
    }

    private void alignSize() {
        if (EnderStorageConfig.storageSize > size) {
            ItemStack[] newItems = ArrayUtils.fill(new ItemStack[sizes[EnderStorageConfig.storageSize]], ItemStack.EMPTY);
            System.arraycopy(items, 0, newItems, 0, items.length);
            items = newItems;
            size = EnderStorageConfig.storageSize;
            markDirty();
        } else {
            int numStacks = 0;
            for (ItemStack item : items) {
                if (!item.isEmpty()) {
                    numStacks++;
                }
            }

            if (numStacks <= sizes[EnderStorageConfig.storageSize]) {
                ItemStack[] newItems = ArrayUtils.fill(new ItemStack[sizes[EnderStorageConfig.storageSize]], ItemStack.EMPTY);
                int copyTo = 0;
                for (ItemStack item : items) {
                    if (!item.isEmpty()) {
                        newItems[copyTo] = item;
                        copyTo++;
                    }
                }
                items = newItems;
                size = EnderStorageConfig.storageSize;
                markDirty();
            }
        }
    }

    @Override
    public String type() {
        return "item";
    }

    public CompoundNBT saveToTag() {
        if (size != EnderStorageConfig.storageSize && open == 0) {
            alignSize();
        }

        CompoundNBT compound = new CompoundNBT();
        compound.put("Items", InventoryUtils.writeItemStacksToTag(items));
        compound.putByte("size", (byte) size);

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
                EnderStorageSPH.sendOpenUpdateTo(null, freq, true);
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
                EnderStorageSPH.sendOpenUpdateTo(null, freq, false);
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

    @Override
    public boolean isEmpty() {
        return ArrayUtils.count(items, (stack -> !stack.isEmpty())) <= 0;
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
    public boolean isUsableByPlayer(PlayerEntity var1) {
        return true;
    }

    public void empty() {
        items = new ItemStack[getSizeInventory()];
        ArrayUtils.fill(items, ItemStack.EMPTY);
    }

    public void openContainer(ServerPlayerEntity player, ITextComponent title) {
        ServerUtils.openContainer(player, new SimpleNamedContainerProvider((id, inv, p) -> new ContainerEnderItemStorage(id, inv, EnderItemStorage.this), title),//
                packet -> {
                    freq.writeToPacket(packet);
                    packet.writeByte(size);
                });
    }

    public void handleContainerPacket(MCDataInput packet) {
        size = packet.readByte();
        empty();
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

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        return true;
    }

    @Override
    public void openInventory(PlayerEntity player) {
    }

    @Override
    public void closeInventory(PlayerEntity player) {
    }

    @Override
    public void clear() {
    }
}
