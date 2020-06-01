package codechicken.enderstorage.client.model;

import net.minecraft.client.renderer.entity.model.RendererModel;
import net.minecraft.client.renderer.model.Model;

public class ModelEnderChest extends Model {

    public RendererModel chestLid;
    public RendererModel chestBelow;
    public RendererModel chestKnob;
    public RendererModel diamondKnob;

    public ModelEnderChest() {
        chestLid = new RendererModel(this, 0, 0).setTextureSize(64, 64);
        chestLid.addBox(0.0F, -5F, -14F, 14, 5, 14, 0.0F);
        chestLid.rotationPointX = 1.0F;
        chestLid.rotationPointY = 7F;
        chestLid.rotationPointZ = 15F;
        chestKnob = new RendererModel(this, 0, 0).setTextureSize(64, 64);
        chestKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        chestKnob.rotationPointX = 8F;
        chestKnob.rotationPointY = 7F;
        chestKnob.rotationPointZ = 15F;
        diamondKnob = new RendererModel(this, 0, 5).setTextureSize(64, 64);
        diamondKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        diamondKnob.rotationPointX = 8F;
        diamondKnob.rotationPointY = 7F;
        diamondKnob.rotationPointZ = 15F;
        chestBelow = new RendererModel(this, 0, 19).setTextureSize(64, 64);
        chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        chestBelow.rotationPointX = 1.0F;
        chestBelow.rotationPointY = 6F;
        chestBelow.rotationPointZ = 1.0F;
    }

    public void render(boolean personal) {
        chestKnob.rotateAngleX = chestLid.rotateAngleX;
        diamondKnob.rotateAngleX = chestLid.rotateAngleX;
        chestLid.render(0.0625F);
        chestBelow.render(0.0625F);
        if (personal) {
            diamondKnob.render(0.0625F);
        } else {
            chestKnob.render(0.0625F);
        }
    }

}
