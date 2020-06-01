package codechicken.enderstorage.client.render;

import codechicken.lib.texture.TextureUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;
import org.lwjgl.opengl.ARBVertexBlend;

import java.nio.FloatBuffer;
import java.util.Random;

import static org.lwjgl.opengl.GL11.GL_QUADS;

public class RenderCustomEndPortal {

    private static final ResourceLocation end_skyTex = new ResourceLocation("textures/environment/end_sky.png");
    private static final ResourceLocation end_portalTex = new ResourceLocation("textures/entity/end_portal.png");

    private double surfaceY;
    private double surfaceX1;
    private double surfaceX2;
    private double surfaceZ1;
    private double surfaceZ2;

    FloatBuffer texBuffer;

    public RenderCustomEndPortal(double y, double x1, double x2, double z1, double z2) {
        surfaceY = y;
        surfaceX1 = x1;
        surfaceX2 = x2;
        surfaceZ1 = z1;
        surfaceZ2 = z2;
        texBuffer = GLAllocation.createDirectFloatBuffer(16);
    }

    public void render(double posX, double posY, double posZ, ActiveRenderInfo renderInfo) {
        if (renderInfo == null) {
            return;
        }
        Vec3d projectedView = renderInfo.getProjectedView();
        GlStateManager.disableLighting();
        Random random = new Random(31100L);
        for (int i = 0; i < 16; i++) {
            GlStateManager.pushMatrix();
            float f5 = 16 - i;
            float f6 = 0.0625F;
            float f7 = 1.0F / (f5 + 1.0F);
            if (i == 0) {
                TextureUtils.changeTexture(end_skyTex);
                f7 = 0.1F;
                f5 = 65F;
                f6 = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (i == 1) {
                TextureUtils.changeTexture(end_portalTex);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                f6 = 0.5F;
            }
            float f8 = (float) (-(posY + surfaceY));
            float f9 = (float) (f8 + projectedView.y);
            float f10 = (float) (f8 + f5 + projectedView.y);
            float f11 = f9 / f10;
            f11 = (float) (posY + surfaceY) + f11;
            GlStateManager.translated(projectedView.x, f11, projectedView.z);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, 9217);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, 9217);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, 9217);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, 9473, this.bufferTexData(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, 9473, this.bufferTexData(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, 9473, this.bufferTexData(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, 9474, this.bufferTexData(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.enableTexGen(GlStateManager.TexGen.S);
            GlStateManager.enableTexGen(GlStateManager.TexGen.T);
            GlStateManager.enableTexGen(GlStateManager.TexGen.R);
            GlStateManager.enableTexGen(GlStateManager.TexGen.Q);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translatef(0.0F, System.currentTimeMillis() % 0xaae60L / 700000F, 0.0F);
            GlStateManager.scalef(f6, f6, f6);
            GlStateManager.translatef(0.5F, 0.5F, 0.0F);
            GlStateManager.rotatef((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
            GlStateManager.translated(-projectedView.x, -projectedView.z, -projectedView.y);
            f9 = f8 + (float) projectedView.y;
            GlStateManager.translated((projectedView.x * f5) / f9, (projectedView.z * f5) / f9, -projectedView.y + 20);
            Tessellator tessellator = Tessellator.getInstance();
            BufferBuilder buffer = tessellator.getBuffer();
            buffer.begin(GL_QUADS, DefaultVertexFormats.POSITION_COLOR);
            f11 = (random.nextFloat() * 0.5F + 0.1F) * f7;
            float f12 = (random.nextFloat() * 0.5F + 0.4F) * f7;
            float f13 = (random.nextFloat() * 0.5F + 0.5F) * f7;
            if (i == 0) {
                f11 = f12 = f13 = 1.0F * f7;
            }

            buffer.pos(posX + surfaceX1, posY + surfaceY, posZ + surfaceZ1).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(posX + surfaceX1, posY + surfaceY, posZ + surfaceZ2).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(posX + surfaceX2, posY + surfaceY, posZ + surfaceZ2).color(f11, f12, f13, 1.0F).endVertex();
            buffer.pos(posX + surfaceX2, posY + surfaceY, posZ + surfaceZ1).color(f11, f12, f13, 1.0F).endVertex();

            tessellator.draw();
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(ARBVertexBlend.GL_MODELVIEW0_ARB);
        }

        GlStateManager.disableBlend();
        GlStateManager.disableTexGen(GlStateManager.TexGen.S);
        GlStateManager.disableTexGen(GlStateManager.TexGen.T);
        GlStateManager.disableTexGen(GlStateManager.TexGen.R);
        GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer bufferTexData(float f, float f1, float f2, float f3) {
        texBuffer.clear();
        texBuffer.put(f).put(f1).put(f2).put(f3);
        texBuffer.flip();
        return texBuffer;
    }
}
