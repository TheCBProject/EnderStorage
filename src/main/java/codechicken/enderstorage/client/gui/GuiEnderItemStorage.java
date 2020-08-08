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
        field_230711_n_ = false;

        if (container.chestInv.getSize() == 2) {
            ySize = 222;
        }
    }

    @Override
    public void func_230430_a_(MatrixStack mStack, int mouseX, int mouseY, float partialTicks) {
        func_230446_a_(mStack);
        super.func_230430_a_(mStack, mouseX, mouseY, partialTicks);
        func_230459_a_(mStack, mouseX, mouseY);
    }

    @Override
    protected void func_230451_b_(MatrixStack mStack, int mouseX, int mouseY) {
        field_230712_o_.func_238422_b_(mStack, field_230704_d_, 8, 6, 0x404040);
        field_230712_o_.func_238422_b_(mStack, playerInventory.getName(), 8, ySize - 94, 0x404040);
        if (container.chestInv.freq.hasOwner()) {
            ITextComponent name = container.chestInv.freq.getOwnerName();
            field_230712_o_.func_238422_b_(mStack, name, 170 - field_230712_o_.func_238414_a_(name), 6, 0x404040);
        }
    }

    @Override
    protected void func_230450_a_(MatrixStack mStack, float partialTicks, int mouseX, int mouseY) {
        GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
        TextureUtils.changeTexture(container.chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (field_230708_k_ - xSize) / 2;
        int y = (field_230709_l_ - ySize) / 2;

        switch (container.chestInv.getSize()) {
            case 0:
            case 2:
                func_238474_b_(mStack, x, y, 0, 0, xSize, ySize);
                break;
            case 1:
                func_238474_b_(mStack, x, y, 0, 0, xSize, 71);
                func_238474_b_(mStack, x, y + 71, 0, 126, xSize, 96);
                break;

        }
    }
}
