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
            imageHeight = 222;
        }
    }

    @Override
    public void render(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(mStack);
        super.render(mStack, mouseX, mouseY, partialTicks);
        renderTooltip(mStack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(MatrixStack mStack, int mouseX, int mouseY) {
        font.draw(mStack, title.getVisualOrderText(), 8, 6, 0x404040);
        font.draw(mStack, inventory.getName().getVisualOrderText(), 8, imageHeight - 94, 0x404040);
        if (menu.chestInv.freq.hasOwner()) {
            ITextComponent name = menu.chestInv.freq.getOwnerName();
            font.draw(mStack, name.getVisualOrderText(), 170 - font.width(name), 6, 0x404040);
        }
    }

    @Override
    protected void renderBg(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager._color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureUtils.changeTexture(menu.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        switch (menu.chestInv.getSize()) {
            case 0:
            case 2:
                blit(mStack, x, y, 0, 0, imageWidth, imageHeight);
                break;
            case 1:
                blit(mStack, x, y, 0, 0, imageWidth, 71);
                blit(mStack, x, y + 71, 0, 126, imageWidth, 96);
                break;

        }
    }
}
