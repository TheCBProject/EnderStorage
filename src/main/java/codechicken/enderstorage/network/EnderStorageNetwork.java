package codechicken.enderstorage.network;

import codechicken.enderstorage.EnderStorage;
import codechicken.lib.packet.PacketCustomChannel;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;

import static codechicken.enderstorage.EnderStorage.MOD_ID;

/**
 * Created by covers1624 on 28/10/19.
 */
public class EnderStorageNetwork {

    public static final ResourceLocation NET_CHANNEL = ResourceLocation.fromNamespaceAndPath(MOD_ID, "network");
    public static final PacketCustomChannel channel = new PacketCustomChannel(NET_CHANNEL)
            .versioned(EnderStorage.container().getModInfo().getVersion().toString())
            .client(() -> EnderStorageCPH::new)
            .server(() -> EnderStorageSPH::new);

    //Client Handled.
    public static final int C_TILE_UPDATE = 1;
    public static final int C_SET_CLIENT_OPEN = 2;
    public static final int C_TANK_SYNC = 3;
    public static final int C_LIQUID_SYNC = 4;
    public static final int C_PRESSURE_SYNC = 5;

    //Server Handled.
    public static final int S_VISIBILITY = 1;

    public static void init(IEventBus modBus) {
        channel.init(modBus);
    }

}
