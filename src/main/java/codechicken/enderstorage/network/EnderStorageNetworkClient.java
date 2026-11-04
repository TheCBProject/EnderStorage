package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.ClientTankSynchronizer;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;

/**
 * Created by covers1624 on 11/4/26.
 */
public class EnderStorageNetworkClient extends EnderStorageNetwork {

    public static void initClient() {
        CHANNEL.assignClientHandler(TILE_UPDATE, EnderStorageNetworkClient::handleTileUpdate);
        CHANNEL.assignClientHandler(CLIENT_OPEN, EnderStorageNetworkClient::handleClientOpen);
        CHANNEL.assignClientHandler(TANK_SYNC, EnderStorageNetworkClient::handleTankSync);
        CHANNEL.assignClientHandler(LIQUID_SYNC, EnderStorageNetworkClient::handleLiquidSync);
        CHANNEL.assignClientHandler(PRESSURE_SYNC, EnderStorageNetworkClient::handlePressureSync);
    }

    private static void handleTileUpdate(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        if (ctx.player().level().getBlockEntity(packet.readBlockPos()) instanceof TileFrequencyOwner tile) {
            tile.readFromPacket(packet);
        }
    }

    private static void handleClientOpen(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        EnderStorageManager.instance(true)
                .getStorage(packet.cc$readWithRegistryCodec(Frequency.STREAM_CODEC), EnderItemStorage.TYPE)
                .setClientOpen(packet.readBoolean() ? 1 : 0);
    }

    private static void handleTankSync(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        ClientTankSynchronizer.handleClientSync(
                packet.cc$readWithRegistryCodec(Frequency.STREAM_CODEC),
                packet.cc$readWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC)
        );
    }

    private static void handleLiquidSync(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        if (!(ctx.player().level().getBlockEntity(packet.readBlockPos()) instanceof TileEnderTank tile)) return;
        if (!(tile.getTankState() instanceof TileEnderTank.ClientTankState tankState)) return;

        tankState.sync(packet.cc$readWithRegistryCodec(FluidStack.OPTIONAL_STREAM_CODEC));
    }

    private static void handlePressureSync(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        if (ctx.player().level().getBlockEntity(packet.readBlockPos()) instanceof TileEnderTank tile) {
            tile.pressure_state.a_pressure = packet.readBoolean();
        }
    }
}
