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
        chestLid.x = 1.0F;
        chestLid.y = 7F;
        chestLid.z = 15F;
        chestKnob = new ModelRenderer(64, 64, 0, 0);
        chestKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        chestKnob.x = 8F;
        chestKnob.y = 7F;
        chestKnob.z = 15F;
        diamondKnob = new ModelRenderer(64, 64, 0, 5);
        diamondKnob.addBox(-1F, -2F, -15F, 2, 4, 1, 0.0F);
        diamondKnob.x = 8F;
        diamondKnob.y = 7F;
        diamondKnob.z = 15F;
        chestBelow = new ModelRenderer(64, 64, 0, 19);
        chestBelow.addBox(0.0F, 0.0F, 0.0F, 14, 10, 14, 0.0F);
        chestBelow.x = 1.0F;
        chestBelow.y = 6F;
        chestBelow.z = 1.0F;
    }

    public void render(MatrixStack stack, IVertexBuilder builder, int packedLight, int packedOverlay, boolean personal) {
        chestKnob.xRot = chestLid.xRot;
        diamondKnob.xRot = chestLid.xRot;
        chestLid.render(stack, builder, packedLight, packedOverlay);
        chestBelow.render(stack, builder, packedLight, packedOverlay);
        if (personal) {
            diamondKnob.render(stack, builder, packedLight, packedOverlay);
        } else {
            chestKnob.render(stack, builder, packedLight, packedOverlay);
        }
    }

}
