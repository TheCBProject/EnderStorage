package codechicken.enderstorage.client;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 6/4/22.
 */
public class Shaders {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static CCShaderInstance starfieldShader;
    public static CCUniform starfieldTime;
    public static CCUniform starfieldYaw;
    public static CCUniform starfieldPitch;
    public static CCUniform starfieldAlpha;

    public static void init() {
        LOCK.lock();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Shaders::onRegisterShaders);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceProvider(), new ResourceLocation(MOD_ID, "starfield"), DefaultVertexFormat.POSITION), e -> {
            starfieldShader = (CCShaderInstance) e;
            starfieldTime = starfieldShader.getUniform("Time");
            starfieldYaw = starfieldShader.getUniform("Yaw");
            starfieldPitch = starfieldShader.getUniform("Pitch");
            starfieldAlpha = starfieldShader.getUniform("Alpha");
        });
    }
}
