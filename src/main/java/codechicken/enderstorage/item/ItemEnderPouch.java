package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderPouch extends Item {

    public ItemEnderPouch() {
        super(new Item.Properties()
                .stacksTo(1)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        Frequency frequency = Frequency.readFromStack(stack);
        if (frequency.hasOwner()) {
            tooltip.add(frequency.getOwnerName());
        }
        tooltip.add(frequency.getTooltip());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        BlockEntity tile = world.getBlockEntity(context.getClickedPos());
        if (tile instanceof TileEnderChest chest && context.getPlayer().isCrouching()) {
            Frequency frequency = chest.getFrequency().copy();
            if (EnderStorageConfig.anarchyMode && !(frequency.owner != null && frequency.owner.equals(context.getPlayer().getUUID()))) {
                frequency.clearOwner();
            }

            frequency.writeToStack(stack);

            return InteractionResult.SUCCESS;
        }
        return InteractionResult.PASS;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            return new InteractionResultHolder<>(InteractionResult.PASS, stack);
        }
        if (!world.isClientSide) {
            Frequency frequency = Frequency.readFromStack(stack);
            EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE).openContainer((ServerPlayer) player, Component.translatable(stack.getDescriptionId()));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
