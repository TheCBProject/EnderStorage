package codechicken.enderstorage.container;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.data.MCDataInput;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ContainerEnderItemStorage extends AbstractContainerMenu {

    public EnderItemStorage chestInv;

    public ContainerEnderItemStorage(int windowId, Inventory playerInv, MCDataInput packet) {
        this(windowId, playerInv, EnderStorageManager.instance(true).getStorage(Frequency.readFromPacket(packet), EnderItemStorage.TYPE));
        chestInv.handleContainerPacket(packet);
    }

    public ContainerEnderItemStorage(int windowId, Inventory playerInv, EnderItemStorage chestInv) {
        super(EnderStorageModContent.ENDER_ITEM_STORAGE.get(), windowId);
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

    private void addPlayerSlots(Container invplayer, int yOffset) {
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
    public boolean stillValid(Player entityplayer) {
        return chestInv.stillValid(entityplayer);
    }

    @Override
    public ItemStack quickMoveStack(Player par1EntityPlayer, int i) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = slots.get(i);

        if (slot != null && slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();

            int chestSlots = EnderItemStorage.sizes[chestInv.getSize()];
            if (i < chestSlots) {
                if (!moveItemStackTo(itemstack1, chestSlots, slots.size(), true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(itemstack1, 0, chestSlots, false)) {
                return ItemStack.EMPTY;
            }
            if (itemstack1.getCount() == 0) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
        }
        return itemstack;
    }

    @Override
    public void removed(Player entityplayer) {
        super.removed(entityplayer);
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
