package codechicken.enderstorage.tile;

import codechicken.enderstorage.config.EnderStorageConfig;
import codechicken.enderstorage.init.EnderStorageModContent;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.lib.data.MCDataInput;
import codechicken.lib.data.MCDataOutput;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import net.minecraftforge.items.wrapper.InvWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static net.minecraft.sounds.SoundEvents.*;

public class TileEnderChest extends TileFrequencyOwner {

    public double a_lidAngle;
    public double b_lidAngle;
    public int c_numOpen;
    public int rotation;

    private LazyOptional<IItemHandler> itemHandler = LazyOptional.empty();

    public TileEnderChest(BlockPos pos, BlockState state) {
        super(EnderStorageModContent.ENDER_CHEST_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        if (!level.isClientSide && (level.getGameTime() % 20 == 0 || c_numOpen != getStorage().getNumOpen())) {
            c_numOpen = getStorage().getNumOpen();
            level.blockEvent(getBlockPos(), getBlockState().getBlock(), 1, c_numOpen);
            level.updateNeighborsAt(worldPosition, getBlockState().getBlock());
        }

        b_lidAngle = a_lidAngle;
        a_lidAngle = MathHelper.approachLinear(a_lidAngle, c_numOpen > 0 ? 1 : 0, 0.1);

        if (b_lidAngle >= 0.5 && a_lidAngle < 0.5) {
            level.playSound(null, getBlockPos(), EnderStorageConfig.useVanillaEnderChestSounds ? ENDER_CHEST_CLOSE : CHEST_CLOSE, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        } else if (b_lidAngle == 0 && a_lidAngle > 0) {
            level.playSound(null, getBlockPos(), EnderStorageConfig.useVanillaEnderChestSounds ? ENDER_CHEST_OPEN : CHEST_OPEN, SoundSource.BLOCKS, 0.5F, level.random.nextFloat() * 0.1F + 0.9F);
        }
    }

    @Override
    public boolean triggerEvent(int id, int type) {
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
        return EnderStorageManager.instance(level.isClientSide).getStorage(frequency, EnderItemStorage.TYPE);
    }

    @Override
    public void onFrequencySet() {
        itemHandler.invalidate();
        itemHandler = LazyOptional.of(() -> new InvWrapper(getStorage()));
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
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
        rotation = entity != null ? (int) Math.floor(entity.getYRot() * 4 / 360 + 2.5D) & 3 : 0;
        onFrequencySet();
        if (!level.isClientSide) {
            sendUpdatePacket();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putByte("rot", (byte) rotation);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        rotation = tag.getByte("rot") & 3;
    }

    @Override
    public boolean activate(Player player, int subHit, InteractionHand hand) {
        getStorage().openContainer((ServerPlayer) player, Component.translatable(getBlockState().getBlock().getDescriptionId()));
        return true;
    }

    @Override
    public boolean rotate() {
        if (!level.isClientSide) {
            rotation = (rotation + 1) % 4;
            PacketCustom.sendToChunk(getUpdatePacket(), level, worldPosition.getX() >> 4, worldPosition.getZ() >> 4);
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
        if (!remove && cap == ForgeCapabilities.ITEM_HANDLER) {
            return itemHandler.cast();
        }
        return super.getCapability(cap, side);
    }
}
