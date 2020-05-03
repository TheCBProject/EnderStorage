package codechicken.enderstorage.block;

import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.raytracer.IndexedCuboid6;
import codechicken.lib.raytracer.SubHitVoxelShape;
import codechicken.lib.vec.*;
import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static codechicken.lib.vec.Vector3.CENTER;

/**
 * Created by covers1624 on 29/10/19.
 */
public class BlockEnderTank extends BlockEnderStorage {

    private static IndexedCuboid6 TANK = new IndexedCuboid6(0, new Cuboid6(0.15, 0, 0.15, 0.85, 0.916, 0.85));
    private static final IndexedCuboid6[][] BUTTONS = new IndexedCuboid6[4][3];
    private static final IndexedCuboid6[] DIAL = new IndexedCuboid6[4];

    private static final VoxelShape TANK_SHAPE = VoxelShapes.create(TANK.aabb());
    private static final VoxelShape[] SHAPES = new VoxelShape[4];

    public static Transformation[] buttonT = new Transformation[3];

    static {
        for (int i = 0; i < 3; i++) {
            buttonT[i] = new Scale(0.6).with(new Translation(0.35 + (2 - i) * 0.15, 0.91, 0.5));
        }

        Cuboid6 dialBase = new Cuboid6(0.358, 0.268, 0.05, 0.662, 0.565, 0.15);
        for (int rot = 0; rot < 4; rot++) {
            Transformation rotation = Rotation.quarterRotations[rot ^ 2].at(CENTER);
            for (int button = 0; button < 3; button++) {
                BUTTONS[rot][button] = new IndexedCuboid6(button + 1, TileFrequencyOwner.selection_button.copy().apply(buttonT[button]).apply(rotation));
            }
            DIAL[rot] = new IndexedCuboid6(4, dialBase.copy().apply(rotation));

            List<IndexedCuboid6> cuboids = new ArrayList<>();
            cuboids.add(TANK);
            Collections.addAll(cuboids, BUTTONS[rot]);
            cuboids.add(DIAL[rot]);
            SHAPES[rot] = new SubHitVoxelShape(TANK_SHAPE, cuboids);

        }
    }

    public BlockEnderTank(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = TANK_SHAPE;
        TileEntity t = worldIn.getTileEntity(pos);
        if (t instanceof TileEnderTank) {
            TileEnderTank tile = (TileEnderTank) t;
            shape = SHAPES[tile.rotation];
        }
        return shape;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnderTank();
    }
}
