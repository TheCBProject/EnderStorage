package codechicken.enderstorage.tile;

import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.init.ModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.misc.EnderDyeButton;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.block.BlockState;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.util.SoundEvents.*;

public class TileEnderChest extends TileFrequencyOwner {

    public double a_lidAngle;
    public double b_lidAngle;
    public int c_numOpen;
    public int rotation;

    private LazyOptional<IItemHandler> itemHandler = LazyOptional.empty();

    public static EnderDyeButton[] buttons;

    static {
        buttons = new EnderDyeButton[3];
        for (int i = 0; i < 3; i++) {
            buttons[i] = new EnderDyeButton(i);
        }
    }

    public TileEnderChest() {
        super(ModContent.tileEnderChestType);
    }

    @Override
    public void tick() {
        super.tick();

        if (!world.isRemote && (world.getGameTime() % 20 == 0 || c_numOpen != getStorage().getNumOpen())) {
            c_numOpen = getStorage().getNumOpen();
            world.addBlockEvent(getPos(), getBlockState().getBlock(), 1, c_numOpen);
            world.notifyNeighborsOfStateChange(pos, getBlockState().getBlock());
        }

        b_lidAngle = a_lidAngle;
        a_lidAngle = MathHelper.approachLinear(a_lidAngle, c_numOpen > 0 ? 1 : 0, 0.1);

        if (b_lidAngle >= 0.5 && a_lidAngle < 0.5) {
            world.playSound(null, getPos(), EnderStorageConfig.useVanillaEnderChestSounds ? BLOCK_ENDER_CHEST_CLOSE : BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        } else if (b_lidAngle == 0 && a_lidAngle > 0) {
            world.playSound(null, getPos(), EnderStorageConfig.useVanillaEnderChestSounds ? BLOCK_ENDER_CHEST_OPEN : BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean receiveClientEvent(int id, int type) {
        if (id == 1) {
            c_numOpen = type;
            return true;
        }
        return false;
    }

    public double getRadianLidAngle(float frame) {
        double a = MathHelper.interpolate(b_lidAngle, a_lidAngle, frame);
        a = 1.0F - a;
        a = 1.0F - a * a * a;
        return a * 3.141593 * -0.5;
    }

    @Override
    public EnderItemStorage getStorage() {
        return EnderStorageManager.instance(world.isRemote).getStorage(frequency, EnderItemStorage.TYPE);
    }

    @Override
    public void onFrequencySet() {
        itemHandler.invalidate();
        itemHandler = LazyOptional.of(() -> new InvWrapper(getStorage()));
    }

    @Override
    public void remove() {
        super.remove();
        itemHandler.invalidate();
    }

    @Override
    public void writeToPacket(MCDataOutput packet) {
        super.writeToPacket(packet);
        packet.writeByte(rotation);
    }

    @Override
    public void readFromPacket(MCDataInput packet) {
        super.readFromPacket(packet);
        rotation = packet.readUByte() & 3;
    }

    @Override
    public void onPlaced(LivingEntity entity) {
        rotation = (int) Math.floor(entity.rotationYaw * 4 / 360 + 2.5D) & 3;
        onFrequencySet();
    }

    @Override
    public CompoundNBT write(CompoundNBT tag) {
        super.write(tag);
        tag.putByte("rot", (byte) rotation);
        return tag;
    }

    @Override
    public void read(BlockState state, CompoundNBT tag) {
        super.read(state, tag);
        rotation = tag.getByte("rot") & 3;
    }

    @Override
    public boolean activate(PlayerEntity player, int subHit, Hand hand) {
        getStorage().openContainer((ServerPlayerEntity) player, new TranslationTextComponent(getBlockState().getBlock().getTranslationKey()));
        return true;
    }

    @Override
    public boolean rotate() {
        if (!world.isRemote) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getUpdatePacket(), world, pos.getX() >> 4, pos.getZ() >> 4);
        }
        return true;
    }

    @Override
    public int comparatorInput() {
        return itemHandler.map(ItemHandlerHelper::calcRedstoneFromInventory).orElse(0);
    }

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (!removed && cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
