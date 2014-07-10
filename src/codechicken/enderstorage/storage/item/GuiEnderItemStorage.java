package codechicken.enderstorage.storage.item;

import net.minecraft.util.StatCollector;
import org.lwjgl.opengl.GL11;

import codechicken.lib.render.CCRenderState;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.inventory.IInventory;
import net.minecraft.entity.player.InventoryPlayer;

public class GuiEnderItemStorage extends GuiContainer
{
    private String name;
    private IInventory playerInv;
    private EnderItemStorage chestInv;

    public GuiEnderItemStorage(InventoryPlayer invplayer, EnderItemStorage chestInv, String name)
    {
        super(new ContainerEnderItemStorage(invplayer, chestInv, true));
        playerInv = invplayer;
        this.chestInv = chestInv;
        allowUserInput = false;
        this.name = StatCollector.translateToLocal(name);
        
        if(chestInv.getSize() == 2)
            ySize = 222;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int par1, int par2)
    {
        fontRendererObj.drawString(name, 8, 6, 0x404040);
        fontRendererObj.drawString(StatCollector.translateToLocal(playerInv.getInventoryName()), 8, ySize - 94, 0x404040);
        ContainerEnderItemStorage ces = (ContainerEnderItemStorage)inventorySlots;
        if(!ces.chestInv.owner.equals("global"))
            fontRendererObj.drawString(ces.chestInv.owner, 170-fontRendererObj.getStringWidth(ces.chestInv.owner), 6, 0x404040);
    }
    
    protected void drawGuiContainerBackgroundLayer(float f, int i, int j)
    {
        GL11.glColor4f(1, 1, 1, 1);
        CCRenderState.changeTexture(chestInv.getSize() == 0 ? "textures/gui/container/dispenser.png" : "textures/gui/container/generic_54.png");
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        
        switch(chestInv.getSize())
        {
            case 0:
            case 2:
                drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
                break;
            case 1:
                drawTexturedModalRect(x, y, 0, 0, xSize, 71);
                drawTexturedModalRect(x, y+71, 0, 126, xSize, 96);
                break;
                
        }
    }
}
