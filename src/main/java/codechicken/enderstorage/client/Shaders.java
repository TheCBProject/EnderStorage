package codechicken.enderstorage.client;

import codechicken.lib.render.shader.CCShaderInstance;
import codechicken.lib.render.shader.CCUniform;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.covers1624.quack.util.CrashLock;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.RegisterShadersEvent;
import org.jetbrains.annotations.Nullable;

import static codechicken.enderstorage.EnderStorage.MOD_ID;
import static java.util.Objects.requireNonNull;

/**
 * Created by covers1624 on 6/4/22.
 */
public class Shaders {

    private static final CrashLock LOCK = new CrashLock("Already Initialized");

    private static @Nullable CCShaderInstance starfieldShader;
    private static @Nullable CCUniform starfieldTime;
    private static @Nullable CCUniform starfieldYaw;
    private static @Nullable CCUniform starfieldPitch;
    private static @Nullable CCUniform starfieldAlpha;

    public static void init(IEventBus modBus) {
        LOCK.lock();
        modBus.addListener(Shaders::onRegisterShaders);
    }

    private static void onRegisterShaders(RegisterShadersEvent event) {
        event.registerShader(CCShaderInstance.create(event.getResourceProvider(), ResourceLocation.fromNamespaceAndPath(MOD_ID, "starfield"), DefaultVertexFormat.POSITION), e -> {
            starfieldShader = (CCShaderInstance) e;
            starfieldTime = starfieldShader.getUniform("Time");
            starfieldYaw = starfieldShader.getUniform("Yaw");
            starfieldPitch = starfieldShader.getUniform("Pitch");
            starfieldAlpha = starfieldShader.getUniform("Alpha");
        });
    }

    // @formatter:off
    public static CCShaderInstance starfieldShader() { return requireNonNull(starfieldShader); }
    public static CCUniform starfieldTime() { return requireNonNull(starfieldTime); }
    public static CCUniform starfieldYaw() { return requireNonNull(starfieldYaw); }
    public static CCUniform starfieldPitch() { return requireNonNull(starfieldPitch); }
    public static CCUniform starfieldAlpha() { return requireNonNull(starfieldAlpha); }
    // @formatter:on
}
