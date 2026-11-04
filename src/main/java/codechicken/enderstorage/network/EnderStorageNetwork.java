package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.manager.ServerTankSynchronizer;
import codechicken.enderstorage.manager.ClientTankSynchronizer;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.packet.StreamNetworkChannel;
import codechicken.lib.packet.StreamNetworkChannel.ClientPacketHandle;
import codechicken.lib.packet.StreamNetworkChannel.ServerPacketHandle;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageNetwork {

    protected static final StreamNetworkChannel CHANNEL = new StreamNetworkChannel(MOD_ID);

    public static final ClientPacketHandle TILE_UPDATE = CHANNEL.playToClient("tile_update");
    public static final ClientPacketHandle CLIENT_OPEN = CHANNEL.playToClient("client_open");
    public static final ClientPacketHandle TANK_SYNC = CHANNEL.playToClient("tank_sync");
    public static final ClientPacketHandle LIQUID_SYNC = CHANNEL.playToClient("liquid_sync");
    public static final ClientPacketHandle PRESSURE_SYNC = CHANNEL.playToClient("pressure_sync");

    public static final ServerPacketHandle TANK_VISIBILITY = CHANNEL.playToServer("tank_visibility", EnderStorageNetwork::handleTankVisibility);

    public static void init(IEventBus modBus, ModContainer container) {
        CHANNEL.init(modBus, container);
    }


    public static void sendOpenUpdateTo(@Nullable ServerPlayer player, Frequency freq, boolean open) {
        // TODO, DON'T USE ServerLifecycleHooks HERE, PASS SOME CONTEXT IN, LIKE THE SERVER
        var packet = CLIENT_OPEN.toClient(ServerLifecycleHooks.getCurrentServer().registryAccess());
        packet.cc$writeWithRegistryCodec(Frequency.STREAM_CODEC, freq);
        packet.writeBoolean(open);
        packet.sendToPlayer(player);
    }


    private static void handleTankVisibility(RegistryFriendlyByteBuf packet, IPayloadContext ctx) {
        ServerTankSynchronizer.handleVisibilityPacket((ServerPlayer) ctx.player(), packet);
    }
}
