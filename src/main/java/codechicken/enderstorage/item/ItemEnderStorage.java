package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class ItemEnderStorage extends BlockItem {

    public ItemEnderStorage(Block block) {
        super(block, new Properties());
    }

    public Frequency getFreq(ItemStack stack) {
        return Frequency.readFromStack(stack);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos pos, Level world, @Nullable Player player, ItemStack stack, BlockState state) {
        boolean flag = super.updateCustomBlockEntityTag(pos, world, player, stack, state);
        TileFrequencyOwner tile = (TileFrequencyOwner) world.getBlockEntity(pos);
        if (tile != null) {
            tile.setFreq(getFreq(stack));
            return true;
        }

        return flag;
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flagIn) {
        Frequency frequency = Frequency.readFromStack(stack);
        frequency.ownerName().ifPresent(tooltip::add);
        tooltip.add(frequency.getTooltip());
    }

    //    private EnderLiquidStorage getLiquidStorage(ItemStack stack) {
    //        return (EnderLiquidStorage) EnderStorageManager.instance(FMLCommonHandler.instance().getSide().isClient()).getStorage(getFreq(stack), "liquid");
    //    }
    //
    //    @Override
    //    public ICapabilityProvider initCapabilities(final ItemStack stack, NBTTagCompound nbt) {
    //        if (getMetadata(stack) == 1) {
    //            return new ICapabilityProvider() {
    //                @Override
    //                public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing) {
    //
    //                    return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY;
    //                }
    //
    //                @Override
    //                public <T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing) {
    //
    //                    return CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY.cast(capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY ? getLiquidStorage(stack) : null);
    //                }
    //            };
    //        }
    //        return null;
    //    }
}
