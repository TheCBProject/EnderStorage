package codechicken.enderstorage.client.gui;

import codechicken.enderstorage.container.ContainerEnderItemStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiEnderItemStorage extends AbstractContainerScreen<ContainerEnderItemStorage> {

    public GuiEnderItemStorage(ContainerEnderItemStorage container, Inventory playerInv, Component title) {
        super(container, playerInv, title);

        if (container.chestInv.getSize() == 2) {
            imageHeight = 222;
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        renderBackground(graphics, mouseX, mouseY, partialTicks);
        super.render(graphics, mouseX, mouseY, partialTicks);
        renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY) {
        graphics.drawString(font, title.getVisualOrderText(), 8, 6, 0x404040, false);
        graphics.drawString(font, playerInventoryTitle.getVisualOrderText(), 8, imageHeight - 94, 0x404040, false);
        if (menu.chestInv.freq.hasOwner()) {
            Component name = menu.chestInv.freq.getOwnerName();
            assert name != null;
            graphics.drawString(font, name.getVisualOrderText(), 170 - font.width(name), 6, 0x404040, false);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        ResourceLocation texture = new ResourceLocation(menu.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - imageWidth) / 2;
        int y = (height - imageHeight) / 2;

        switch (menu.chestInv.getSize()) {
            case 0:
            case 2:
                graphics.blit(texture, x, y, 0, 0, imageWidth, imageHeight);
                break;
            case 1:
                graphics.blit(texture, x, y, 0, 0, imageWidth, 71);
                graphics.blit(texture, x, y + 71, 0, 126, imageWidth, 96);
                break;

        }
    }
}
