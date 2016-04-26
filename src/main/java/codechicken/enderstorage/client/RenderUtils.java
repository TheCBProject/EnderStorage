package codechicken.enderstorage.client;

import net.minecraft.util.math.BlockPos;

/**
 * Created by covers1624 on 4/12/2016.
 */
public class RenderUtils extends codechicken.lib.render.RenderUtils{

    public static float getPearlBob(double time) {
        return (float) Math.sin(time / 25 * 3.141593) * 0.1F;
    }

    public static int getTimeOffset(BlockPos pos) {
        return getTimeOffset(pos.getX(), pos.getY(), pos.getZ());
    }

    public static int getTimeOffset(int x, int y, int z) {
        return x * 3 + y * 5 + z * 9;
    }
}
