package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.EnderPouchBakery;
import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUseContext;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderPouch extends Item implements IBakeryProvider {

    public ItemEnderPouch() {
        super(new Item.Properties()//
                .stacksTo(1)//
                .tab(ItemGroup.TAB_TRANSPORTATION)//
        );
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        Frequency frequency = Frequency.readFromStack(stack);
        if (frequency.hasOwner()) {
            tooltip.add(frequency.getOwnerName());
        }
        tooltip.add(frequency.getTooltip());
    }

    @Override
    public ActionResultType onItemUseFirst(ItemStack stack, ItemUseContext context) {
        World world = context.getLevel();
        if (world.isClientSide()) {
            return ActionResultType.PASS;
        }

        TileEntity tile = world.getBlockEntity(context.getClickedPos());
        if (tile instanceof TileEnderChest && context.getPlayer().isCrouching()) {
            TileEnderChest chest = (TileEnderChest) tile;
            Frequency frequency = chest.getFrequency().copy();
            if (EnderStorageConfig.anarchyMode && !(frequency.owner != null && frequency.owner.equals(context.getPlayer().getUUID()))) {
                frequency.setOwner(null);
            }

            frequency.writeToStack(stack);

            return ActionResultType.SUCCESS;
        }
        return ActionResultType.PASS;
    }

    @Override
    public ActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (player.isCrouching()) {
            return new ActionResult<>(ActionResultType.PASS, stack);
        }
        if (!world.isClientSide) {
            Frequency frequency = Frequency.readFromStack(stack);
            EnderStorageManager.instance(world.isClientSide).getStorage(frequency, EnderItemStorage.TYPE).openContainer((ServerPlayerEntity) player, new TranslationTextComponent(stack.getDescriptionId()));
        }
        return new ActionResult<>(ActionResultType.SUCCESS, stack);
    }

    @Override
    @OnlyIn (Dist.CLIENT)
    public IBakery getBakery() {
        return EnderPouchBakery.INSTANCE;
    }
}
