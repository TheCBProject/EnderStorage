package codechicken.enderstorage.container;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.ModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.data.MCDataInput;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;

//@ChestContainer
public class ContainerEnderItemStorage extends Container {

    public EnderItemStorage chestInv;

    public ContainerEnderItemStorage(int windowId, PlayerInventory playerInv, MCDataInput packet) {
        this(windowId, playerInv, EnderStorageManager.instance(true).getStorage(Frequency.readFromPacket(packet), EnderItemStorage.TYPE));
        chestInv.handleContainerPacket(packet);
    }

    public ContainerEnderItemStorage(int windowId, PlayerInventory playerInv, EnderItemStorage chestInv) {
        super(ModContent.containerItemStorage, windowId);
        this.chestInv = chestInv;
        chestInv.openInventory();

        switch (chestInv.getSize()) {
            case 0:
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 3; ++col) {
                        addSlot(new Slot(chestInv, col + row * 3, 62 + col * 18, 17 + row * 18));
                    }
                }
                addPlayerSlots(playerInv, 84);
                break;
            case 1:
                for (int row = 0; row < 3; ++row) {
                    for (int col = 0; col < 9; ++col) {
                        addSlot(new Slot(chestInv, col + row * 9, 8 + col * 18, 18 + row * 18));
                    }
                }
                addPlayerSlots(playerInv, 85);
                break;
            case 2:
                for (int row = 0; row < 6; ++row) {
                    for (int col = 0; col < 9; ++col) {
                        addSlot(new Slot(chestInv, col + row * 9, 8 + col * 18, 18 + row * 18));
                    }
                }
                addPlayerSlots(playerInv, 140);
                break;
        }

    }

    private void addPlayerSlots(IInventory invplayer, int yOffset) {
        for (int row = 0; row < 3; row++) {
            for (int col = 0; col < 9; col++) {
                addSlot(new Slot(invplayer, col + row * 9 + 9, 8 + col * 18, yOffset + row * 18));
            }
        }

        for (int col = 0; col < 9; col++) {
            addSlot(new Slot(invplayer, col, 8 + col * 18, yOffset + 58));
        }
    }

    @Override
    public boolean canInteractWith(PlayerEntity entityplayer) {
        return chestInv.isUsableByPlayer(entityplayer);
    }

    @Override
    public ItemStack transferStackInSlot(PlayerEntity par1EntityPlayer, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = inventorySlots.get(i);

        if (slot != null && slot.getHasStack()) {
            ItemStack itemstack1 = slot.getStack();
            itemstack = itemstack1.copy();

            int chestSlots = EnderItemStorage.sizes[chestInv.getSize()];
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
    public void onContainerClosed(PlayerEntity entityplayer) {
        super.onContainerClosed(entityplayer);
        chestInv.closeInventory();
    }

    //    @ChestContainer.RowSizeCallback
    //    public int getRowSize() {
    //        switch(chestInv.getSize()) {
    //            case 0:
    //                return 3;
    //            case 1:
    //            case 2:
    //                return 9;
    //            default:
    //                throw new IllegalArgumentException("Invalid chest size: " + chestInv.getSize());
    //        }
    //    }
}
