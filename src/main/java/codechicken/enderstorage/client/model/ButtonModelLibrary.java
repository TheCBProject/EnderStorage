package codechicken.enderstorage.client.model;

import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.render.CCModel;
import codechicken.lib.vec.Vector3;
import codechicken.lib.vec.Vertex5;

public class ButtonModelLibrary {

    public static CCModel button;

    static {
        button = CCModel.quadModel(20);
        Vector3 min = TileFrequencyOwner.SELECTION_BUTTON.min;
        Vector3 max = TileFrequencyOwner.SELECTION_BUTTON.max;
        Vector3[] corners = new Vector3[8];
        corners[0] = new Vector3(min.x, min.y, min.z);
        corners[1] = new Vector3(max.x, min.y, min.z);
        corners[3] = new Vector3(min.x, max.y, min.z);
        corners[2] = new Vector3(max.x, max.y, min.z);
        corners[4] = new Vector3(min.x, min.y, max.z);
        corners[5] = new Vector3(max.x, min.y, max.z);
        corners[7] = new Vector3(min.x, max.y, max.z);
        corners[6] = new Vector3(max.x, max.y, max.z);

        int i = 0;
        Vertex5[] verts = button.verts;

        verts[i++] = new Vertex5(corners[7], 0.0938, 0.0625);
        verts[i++] = new Vertex5(corners[6], 0.1562, 0.0625);
        verts[i++] = new Vertex5(corners[2], 0.1562, 0.1875);
        verts[i++] = new Vertex5(corners[3], 0.0938, 0.1875);

        verts[i++] = new Vertex5(corners[4], 0.0938, 0.0313);
        verts[i++] = new Vertex5(corners[5], 0.1562, 0.0624);
        verts[i++] = new Vertex5(corners[6], 0.1562, 0.0624);
        verts[i++] = new Vertex5(corners[7], 0.0938, 0.0313);

        verts[i++] = new Vertex5(corners[0], 0.0938, 0.2186);
        verts[i++] = new Vertex5(corners[3], 0.0938, 0.1876);
        verts[i++] = new Vertex5(corners[2], 0.1562, 0.1876);
        verts[i++] = new Vertex5(corners[1], 0.1562, 0.2186);

        verts[i++] = new Vertex5(corners[6], 0.1563, 0.0626);
        verts[i++] = new Vertex5(corners[5], 0.1874, 0.0626);
        verts[i++] = new Vertex5(corners[1], 0.1874, 0.1874);
        verts[i++] = new Vertex5(corners[2], 0.1563, 0.1874);

        verts[i++] = new Vertex5(corners[7], 0.0937, 0.0626);
        verts[i++] = new Vertex5(corners[3], 0.0937, 0.1874);
        verts[i++] = new Vertex5(corners[0], 0.0626, 0.1874);
        verts[i++] = new Vertex5(corners[4], 0.0626, 0.0626);

        button.computeNormals();
    }
}
