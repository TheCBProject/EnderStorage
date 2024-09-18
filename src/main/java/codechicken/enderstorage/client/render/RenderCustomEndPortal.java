package codechicken.enderstorage.client.render;

import codechicken.enderstorage.client.Shaders;
import codechicken.lib.math.MathHelper;
import codechicken.lib.render.buffer.TransformingVertexConsumer;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.vec.Matrix4;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.TheEndPortalRenderer;

public class RenderCustomEndPortal {

    private static final RenderType STARFIELD_TYPE = RenderType.create("starfield", DefaultVertexFormat.POSITION, VertexFormat.Mode.QUADS, 256,
            RenderType.CompositeState.builder()
                    .setShaderState(new RenderStateShard.ShaderStateShard(Shaders::starfieldShader))
                    .setTextureState(new RenderStateShard.TextureStateShard(TheEndPortalRenderer.END_PORTAL_LOCATION, false, false))
                    .createCompositeState(false)
    );

    private final double surfaceY;
    private final double surfaceX1;
    private final double surfaceX2;
    private final double surfaceZ1;
    private final double surfaceZ2;

    public RenderCustomEndPortal(double y, double x1, double x2, double z1, double z2) {
        surfaceY = y;
        surfaceX1 = x1;
        surfaceX2 = x2;
        surfaceZ1 = z1;
        surfaceZ2 = z2;
    }

    public void render(Matrix4 mat, MultiBufferSource source) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        assert localPlayer != null;

        Shaders.starfieldTime().glUniform1f((float) ClientUtils.getRenderTime());
        Shaders.starfieldYaw().glUniform1f((float) (localPlayer.getYRot() * MathHelper.torad));
        Shaders.starfieldPitch().glUniform1f((float) -(localPlayer.getXRot() * MathHelper.torad));

        VertexConsumer cons = new TransformingVertexConsumer(source.getBuffer(STARFIELD_TYPE), mat);
        cons.vertex(surfaceX1, surfaceY, surfaceZ1).endVertex();
        cons.vertex(surfaceX1, surfaceY, surfaceZ2).endVertex();
        cons.vertex(surfaceX2, surfaceY, surfaceZ2).endVertex();
        cons.vertex(surfaceX2, surfaceY, surfaceZ1).endVertex();
    }

    public void render(PoseStack pStack, MultiBufferSource source) {
        LocalPlayer localPlayer = Minecraft.getInstance().player;
        assert localPlayer != null;

        Shaders.starfieldTime().glUniform1f((float) ClientUtils.getRenderTime());
        Shaders.starfieldYaw().glUniform1f((float) (localPlayer.getYRot() * MathHelper.torad));
        Shaders.starfieldPitch().glUniform1f((float) -(localPlayer.getXRot() * MathHelper.torad));

        VertexConsumer cons = source.getBuffer(STARFIELD_TYPE);
        cons.vertex(pStack.last().pose(), (float) surfaceX1, (float) surfaceY, (float) surfaceZ1).endVertex();
        cons.vertex(pStack.last().pose(), (float) surfaceX1, (float) surfaceY, (float) surfaceZ2).endVertex();
        cons.vertex(pStack.last().pose(), (float) surfaceX2, (float) surfaceY, (float) surfaceZ2).endVertex();
        cons.vertex(pStack.last().pose(), (float) surfaceX2, (float) surfaceY, (float) surfaceZ1).endVertex();
    }
}
