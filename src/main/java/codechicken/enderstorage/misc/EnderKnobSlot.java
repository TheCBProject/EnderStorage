package codechicken.enderstorage.misc;

import codechicken.lib.vec.Vector3;
import net.minecraft.util.math.AxisAlignedBB;

public class EnderKnobSlot {

    public EnderKnobSlot(int meta) {

        Vector3[] verts = new Vector3[8];
        verts[0] = new Vector3(-0.0625, 0.4375, -0.5);
        verts[1] = new Vector3(0.0625, 0.4375, -0.5);
        verts[3] = new Vector3(-0.0625, 0.4375, -0.4375);
        verts[2] = new Vector3(0.0625, 0.4375, -0.4375);
        verts[5] = new Vector3(-0.0625, 0.6875, -0.5);
        verts[4] = new Vector3(0.0625, 0.6875, -0.5);
        verts[6] = new Vector3(-0.0625, 0.6875, -0.4375);
        verts[7] = new Vector3(0.0625, 0.6875, -0.4375);

        for (int i = 0; i < 8; i++) {
            verts[i].rotate((meta + 2) * -0.5 * 3.14159, new Vector3(0, 1, 0));
            verts[i].add(0.5, 0, 0.5);
        }

        aabb = cornersToAABB(verts);
    }

    private AxisAlignedBB aabb;

    public AxisAlignedBB getSelectionBB() {

        return aabb;
    }

    public static AxisAlignedBB cornersToAABB(Vector3[] corners) {

        Vector3 min = corners[0].copy();
        Vector3 max = corners[0].copy();
        for (int i = 1; i < corners.length; i++) {
            Vector3 vec = corners[i];
            if (vec.x < min.x) {
                min.x = vec.x;
            } else if (vec.x > max.x) {
                max.x = vec.x;
            }
            if (vec.y < min.y) {
                min.y = vec.y;
            } else if (vec.y > max.y) {
                max.y = vec.y;
            }
            if (vec.z < min.z) {
                min.z = vec.z;
            } else if (vec.z > max.z) {
                max.z = vec.z;
            }
        }
        return new AxisAlignedBB(min.x, min.y, min.z, max.x, max.y, max.z);
    }
}
