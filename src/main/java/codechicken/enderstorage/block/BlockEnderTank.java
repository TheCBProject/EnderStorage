package codechicken.enderstorage.block;

import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.raytracer.IndexedVoxelShape;
import codechicken.lib.raytracer.MultiIndexedVoxelShape;
import codechicken.lib.raytracer.VoxelShapeCache;
import codechicken.lib.vec.*;
import com.google.common.collect.ImmutableSet;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

import static codechicken.lib.vec.Vector3.CENTER;

/**
 * Created by covers1624 on 29/10/19.
 */
public class BlockEnderTank extends BlockEnderStorage {

    private static final IndexedVoxelShape TANK = new IndexedVoxelShape(Shapes.create(0.15, 0, 0.15, 0.85, 0.916, 0.85), 0);
    private static final IndexedVoxelShape[][] BUTTONS = new IndexedVoxelShape[4][3];
    private static final IndexedVoxelShape[] DIAL = new IndexedVoxelShape[4];
    private static final MultiIndexedVoxelShape[] SHAPES = new MultiIndexedVoxelShape[4];

    public static Transformation[] buttonT = new Transformation[3];

    static {
        //          1 2 3 4  5 6 7 8  9 ROT
        int asd = 0b00000000_00000000_00000000_00000000;
        for (int i = 0; i < 3; i++) {
            buttonT[i] = new Scale(0.6).with(new Translation(0.35 + (2 - i) * 0.15, 0.91, 0.5));
        }

        Cuboid6 dialBase = new Cuboid6(0.358, 0.268, 0.05, 0.662, 0.565, 0.15);
        for (int rot = 0; rot < 4; rot++) {
            Transformation rotation = Rotation.quarterRotations[rot ^ 2].at(CENTER);
            for (int button = 0; button < 3; button++) {
                BUTTONS[rot][button] = new IndexedVoxelShape(
                        VoxelShapeCache.getShape(TileFrequencyOwner.SELECTION_BUTTON.copy().apply(buttonT[button]).apply(rotation)),
                        button + 1
                );
            }
            DIAL[rot] = new IndexedVoxelShape(VoxelShapeCache.getShape(dialBase.copy().apply(rotation)), 4);

            ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
            cuboids.add(TANK);
            cuboids.add(BUTTONS[rot]);
            cuboids.add(DIAL[rot]);
            SHAPES[rot] = new MultiIndexedVoxelShape(cuboids.build());
        }
    }

    public BlockEnderTank(Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape shape = TANK;
        BlockEntity t = worldIn.getBlockEntity(pos);
        if (t instanceof TileEnderTank tile) {
            shape = SHAPES[tile.rotation];
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEnderTank(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return createTickerHelper(p_153214_, EnderStorageModContent.ENDER_TANK_TILE.get(), (level, pos, state, tile) -> tile.tick());
    }
}
