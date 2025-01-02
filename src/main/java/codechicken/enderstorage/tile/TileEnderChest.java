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
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.items.wrapper.InvWrapper;
import org.jetbrains.annotations.Nullable;

import static net.minecraft.sounds.SoundEvents.*;

public class TileEnderChest extends TileFrequencyOwner {

    public double a_lidAngle;
    public double b_lidAngle;
    public int c_numOpen;
    public int rotation;

    private @Nullable IItemHandler itemHandler;

    public TileEnderChest(BlockPos pos, BlockState state) {
        super(EnderStorageModContent.ENDER_CHEST_TILE.get(), pos, state);
    }

    @Override
    public void tick() {
        super.tick();

        assert level != null;
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
        assert level != null;
        return EnderStorageManager.instance(level.isClientSide).getStorage(frequency, EnderItemStorage.TYPE);
    }

    @Override
    public void onFrequencySet() {
        invalidateCapabilities();
        itemHandler = null;
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
    public void onPlaced(@Nullable LivingEntity entity) {
        assert level != null;
        rotation = entity != null ? (int) Math.floor(entity.getYRot() * 4 / 360 + 2.5D) & 3 : 0;
        onFrequencySet();
        if (!level.isClientSide) {
            sendUpdatePacket();
        }
    }

    @Override
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.saveAdditional(tag, registries);
        tag.putByte("rot", (byte) rotation);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
        super.loadAdditional(tag, registries);
        rotation = tag.getByte("rot") & 3;
    }

    @Override
    public boolean activate(Player player, int subHit, InteractionHand hand) {
        getStorage().openContainer((ServerPlayer) player, Component.translatable(getBlockState().getBlock().getDescriptionId()));
        return true;
    }

    @Override
    public boolean rotate() {
        assert level != null;
        if (!level.isClientSide) {
            rotation = (rotation + 1) % 4;
            sendUpdatePacket();
        }
        return true;
    }

    @Override
    public int comparatorOutput() {
        return ItemHandlerHelper.calcRedstoneFromInventory(getItemHandler());
    }

    public IItemHandler getItemHandler() {
        if (itemHandler == null) {
            itemHandler = new InvWrapper(getStorage());
        }
        return itemHandler;
    }
}
