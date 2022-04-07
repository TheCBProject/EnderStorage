package codechicken.enderstorage.client.gui;

import codechicken.enderstorage.container.ContainerEnderItemStorage;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

public class GuiEnderItemStorage extends AbstractContainerScreen<ContainerEnderItemStorage> {

    public GuiEnderItemStorage(ContainerEnderItemStorage container, Inventory playerInv, Component title) {
        super(container, playerInv, title);
        passEvents = false;

        if (container.chestInv.getSize() == 2) {
            imageHeight = 222;
        }
    }

    @Override
    public void render(PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        renderBackground(stack);
        super.render(stack, mouseX, mouseY, partialTicks);
        renderTooltip(stack, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(PoseStack mStack, int mouseX, int mouseY) {
        font.draw(mStack, title.getVisualOrderText(), 8, 6, 0x404040);
        font.draw(mStack, playerInventoryTitle.getVisualOrderText(), 8, imageHeight - 94, 0x404040);
        if (menu.chestInv.freq.hasOwner()) {
            Component name = menu.chestInv.freq.getOwnerName();
            font.draw(mStack, name.getVisualOrderText(), 170 - font.width(name), 6, 0x404040);
        }
    }

    @Override
    protected void renderBg(PoseStack mStack, float partialTicks, int mouseX, int mouseY) {
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.setShaderTexture(0, new ResourceLocation(menu.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png"));
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
