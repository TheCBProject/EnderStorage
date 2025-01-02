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

import java.util.List;

public class ItemEnderPouch extends Item {

    public ItemEnderPouch() {
        super(new Item.Properties()
                .stacksTo(1)
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext ctx, List<Component> tooltip, TooltipFlag flagIn) {
        Frequency frequency = Frequency.readFromStack(stack);
        frequency.ownerName().ifPresent(tooltip::add);
        tooltip.add(frequency.getTooltip());
    }

    @Override
    public InteractionResult onItemUseFirst(ItemStack stack, UseOnContext context) {
        Level world = context.getLevel();
        if (world.isClientSide()) {
            return InteractionResult.PASS;
        }

        Player player = context.getPlayer();
        BlockEntity tile = world.getBlockEntity(context.getClickedPos());
        if (tile instanceof TileEnderChest chest && player != null && player.isCrouching()) {
            Frequency frequency = chest.getFrequency();
            if (EnderStorageConfig.anarchyMode && !(frequency.owner().isPresent() && frequency.owner().get().equals(player.getUUID()))) {
                frequency = frequency.withoutOwner();
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
            EnderStorageManager.instance(false).getStorage(frequency, EnderItemStorage.TYPE).openContainer((ServerPlayer) player, Component.translatable(stack.getDescriptionId()));
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, stack);
    }
}
