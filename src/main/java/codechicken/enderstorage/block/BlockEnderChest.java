package codechicken.enderstorage.block;

import codechicken.enderstorage.misc.EnderKnobSlot;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.math.MathHelper;
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

/**
 * Created by covers1624 on 29/10/19.
 */
public class BlockEnderChest extends BlockEnderStorage {

    private static final IndexedCuboid6 CHEST = new IndexedCuboid6(0, new Cuboid6(1 / 16D, 0, 1 / 16D, 15 / 16D, 14 / 16D, 15 / 16D));
    private static final IndexedCuboid6[][] BUTTONS = new IndexedCuboid6[4][3];
    private static final IndexedCuboid6[] LATCH = new IndexedCuboid6[4];

    private static final VoxelShape CHEST_SHAPE = VoxelShapes.create(CHEST.aabb());
    private static final VoxelShape[][] SHAPES = new VoxelShape[4][2];

    public static final Transformation[] buttonT = new Transformation[3];

    static {
        for (int button = 0; button < 3; button++) {
            buttonT[button] = new Translation(-(3 / 16D) + ((3D / 16D) * button), 14D / 16D, 0);
        }
        for (int rot = 0; rot < 4; rot++) {
            //Build buttons and latch.
            for (int button = 0; button < 3; button++) {
                Cuboid6 cuboid = TileFrequencyOwner.selection_button.copy();
                cuboid.apply(new Translation(0.5, 0, 0.5));
                cuboid.apply(buttonT[button]);
                cuboid.apply(new Rotation((-90 * (rot)) * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0, 0.5)));
                BUTTONS[rot][button] = new IndexedCuboid6(button + 1, cuboid);
            }
            LATCH[rot] = new IndexedCuboid6(4, new Cuboid6(new EnderKnobSlot(rot).getSelectionBB()));

            //Build all VoxelShapes.
            for (int state = 0; state < 2; state++) {
                List<IndexedCuboid6> cuboids = new ArrayList<>();
                cuboids.add(CHEST);
                if (state == 0) {
                    Collections.addAll(cuboids, BUTTONS[rot]);
                    cuboids.add(LATCH[rot]);
                }
                SHAPES[rot][state] = new SubHitVoxelShape(CHEST_SHAPE, cuboids);
            }
        }
    }

    public BlockEnderChest(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        VoxelShape shape = CHEST_SHAPE;
        TileEntity t = worldIn.getTileEntity(pos);
        if (t instanceof TileEnderChest) {
            TileEnderChest tile = (TileEnderChest) t;
            shape = SHAPES[tile.rotation][tile.getRadianLidAngle(0) >= 0 ? 0 : 1];
        }
        return shape;
    }

    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TileEnderChest();
    }

}
