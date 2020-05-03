package codechicken.enderstorage.network;

import codechicken.lib.packet.PacketCustomChannelBuilder;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.event.EventNetworkChannel;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageNetwork {

    public static final ResourceLocation NET_CHANNEL = new ResourceLocation("enderstorage:network");
    public static EventNetworkChannel netChannel;

    public static void init() {
        netChannel = PacketCustomChannelBuilder.named(NET_CHANNEL)//
                .networkProtocolVersion(() -> "1")//
                .clientAcceptedVersions(e -> true)//
                .serverAcceptedVersions(e -> true)//
                .assignClientHandler(() -> EnderStorageCPH::new)//
                .assignServerHandler(() -> EnderStorageSPH::new)//
                .build();
    }

}
