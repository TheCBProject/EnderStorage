package codechicken.enderstorage.block;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 4/11/2016.
 */
public abstract class BlockEnderStorage extends Block// implements ICustomParticleBlock
{

    public BlockEnderStorage(Properties properties) {
        super(properties);
    }

    @Override
    public BlockRenderType getRenderShape(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public boolean removedByPlayer(BlockState state, World world, BlockPos pos, PlayerEntity player, boolean willHarvest, FluidState fluid) {
        return willHarvest || super.removedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void playerDestroy(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        TileFrequencyOwner tile = (TileFrequencyOwner) builder.getOptionalParameter(LootParameters.BLOCK_ENTITY);
        if (tile != null) {
            drops.add(createItem(tile.getFrequency()));
            if (EnderStorageConfig.anarchyMode && tile.getFrequency().hasOwner()) {
                drops.add(EnderStorageConfig.personalItem.copy());
            }
        }
        return drops;
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult rayTraceResult, IBlockReader world, BlockPos pos, PlayerEntity player) {
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getBlockEntity(pos);
        return createItem(tile.getFrequency());
    }

    private ItemStack createItem(Frequency freq) {
        ItemStack stack = new ItemStack(this, 1);
        if (EnderStorageConfig.anarchyMode) {
            freq.setOwner(null);
        }
        freq.writeToStack(stack);
        return stack;
    }

    @Override
    public ActionResultType use(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult clientHit) {
        if (world.isClientSide) {
            return ActionResultType.SUCCESS;
        }
        TileEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileFrequencyOwner)) {
            return ActionResultType.FAIL;
        }
        TileFrequencyOwner owner = (TileFrequencyOwner) tile;

        //Normal block trace.
        RayTraceResult hit = RayTracer.retrace(player);
        if (hit == null) {
            return ActionResultType.FAIL;
        }
        if (hit.subHit == 4) {
            ItemStack item = player.inventory.getSelected();
            if (player.isCrouching() && owner.getFrequency().hasOwner()) {
                if (!player.abilities.instabuild && !player.inventory.add(EnderStorageConfig.personalItem.copy())) {
                    return ActionResultType.FAIL;
                }

                owner.setFreq(owner.getFrequency().copy().setOwner(null));
                return ActionResultType.SUCCESS;
            } else if (!item.isEmpty() && ItemUtils.areStacksSameType(item, EnderStorageConfig.personalItem)) {
                if (!owner.getFrequency().hasOwner()) {
                    owner.setFreq(owner.getFrequency().copy()//
                            .setOwner(player.getUUID())//
                            .setOwnerName(player.getName())//
                    );
                    if (!player.abilities.instabuild) {
                        item.shrink(1);
                    }
                    return ActionResultType.SUCCESS;
                }
            }
        } else if (hit.subHit >= 1 && hit.subHit <= 3) {
            ItemStack item = player.inventory.getSelected();
            if (!item.isEmpty()) {
                EnumColour dye = EnumColour.fromDyeStack(item);
                if (dye != null) {
                    EnumColour[] colours = { null, null, null };
                    if (colours[hit.subHit - 1] == dye) {
                        return ActionResultType.FAIL;
                    }
                    colours[hit.subHit - 1] = dye;
                    owner.setFreq(owner.getFrequency().copy().set(colours));
                    if (!player.abilities.instabuild) {
                        item.shrink(1);
                    }
                    return ActionResultType.FAIL;
                }
            }
        }
        return !player.isCrouching() && owner.activate(player, hit.subHit, hand) ? ActionResultType.SUCCESS : ActionResultType.FAIL;
    }

    @Override
    public void neighborChanged(BlockState state, World world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).onNeighborChange(fromPos);
        }
    }

    @Override
    public void setPlacedBy(World world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).onPlaced(placer);
        }
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            return ((TileFrequencyOwner) tile).getLightValue();
        }
        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, IBlockReader world, BlockPos pos, Direction side) {
        TileEntity tile = world.getBlockEntity(pos);
        return tile instanceof TileFrequencyOwner && ((TileFrequencyOwner) tile).redstoneInteraction();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, World world, BlockPos pos) {
        TileEntity tile = world.getBlockEntity(pos);
        return tile instanceof TileFrequencyOwner ? ((TileFrequencyOwner) tile).comparatorInput() : 0;
    }

    @Override
    public BlockState rotate(BlockState state, IWorld world, BlockPos pos, Rotation direction) {
        TileEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).rotate();
        }
        return state;
    }

    @Override
    public boolean triggerEvent(BlockState state, World worldIn, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, worldIn, pos, eventID, eventParam);
        TileEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(eventID, eventParam);
    }
}
