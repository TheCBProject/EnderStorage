package codechicken.enderstorage.block;

import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.misc.EnderKnobSlot;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.math.MathHelper;
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
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

/**
 * Created by covers1624 on 29/10/19.
 */
public class BlockEnderChest extends BlockEnderStorage {

    private static final IndexedVoxelShape CHEST = new IndexedVoxelShape(VoxelShapeCache.getShape(new Cuboid6(1 / 16D, 0, 1 / 16D, 15 / 16D, 14 / 16D, 15 / 16D)), 0);
    private static final IndexedVoxelShape[][] BUTTONS = new IndexedVoxelShape[4][3];
    private static final IndexedVoxelShape[] LATCH = new IndexedVoxelShape[4];

    private static final VoxelShape[][] SHAPES = new VoxelShape[4][2];

    public static final Transformation[] buttonT = new Transformation[3];

    static {
        for (int button = 0; button < 3; button++) {
            buttonT[button] = new Translation(-(3 / 16D) + ((3D / 16D) * button), 14D / 16D, 0);
        }
        for (int rot = 0; rot < 4; rot++) {
            //Build buttons and latch.
            for (int button = 0; button < 3; button++) {
                Cuboid6 cuboid = TileFrequencyOwner.SELECTION_BUTTON.copy();
                cuboid.apply(buttonT[button]);
                cuboid.apply(new Translation(0.5, 0, 0.5));
                cuboid.apply(new Rotation((-90 * (rot)) * MathHelper.torad, Vector3.Y_POS).at(new Vector3(0.5, 0, 0.5)));
                BUTTONS[rot][button] = new IndexedVoxelShape(VoxelShapeCache.getShape(cuboid), button + 1);
            }
            LATCH[rot] = new IndexedVoxelShape(VoxelShapeCache.getShape(new Cuboid6(new EnderKnobSlot(rot).getSelectionBB())), 4);

            //Build all VoxelShapes.
            for (int state = 0; state < 2; state++) {
                ImmutableSet.Builder<IndexedVoxelShape> cuboids = ImmutableSet.builder();
                cuboids.add(CHEST);
                if (state == 0) {
                    cuboids.add(BUTTONS[rot]);
                    cuboids.add(LATCH[rot]);
                }
                SHAPES[rot][state] = new MultiIndexedVoxelShape(CHEST, cuboids.build());
            }
        }
    }

    public BlockEnderChest(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
        VoxelShape shape = CHEST;
        BlockEntity t = worldIn.getBlockEntity(pos);
        if (t instanceof TileEnderChest tile) {
            shape = SHAPES[tile.rotation][tile.getRadianLidAngle(0) >= 0 ? 0 : 1];
        }
        return shape;
    }

    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEnderChest(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level p_153212_, BlockState p_153213_, BlockEntityType<T> p_153214_) {
        return createTickerHelper(p_153214_, EnderStorageModContent.ENDER_CHEST_TILE.get(), (level, pos, state, tile) -> tile.tick());
    }
}
