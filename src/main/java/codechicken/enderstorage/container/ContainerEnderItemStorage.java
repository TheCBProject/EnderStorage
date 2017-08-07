package codechicken.enderstorage.container;

import codechicken.enderstorage.plugin.EnderItemStoragePlugin;
import codechicken.enderstorage.storage.EnderItemStorage;
import invtweaks.api.container.ChestContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

@ChestContainer
public class ContainerEnderItemStorage extends Container {

    public EnderItemStorage chestInv;

    public ContainerEnderItemStorage(IInventory invplayer, EnderItemStorage chestInv, boolean client) {
        this.chestInv = chestInv;
        chestInv.openInventory();

        switch (chestInv.getSize()) {
            case 0:
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 3; ++col) {
                        addSlotToContainer(new Slot(chestInv, col + row * 3, 62 + col * 18, 17 + row * 18));
                    }
                }
                addPlayerSlots(invplayer, 84);
                break;
            case 1:
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 9; ++col) {
                        addSlotToContainer(new Slot(chestInv, col + row * 9, 8 + col * 18, 18 + row * 18));
                    }
                }
                addPlayerSlots(invplayer, 85);
                break;
            case 2:
                for (int row = 0; row < 6; ++row) {
                    for (int col = 0; col < 9; ++col) {
                        addSlotToContainer(new Slot(chestInv, col + row * 9, 8 + col * 18, 18 + row * 18));
                    }
                }
                addPlayerSlots(invplayer, 140);
                break;
        }

    }

    private void addPlayerSlots(IInventory invplayer, int yOffset) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlotToContainer(new Slot(invplayer, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlotToContainer(new Slot(invplayer, col, 8 + col * 18, yOffset + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int chestSlots = EnderItemStoragePlugin.sizes[chestInv.getSize()];
            if (i < chestSlots) {
                if (!mergeItemStack(itemstack1, chestSlots, inventorySlots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!mergeItemStack(itemstack1, 0, chestSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void onContainerClosed(EntityPlayer entityplayer) {
        super.onContainerClosed(entityplayer);
        chestInv.closeInventory();
    }

    @Override
    public boolean canInteractWith(EntityPlayer entityplayer) {
        return chestInv.isUsableByPlayer(entityplayer);
    }

    @ChestContainer.RowSizeCallback
    public int getRowSize() {
        switch (chestInv.getSize()) {
            case 0:
                return 3;
            case 1:
                return 9;
            case 2:
                return 9;
            default:
                throw new IllegalStateException("Unsupported chest size! Must be one of {0, 1, 2}");
        }
    }
}
