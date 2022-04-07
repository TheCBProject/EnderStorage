package codechicken.enderstorage.client;

import com.mojang.blaze3d.shaders.Uniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.covers1624.quack.util.SneakyUtils;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 6/4/22.
 */
public class Shaders {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    public static ShaderInstance starfieldShader;
    public static Uniform starfieldTime;
    public static Uniform starfieldYaw;
    public static Uniform starfieldPitch;
    public static Uniform starfieldAlpha;

    public static void init() {
        LOCK.lock();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(Shaders::onRegisterShaders);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        SneakyUtils.sneaky(() -> event.registerShader(new ShaderInstance(event.getResourceManager(), new ResourceLocation(MOD_ID, "starfield"), DefaultVertexFormat.POSITION), e -> {
            starfieldShader = e;
            starfieldTime = e.getUniform("Time");
            starfieldYaw = e.getUniform("Yaw");
            starfieldPitch = e.getUniform("Pitch");
            starfieldAlpha = e.getUniform("Alpha");
        }));
    }
}
