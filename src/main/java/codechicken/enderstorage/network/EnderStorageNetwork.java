package codechicken.enderstorage.network;

import codechicken.lib.packet.PacketCustomChannelBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.event.EventNetworkChannel;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageNetwork {

    public static final ResourceLocation NET_CHANNEL = new ResourceLocation("enderstorage:network");
    public static EventNetworkChannel netChannel;

    //Client Handled.
    public static final int C_TILE_UPDATE = 1;
    public static final int C_SET_CLIENT_OPEN = 2;
    public static final int C_TANK_SYNC = 3;
    public static final int C_LIQUID_SYNC = 4;
    public static final int C_PRESSURE_SYNC = 5;

    //Server Handled.
    public static final int S_VISIBILITY = 1;

    public static void init() {
        netChannel = PacketCustomChannelBuilder.named(NET_CHANNEL)
                .assignClientHandler(() -> EnderStorageCPH::new)
                .assignServerHandler(() -> EnderStorageSPH::new)
                .build();
    }

}
