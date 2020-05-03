package codechicken.enderstorage.client.gui;

import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.text.ITextComponent;

public class GuiEnderItemStorage extends ContainerScreen<ContainerEnderItemStorage> {

    public GuiEnderItemStorage(ContainerEnderItemStorage container, PlayerInventory playerInv, ITextComponent title) {
        super(container, playerInv, title);
        passEvents = false;

        if (container.chestInv.getSize() == 2) {
            ySize = 222;
        }
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2) {
        font.drawString(title.getFormattedText(), 8, 6, 0x404040);
        font.drawString(playerInventory.getName().getFormattedText(), 8, ySize - 94, 0x404040);
        if (container.chestInv.freq.hasOwner()) {
            String formatted = container.chestInv.freq.getOwnerName().getFormattedText();
            font.drawString(formatted, 170 - font.getStringWidth(formatted), 6, 0x404040);
        }
    }

    protected void drawGuiContainerBackgroundLayer(float f, int i, int j) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureUtils.changeTexture(container.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        switch (container.chestInv.getSize()) {
            case 0:
            case 2:
                blit(x, y, 0, 0, xSize, ySize);
                break;
            case 1:
                blit(x, y, 0, 0, xSize, 71);
                blit(x, y + 71, 0, 126, xSize, 96);
                break;

        }
    }
}
