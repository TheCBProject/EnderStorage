package codechicken.enderstorage.client.render;

import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
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

    FloatBuffer field_40448_a;

    public RenderCustomEndPortal(double y, double x1, double x2, double z1, double z2) {
        surfaceY = y;
        surfaceX1 = x1;
        surfaceX2 = x2;
        surfaceZ1 = z1;
        surfaceZ2 = z2;
        field_40448_a = GLAllocation.createDirectFloatBuffer(16);
    }

    public void render(double posX, double posY, double posZ, float frame, double playerX, double playerY, double playerZ, TextureManager r) {
        if (r == null) {
            return;
        }
        GlStateManager.disableLighting();
        Random random = new Random(31100L);
        for (int i = 0; i < 16; i++) {
            GlStateManager.pushMatrix();
            float f5 = 16 - i;
            float f6 = 0.0625F;
            float f7 = 1.0F / (f5 + 1.0F);
            if (i == 0) {
                r.bindTexture(end_skyTex);
                f7 = 0.1F;
                f5 = 65F;
                f6 = 0.125F;
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            }
            if (i == 1) {
                r.bindTexture(end_portalTex);
                GlStateManager.enableBlend();
                GlStateManager.blendFunc(GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ONE);
                f6 = 0.5F;
            }
            float f8 = (float) (-(posY + surfaceY));
            float f9 = (float) (f8 + ActiveRenderInfo.getPosition().yCoord);
            float f10 = (float) (f8 + f5 + ActiveRenderInfo.getPosition().yCoord);
            float f11 = f9 / f10;
            f11 = (float) (posY + surfaceY) + f11;
            GlStateManager.translate(playerX, f11, playerZ);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.T, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.R, 9217);
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9216);
            GlStateManager.texGen(GlStateManager.TexGen.S, 9473, this.func_40447_a(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.T, 9473, this.func_40447_a(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGen(GlStateManager.TexGen.R, 9473, this.func_40447_a(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGen(GlStateManager.TexGen.Q, 9474, this.func_40447_a(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.S);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.T);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.R);
            GlStateManager.enableTexGenCoord(GlStateManager.TexGen.Q);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.loadIdentity();
            GlStateManager.translate(0.0F, System.currentTimeMillis() % 0xaae60L / 700000F, 0.0F);
            GlStateManager.scale(f6, f6, f6);
            GlStateManager.translate(0.5F, 0.5F, 0.0F);
            GlStateManager.rotate((i * i * 4321 + i * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            GlStateManager.translate(-0.5F, -0.5F, 0.0F);
            GlStateManager.translate(-playerX, -playerZ, -playerY);
            f9 = f8 + (float) ActiveRenderInfo.getPosition().yCoord;
            GlStateManager.translate(((float) ActiveRenderInfo.getPosition().xCoord * f5) / f9, ((float) ActiveRenderInfo.getPosition().zCoord * f5) / f9, -playerY + 20);
            Tessellator tessellator = Tessellator.getInstance();
            VertexBuffer buffer = tessellator.getBuffer();
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
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.S);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.T);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.R);
        GlStateManager.disableTexGenCoord(GlStateManager.TexGen.Q);
        GlStateManager.enableLighting();
    }

    private FloatBuffer func_40447_a(float f, float f1, float f2, float f3) {
        field_40448_a.clear();
        field_40448_a.put(f).put(f1).put(f2).put(f3);
        field_40448_a.flip();
        return field_40448_a;
    }
}
