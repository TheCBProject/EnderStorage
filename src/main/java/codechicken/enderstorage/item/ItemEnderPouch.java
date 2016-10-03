package codechicken.enderstorage.item;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.handler.ConfigurationHandler;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderChest;
import codechicken.enderstorage.util.LogHelper;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class ItemEnderPouch extends Item {

    public ItemEnderPouch() {
        setMaxStackSize(1);
        setCreativeTab(CreativeTabs.TRANSPORTATION);
        setUnlocalizedName("enderPouch");
    }

    @Override
    public void addInformation(ItemStack stack, EntityPlayer player, List<String> list, boolean extended) {
        Frequency freq = Frequency.fromItemStack(stack);
        if (freq.owner != null) {
            list.add(freq.owner);
        }
        list.add(String.format("%s/%s/%s", freq.getLocalizedLeft(), freq.getLocalizedMiddle(), freq.getLocalizedRight()));
    }

    @Override
    public EnumActionResult onItemUseFirst(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, EnumHand hand) {
        if (world.isRemote) {
            return EnumActionResult.PASS;
        }

        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEnderChest && player.isSneaking()) {
            TileEnderChest chest = (TileEnderChest) tile;
            if (!stack.hasTagCompound()) {
                stack.setTagCompound(new NBTTagCompound());
            }
            NBTTagCompound frequencyTag = new NBTTagCompound();
            Frequency frequency = chest.frequency.copy();
            if (ConfigurationHandler.anarchyMode && !frequency.owner.equals(player.getDisplayNameString())) {
                frequency.setOwner(null);
            }

            frequency.writeNBT(frequencyTag);
            stack.getTagCompound().setTag("Frequency", frequencyTag);

            return EnumActionResult.SUCCESS;
        }
        return EnumActionResult.PASS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {

        if (world.isRemote || player.isSneaking()) {
            return new ActionResult<ItemStack>(EnumActionResult.PASS, stack);
        }
        Frequency frequency = Frequency.fromItemStack(stack);
        ((EnderItemStorage) EnderStorageManager.instance(world.isRemote).getStorage(frequency, "item")).openSMPGui(player, stack.getUnlocalizedName() + ".name");
        return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, stack);
    }
}
