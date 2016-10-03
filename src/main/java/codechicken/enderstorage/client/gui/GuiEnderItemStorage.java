package codechicken.enderstorage.client.gui;

import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.render.TextureUtils;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.translation.I18n;

public class GuiEnderItemStorage extends GuiContainer {
    private String name;
    private IInventory playerInv;
    private EnderItemStorage chestInv;

    public GuiEnderItemStorage(InventoryPlayer invplayer, EnderItemStorage chestInv, String name) {
        super(new ContainerEnderItemStorage(invplayer, chestInv, true));
        playerInv = invplayer;
        this.chestInv = chestInv;
        allowUserInput = false;
        this.name = I18n.translateToLocal(name);

        if (chestInv.getSize() == 2) {
            ySize = 222;
        }
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        fontRendererObj.drawString(name, 8, 6, 0x404040);
        fontRendererObj.drawString(I18n.translateToLocal(playerInv.getName()), 8, ySize - 94, 0x404040);
        ContainerEnderItemStorage ces = (ContainerEnderItemStorage) inventorySlots;
        if (ces.chestInv.freq.hasOwner()) {
            fontRendererObj.drawString(ces.chestInv.freq.owner, 170 - fontRendererObj.getStringWidth(ces.chestInv.freq.owner), 6, 0x404040);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        TextureUtils.changeTexture(chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        switch (chestInv.getSize()) {
            case 0:
            case 2:
                drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
                break;
            case 1:
                drawTexturedModalRect(x, y, 0, 0, xSize, 71);
                drawTexturedModalRect(x, y + 71, 0, 126, xSize, 96);
                break;

        }
    }
}
