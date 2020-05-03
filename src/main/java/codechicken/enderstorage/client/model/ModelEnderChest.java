package codechicken.enderstorage.client.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class ModelEnderChest {

    public ModelRenderer chestLid;
    public ModelRenderer chestBelow;
    public ModelRenderer chestKnob;
    public ModelRenderer diamondKnob;

    public ModelEnderChest() {
        chestLid = new ModelRenderer(64, 64, 0, 0);
        chestLid.addBox(0.0F, -5F, -14F, 14, 5, 14, 0.0F);
        chestLid.rotationPointX = 1.0F;
        chestLid.rotationPointY = 7F;
        chestLid.rotationPointZ = 15F;
        chestKnob = new ModelRenderer(64, 64, 0, 0);
        chestKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        chestKnob.rotationPointX = 8F;
        chestKnob.rotationPointY = 7F;
        chestKnob.rotationPointZ = 15F;
        diamondKnob = new ModelRenderer(64, 64, 0, 5);
        diamondKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        diamondKnob.rotationPointX = 8F;
        diamondKnob.rotationPointY = 7F;
        diamondKnob.rotationPointZ = 15F;
        chestBelow = new ModelRenderer(64, 64, 0, 19);
        chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        chestBelow.rotationPointX = 1.0F;
        chestBelow.rotationPointY = 6F;
        chestBelow.rotationPointZ = 1.0F;
    }

    public void render(MatrixStack stack, IVertexBuilder builder, int packedLight, int packedOverlay, boolean personal) {
        chestKnob.rotateAngleX = chestLid.rotateAngleX;
        diamondKnob.rotateAngleX = chestLid.rotateAngleX;
        chestLid.render(stack, builder, packedLight, packedOverlay);
        chestBelow.render(stack, builder, packedLight, packedOverlay);
        if (personal) {
            diamondKnob.render(stack, builder, packedLight, packedOverlay);
        } else {
            chestKnob.render(stack, builder, packedLight, packedOverlay);
        }
    }

}
