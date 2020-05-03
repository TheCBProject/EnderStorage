package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.lib.packet.ICustomPacketHandler.IServerPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.play.IServerPlayNetHandler;

public class EnderStorageSPH implements IServerPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, ServerPlayerEntity sender, IServerPlayNetHandler handler) {
        switch (packet.getType()) {
            case 1:
                TankSynchroniser.handleVisiblityPacket(sender, packet);
                break;
        }
    }

    public static void sendOpenUpdateTo(ServerPlayerEntity player, Frequency freq, boolean open) {
        PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, 3);
        freq.writeToPacket(packet);
        packet.writeBoolean(open);
        packet.sendToPlayer(player);
    }
}
