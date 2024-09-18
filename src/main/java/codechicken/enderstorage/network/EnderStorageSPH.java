package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.jetbrains.annotations.Nullable;

import static codechicken.enderstorage.network.EnderStorageNetwork.S_VISIBILITY;

public class EnderStorageSPH implements IServerPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayer sender) {
        switch (packet.getType()) {
            case S_VISIBILITY:
                TankSynchroniser.handleVisiblityPacket(sender, packet);
                break;
        }
    }

    public static void sendOpenUpdateTo(@Nullable ServerPlayer player, Frequency freq, boolean open) {
        PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_SET_CLIENT_OPEN);
        freq.writeToPacket(packet);
        packet.writeBoolean(open);
        packet.sendToPlayer(player);
    }
}
