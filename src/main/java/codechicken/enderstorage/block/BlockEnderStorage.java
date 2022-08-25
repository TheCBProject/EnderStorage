package codechicken.enderstorage.block;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.colour.EnumColour;
import codechicken.lib.raytracer.SubHitBlockHitResult;
import codechicken.lib.raytracer.RayTracer;
import codechicken.lib.util.ItemUtils;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by covers1624 on 4/11/2016.
 */
public abstract class BlockEnderStorage extends BaseEntityBlock// implements ICustomParticleBlock
{

    public BlockEnderStorage(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.INVISIBLE;
    }

    @Override
    public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
        return willHarvest || super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
    }

    @Override
    public void playerDestroy(Level worldIn, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity te, ItemStack stack) {
        super.playerDestroy(worldIn, player, pos, state, te, stack);
        worldIn.setBlockAndUpdate(pos, Blocks.AIR.defaultBlockState());
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        List<ItemStack> drops = new ArrayList<>();
        TileFrequencyOwner tile = (TileFrequencyOwner) builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (tile != null) {
            drops.add(createItem(tile.getFrequency()));
            if (EnderStorageConfig.anarchyMode && tile.getFrequency().hasOwner()) {
                drops.add(EnderStorageConfig.getPersonalItem().copy());
            }
        }
        return drops;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult rayTraceResult, BlockGetter world, BlockPos pos, Player player) {
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
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult clientHit) {
        if (world.isClientSide) {
            return InteractionResult.SUCCESS;
        }
        BlockEntity tile = world.getBlockEntity(pos);
        if (!(tile instanceof TileFrequencyOwner owner)) {
            return InteractionResult.FAIL;
        }

        //Normal block trace.
        HitResult rawHit = RayTracer.retrace(player);
        if (!(rawHit instanceof SubHitBlockHitResult hit)) {
            return InteractionResult.FAIL;
        }
        if (hit.subHit == 4) {
            ItemStack item = player.getInventory().getSelected();
            if (player.isCrouching() && owner.getFrequency().hasOwner()) {
                if (!player.getAbilities().instabuild && !player.getInventory().add(EnderStorageConfig.getPersonalItem().copy())) {
                    return InteractionResult.FAIL;
                }

                owner.setFreq(owner.getFrequency().copy().clearOwner());
                return InteractionResult.SUCCESS;
            } else if (!item.isEmpty() && ItemUtils.areStacksSameType(item, EnderStorageConfig.getPersonalItem())) {
                if (!owner.getFrequency().hasOwner()) {
                    owner.setFreq(owner.getFrequency().copy().setOwner(player));
                    if (!player.getAbilities().instabuild) {
                        item.shrink(1);
                    }
                    return InteractionResult.SUCCESS;
                }
            }
        } else if (hit.subHit >= 1 && hit.subHit <= 3) {
            ItemStack item = player.getInventory().getSelected();
            if (!item.isEmpty()) {
                EnumColour dye = EnumColour.fromDyeStack(item);
                if (dye != null) {
                    EnumColour[] colours = { null, null, null };
                    if (colours[hit.subHit - 1] == dye) {
                        return InteractionResult.FAIL;
                    }
                    colours[hit.subHit - 1] = dye;
                    owner.setFreq(owner.getFrequency().copy().set(colours));
                    if (!player.getAbilities().instabuild) {
                        item.shrink(1);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }
        return !player.isCrouching() && owner.activate(player, hit.subHit, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
    }

    @Override
    public void neighborChanged(BlockState state, Level world, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).onNeighborChange(fromPos);
        }
    }

    @Override
    public void setPlacedBy(Level world, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).onPlaced(placer);
        }
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            return ((TileFrequencyOwner) tile).getLightValue();
        }
        return 0;
    }

    @Override
    public boolean canConnectRedstone(BlockState state, BlockGetter world, BlockPos pos, Direction side) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof TileFrequencyOwner && ((TileFrequencyOwner) tile).redstoneInteraction();
    }

    @Override
    public int getAnalogOutputSignal(BlockState state, Level world, BlockPos pos) {
        BlockEntity tile = world.getBlockEntity(pos);
        return tile instanceof TileFrequencyOwner ? ((TileFrequencyOwner) tile).comparatorInput() : 0;
    }

    @Override
    public BlockState rotate(BlockState state, LevelAccessor world, BlockPos pos, Rotation direction) {
        BlockEntity tile = world.getBlockEntity(pos);
        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).rotate();
        }
        return state;
    }

    @Override
    public boolean triggerEvent(BlockState state, Level worldIn, BlockPos pos, int eventID, int eventParam) {
        super.triggerEvent(state, worldIn, pos, eventID, eventParam);
        BlockEntity tileentity = worldIn.getBlockEntity(pos);
        return tileentity != null && tileentity.triggerEvent(eventID, eventParam);
    }
}
