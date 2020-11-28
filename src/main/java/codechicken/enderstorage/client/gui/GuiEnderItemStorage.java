package codechicken.enderstorage.client.gui;

import codechicken.enderstorage.container.ContainerEnderItemStorage;
import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.matrix.MatrixStack;
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
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        renderHoveredTooltip(mStack, mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(MatrixStack mStack, int mouseX, int mouseY) {
        font.func_238422_b_(mStack, title.func_241878_f(), 8, 6, 0x404040);
        font.func_238422_b_(mStack, playerInventory.getName().func_241878_f(), 8, ySize - 94, 0x404040);
        if (container.chestInv.freq.hasOwner()) {
            ITextComponent name = container.chestInv.freq.getOwnerName();
            font.func_238422_b_(mStack, name.func_241878_f(), 170 - font.getStringPropertyWidth(name), 6, 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureUtils.changeTexture(container.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        switch (container.chestInv.getSize()) {
            case 0:
            case 2:
                blit(mStack, x, y, 0, 0, xSize, ySize);
                break;
            case 1:
                blit(mStack, x, y, 0, 0, xSize, 71);
                blit(mStack, x, y + 71, 0, 126, xSize, 96);
                break;

        }
    }
}
