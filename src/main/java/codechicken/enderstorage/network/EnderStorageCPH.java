package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.packet.ICustomPacketHandler.IClientPacketHandler;
import codechicken.lib.packet.PacketCustom;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;

import static codechicken.enderstorage.network.EnderStorageNetwork.*;

public class EnderStorageCPH implements IClientPacketHandler {

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, ClientPacketListener handler) {
        switch (packet.getType()) {
            case C_TILE_UPDATE:
                if (mc.level.getBlockEntity(packet.readPos()) instanceof TileFrequencyOwner tile) {
                    tile.readFromPacket(packet);
                }
                break;
            case C_SET_CLIENT_OPEN:
                EnderStorageManager.instance(true).getStorage(Frequency.readFromPacket(packet), EnderItemStorage.TYPE).setClientOpen(packet.readBoolean() ? 1 : 0);
                break;
            case C_TANK_SYNC:
                TankSynchroniser.syncClient(Frequency.readFromPacket(packet), packet.readFluidStack());
                break;
            case C_LIQUID_SYNC:
                if (mc.level.getBlockEntity(packet.readPos()) instanceof TileEnderTank tile) {
                    tile.liquid_state.sync(packet.readFluidStack());
                }
                break;
            case C_PRESSURE_SYNC:
                if (mc.level.getBlockEntity(packet.readPos()) instanceof TileEnderTank tile) {
                    tile.pressure_state.a_pressure = packet.readBoolean();
                }
                break;
        }
    }
}
