package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.client.EnderPouchBakery;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.lib.model.bakery.IBakeryProvider;
import codechicken.lib.model.bakery.generation.IBakery;
import codechicken.lib.util.ItemNBTUtils;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class ItemEnderPouch extends Item implements IBakeryProvider {

    public ItemEnderPouch() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setUnlocalizedName("enderPouch");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        Frequency freq = Frequency.readFromStack(stack);
        if (freq.owner != null) {
            tooltip.add(freq.owner);
        }
        tooltip.add(freq.getTooltip());
    }

    @Override
    public EnumActionResult onItemUseFirst(EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        ItemStack stack = player.getHeldItem(hand);
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEnderChest && player.isSneaking()) {
            TileEnderChest chest = (TileEnderChest) tile;
            ItemNBTUtils.validateTagExists(stack);
            Frequency frequency = chest.frequency.copy();
            if (ConfigurationHandler.anarchyMode && !frequency.owner.equals(player.getDisplayNameString())) {
                frequency.setOwner(null);
            }

            frequency.writeToStack(stack);

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (world.isRemote || player.isSneaking()) {
            return new ActionResult<>(EnumActionResult.PASS, stack);
        }
        Frequency frequency = Frequency.readFromStack(stack);
        ((EnderItemStorage) EnderStorageManager.instance(world.isRemote).getStorage(frequency, "item")).openSMPGui(player, stack.getUnlocalizedName() + ".name");
        return new ActionResult<>(EnumActionResult.SUCCESS, stack);
    }

    @SideOnly (Side.CLIENT)
    @Override
    public IBakery getBakery() {
        return EnderPouchBakery.INSTANCE;
    }
}
