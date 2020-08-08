package codechicken.enderstorage.client.render;

import codechicken.lib.vec.Matrix4;
import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.GLAllocation;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.EndPortalTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.vector.Vector3d;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

public class RenderCustomEndPortal {

    private static final FloatBuffer texBuffer = GLAllocation.createDirectFloatBuffer(16);
    private static final List<RenderType.State> RENDER_STATES = IntStream.range(0, 16)//
            .mapToObj(i -> RenderType.State.getBuilder()//
                    .transparency(i == 0 ? RenderType.TRANSLUCENT_TRANSPARENCY : RenderType.ADDITIVE_TRANSPARENCY)//
                    .texture(new RenderState.TextureState(i == 0 ? EndPortalTileEntityRenderer.END_SKY_TEXTURE : EndPortalTileEntityRenderer.END_PORTAL_TEXTURE, false, false))//
                    .build(false)//
            )//
            .collect(ImmutableList.toImmutableList());

    private final Random randy = new Random();

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

    public void render(Matrix4 mat, IRenderTypeBuffer getter, double yToCamera) {
        Vector3d projectedView = TileEntityRendererDispatcher.instance.renderInfo.getProjectedView();
        mat = mat.copy();//Defensive copy, prevent external modifications.
        randy.setSeed(31100L);
        for (int i = 0; i < 16; i++) {
            RenderType.State state = RENDER_STATES.get(i);
            EndPortalRenderType renderType = new EndPortalRenderType(i, yToCamera, projectedView, mat, state);
            IVertexBuilder builder = getter.getBuffer(renderType);
            float r = (randy.nextFloat() * 0.5F + 0.1F) * renderType.f7;
            float g = (randy.nextFloat() * 0.5F + 0.4F) * renderType.f7;
            float b = (randy.nextFloat() * 0.5F + 0.5F) * renderType.f7;
            if (i == 0) {
                r = g = b = 1.0F * renderType.f7;
            }
            builder.pos(surfaceX1, surfaceY, surfaceZ1).color(r, g, b, 1.0F).endVertex();
            builder.pos(surfaceX1, surfaceY, surfaceZ2).color(r, g, b, 1.0F).endVertex();
            builder.pos(surfaceX2, surfaceY, surfaceZ2).color(r, g, b, 1.0F).endVertex();
            builder.pos(surfaceX2, surfaceY, surfaceZ1).color(r, g, b, 1.0F).endVertex();
        }
    }

    private static FloatBuffer bufferTexData(float f, float f1, float f2, float f3) {
        texBuffer.clear();
        texBuffer.put(f).put(f1).put(f2).put(f3);
        texBuffer.flip();
        return texBuffer;
    }

    public class EndPortalRenderType extends RenderType {

        private final int idx;
        private final Vector3d projectedView;
        private final Matrix4 mat;
        private final State state;

        //I have no idea what these field names could be changed to, blame decompiler. #borrowed form mojang
        public final float f5;
        public final float f6;
        public final float f7;
        public final float f8;
        public final float f9;
        public final float f10;
        public final float f11;

        public EndPortalRenderType(int idx, double posY, Vector3d projectedView, Matrix4 mat, RenderType.State state) {
            super("enderstorage:end_portal", DefaultVertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, null, null);
            this.idx = idx;
            this.projectedView = projectedView;
            this.mat = mat;
            this.state = state;
            f5 = idx == 0 ? 65F : 16 - idx;
            f6 = idx == 0 ? 0.125F : (idx == 1 ? 0.5F : 0.0625F);
            f7 = idx == 0 ? 0.1F : 1.0F / (16 - idx + 1.0F);
            f8 = (float) (-(posY + surfaceY));
            f9 = (float) (f8 + projectedView.y);
            f10 = (float) (f8 + f5 + projectedView.y);
            f11 = (float) (posY + surfaceY) + (f9 / f10);
        }

        @Override
        @SuppressWarnings ("deprecation")
        public void setupRenderState() {
            state.renderStates.forEach(RenderState::setupRenderState);
            RenderSystem.disableLighting();
            RenderSystem.pushMatrix();//Apply stack here.
            mat.glApply();
            RenderSystem.pushMatrix();
            GlStateManager.translated(projectedView.x, f11, projectedView.z);
            GlStateManager.texGenMode(GlStateManager.TexGen.S, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexGen.T, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexGen.R, GL11.GL_OBJECT_LINEAR);
            GlStateManager.texGenMode(GlStateManager.TexGen.Q, GL11.GL_EYE_LINEAR);
            GlStateManager.texGenParam(GlStateManager.TexGen.S, GL11.GL_OBJECT_PLANE, bufferTexData(1.0F, 0.0F, 0.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.T, GL11.GL_OBJECT_PLANE, bufferTexData(0.0F, 0.0F, 1.0F, 0.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.R, GL11.GL_OBJECT_PLANE, bufferTexData(0.0F, 0.0F, 0.0F, 1.0F));
            GlStateManager.texGenParam(GlStateManager.TexGen.Q, GL11.GL_EYE_PLANE, bufferTexData(0.0F, 1.0F, 0.0F, 0.0F));
            GlStateManager.enableTexGen(GlStateManager.TexGen.S);
            GlStateManager.enableTexGen(GlStateManager.TexGen.T);
            GlStateManager.enableTexGen(GlStateManager.TexGen.R);
            GlStateManager.enableTexGen(GlStateManager.TexGen.Q);
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL11.GL_TEXTURE);
            RenderSystem.pushMatrix();
            RenderSystem.loadIdentity();
            RenderSystem.translatef(0.0F, System.currentTimeMillis() % 700000L / 700000F, 0.0F);
            RenderSystem.scalef(f6, f6, f6);
            RenderSystem.translatef(0.5F, 0.5F, 0.0F);
            RenderSystem.rotatef((idx * idx * 4321 + idx * 9) * 2.0F, 0.0F, 0.0F, 1.0F);
            RenderSystem.translatef(-0.5F, -0.5F, 0.0F);
            RenderSystem.translated(-projectedView.x, -projectedView.z, -projectedView.y);
            float f92 = f8 + (float) projectedView.y;
            RenderSystem.translated((projectedView.x * f5) / f92, (projectedView.z * f5) / f92, -projectedView.y + 20);
        }

        @Override
        @SuppressWarnings ("deprecation")
        public void clearRenderState() {
            RenderSystem.popMatrix();
            RenderSystem.matrixMode(GL11.GL_MODELVIEW);
            RenderSystem.popMatrix();//Pop stack here.
            GlStateManager.disableTexGen(GlStateManager.TexGen.S);
            GlStateManager.disableTexGen(GlStateManager.TexGen.T);
            GlStateManager.disableTexGen(GlStateManager.TexGen.R);
            GlStateManager.disableTexGen(GlStateManager.TexGen.Q);
            state.renderStates.forEach(RenderState::clearRenderState);
        }

        @Override
        public boolean equals(Object other) {
            return other == this;
        }

        @Override
        public int hashCode() {
            return System.identityHashCode(this);
        }
    }
}
