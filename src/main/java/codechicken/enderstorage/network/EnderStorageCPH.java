package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderItemStorage;
import codechicken.enderstorage.tile.TileEnderTank;
import codechicken.enderstorage.tile.TileFrequencyOwner;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.packet.PacketCustom.IClientPacketHandler;
import codechicken.lib.vec.BlockCoord;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;

public class EnderStorageCPH implements IClientPacketHandler {
    public static final String channel = "ES";

    @Override
    public void handlePacket(PacketCustom packet, Minecraft mc, INetHandlerPlayClient handler) {
        switch (packet.getType()) {
        case 1:
            handleTilePacket(mc.theWorld, packet, packet.readCoord());
            break;
        case 2:
            int windowID = packet.readUByte();
            ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(packet.readString(), Frequency.fromNBT(packet.readNBTTagCompound()), "item")).openClientGui(windowID, mc.thePlayer.inventory, packet.readString(), packet.readUByte());
            break;
        case 3:
            ((EnderItemStorage) EnderStorageManager.instance(true).getStorage(packet.readString(), Frequency.fromNBT(packet.readNBTTagCompound()), "item")).setClientOpen(packet.readBoolean() ? 1 : 0);
            break;
        case 4:
            TankSynchroniser.syncClient(Frequency.fromNBT(packet.readNBTTagCompound()), packet.readString(), packet.readFluidStack());
            break;
        case 5:
        case 6:
            handleTankTilePacket(mc.theWorld, packet.readCoord(), packet);
            break;
        }
    }

    private void handleTankTilePacket(WorldClient world, BlockCoord pos, PacketCustom packet) {
        TileEntity tile = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));
        if (tile instanceof TileEnderTank) {
            ((TileEnderTank) tile).sync(packet);
        }
    }

    private void handleTilePacket(WorldClient world, PacketCustom packet, BlockCoord pos) {
        TileEntity tile = world.getTileEntity(new BlockPos(pos.x, pos.y, pos.z));

        if (tile instanceof TileFrequencyOwner) {
            ((TileFrequencyOwner) tile).handleDescriptionPacket(packet);
        }
    }
}
