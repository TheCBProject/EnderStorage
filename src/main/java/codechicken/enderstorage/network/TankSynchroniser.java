package codechicken.enderstorage.network;

import codechicken.core.ClientUtils;
import codechicken.core.ServerUtils;
import codechicken.core.fluid.FluidUtils;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.enderstorage.util.LogHelper;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import com.google.common.collect.Sets;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class TankSynchroniser {
    public static abstract class TankState {
        public EnderLiquidStorage storage;
        public FluidStack c_liquid = FluidUtils.emptyFluid();
        public FluidStack s_liquid = FluidUtils.emptyFluid();
        public FluidStack f_liquid = FluidUtils.emptyFluid();

        public void reloadStorage(EnderLiquidStorage storage) {
            this.storage = storage;
        }

        public void update(boolean client) {
            FluidStack b_liquid;
            FluidStack a_liquid;
            if (client) {
                b_liquid = c_liquid.copy();

                if (s_liquid.isFluidEqual(c_liquid)) {
                    c_liquid.amount = MathHelper.approachExpI(c_liquid.amount, s_liquid.amount, 0.1);
                } else if (c_liquid.amount > 100) {
                    c_liquid.amount = MathHelper.retreatExpI(c_liquid.amount, 0, f_liquid.amount, 0.1, 1000);
                } else {
                    c_liquid = FluidUtils.copy(s_liquid, 0);
                }

                a_liquid = c_liquid;
            } else {
                s_liquid = storage.getFluid();
                b_liquid = s_liquid.copy();
                if (!s_liquid.isFluidEqual(c_liquid)) {
                    sendSyncPacket();
                    c_liquid = s_liquid;
                } else if (Math.abs(c_liquid.amount - s_liquid.amount) > 250 || (s_liquid.amount == 0 && c_liquid.amount > 0)) {
                    sendSyncPacket();
                    c_liquid = s_liquid;
                }

                a_liquid = s_liquid;
            }
            if ((b_liquid.amount == 0) != (a_liquid.amount == 0) || !b_liquid.isFluidEqual(a_liquid)) {
                onLiquidChanged();
            }
        }

        public void onLiquidChanged() {
        }

        public abstract void sendSyncPacket();

        public void sync(FluidStack liquid) {
            s_liquid = liquid;
            if (!s_liquid.isFluidEqual(c_liquid)) {
                f_liquid = c_liquid.copy();
            }
        }
    }

    public static class PlayerItemTankState extends TankState {
        private EntityPlayerMP player;
        private boolean tracking;

        public PlayerItemTankState(EntityPlayerMP player, EnderLiquidStorage storage) {
            this.player = player;
            reloadStorage(storage);
            tracking = true;
        }

        public PlayerItemTankState() {
        }

        @Override
        public void sendSyncPacket() {
            if (!tracking) {
                return;
            }

            PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 4);
            packet.writeNBTTagCompound(storage.freq.toNBT());
            //packet.writeString(storage.owner);
            packet.writeFluidStack(s_liquid);
            packet.sendToPlayer(player);
        }

        public void setTracking(boolean t) {
            tracking = t;
        }

        @Override
        public void update(boolean client) {
            if (tracking || client) {
                super.update(client);
            }
        }
    }

    public static class PlayerItemTankCache {
        private boolean client;
        private HashMap<String, PlayerItemTankState> tankStates = new HashMap<String, PlayerItemTankState>();
        //client
        private HashSet<Frequency> b_visible;
        private HashSet<Frequency> a_visible;
        //server
        private EntityPlayerMP player;

        public PlayerItemTankCache(EntityPlayerMP player) {
            this.player = player;
            client = false;
        }

        public PlayerItemTankCache() {
            client = true;
            a_visible = new HashSet<Frequency>();
            b_visible = new HashSet<Frequency>();
        }

        public void track(Frequency freq, boolean t) {
            String key = key(freq);
            PlayerItemTankState state = tankStates.get(key);
            if (state == null) {
                if (!t) {
                    return;
                }
                tankStates.put(key, state = new PlayerItemTankState(player, (EnderLiquidStorage) EnderStorageManager.instance(false).getStorage(freq, "liquid")));
            }
            state.setTracking(t);
        }

        public void sync(Frequency freq, FluidStack liquid) {
            String key = key(freq);
            PlayerItemTankState state = tankStates.get(key);
            if (state == null) {
                tankStates.put(key, state = new PlayerItemTankState());
            }
            state.sync(liquid);
        }

        public void update() {
            for (Map.Entry<String, PlayerItemTankState> entry : tankStates.entrySet()) {
                entry.getValue().update(client);
            }

            if (client) {
                Sets.SetView<Frequency> new_visible = Sets.difference(a_visible, b_visible);
                Sets.SetView<Frequency> old_visible = Sets.difference(b_visible, a_visible);

                if (!new_visible.isEmpty() || !old_visible.isEmpty()) {
                    PacketCustom packet = new PacketCustom(EnderStorageCPH.channel, 1);
                    packet.writeShort(new_visible.size());
                    for (Frequency frequency : new_visible) {
                        packet.writeNBTTagCompound(frequency.toNBT());
                    }
                    packet.writeShort(old_visible.size());
                    for (Frequency frequency : old_visible) {
                        packet.writeNBTTagCompound(frequency.toNBT());
                    }
                    packet.sendToServer();
                }

                HashSet<Frequency> temp = b_visible;
                temp.clear();
                b_visible = a_visible;
                a_visible = temp;
            }
        }

        public FluidStack getLiquid(Frequency freq) {
            String key = key(freq);
            a_visible.add(freq);
            PlayerItemTankState state = tankStates.get(key);
            return state == null ? FluidUtils.emptyFluid() : state.c_liquid;
        }

        public void handleVisiblityPacket(PacketCustom packet) {
            int k = packet.readUShort();
            for (int i = 0; i < k; i++) {
                track(Frequency.fromNBT(packet.readNBTTagCompound()), true);
            }
            k = packet.readUShort();
            for (int i = 0; i < k; i++) {
                track(Frequency.fromNBT(packet.readNBTTagCompound()), false);
            }
        }
    }

    public static String key(Frequency freq) {
        return freq.toString();
    }

    public static int splitKeyF(String s) {
        return Integer.parseInt(s.substring(0, s.indexOf('|')));
    }

    public static String splitKeyS(String s) {
        return s.substring(s.indexOf('|') + 1, s.length());
    }

    private static HashMap<String, PlayerItemTankCache> playerItemTankStates;
    private static PlayerItemTankCache clientState;

    public static void syncClient(Frequency freq, FluidStack liquid) {
        clientState.sync(freq, liquid);
    }

    public static FluidStack getClientLiquid(Frequency freq) {
        return clientState.getLiquid(freq);
    }

    public static void handleVisiblityPacket(EntityPlayerMP player, PacketCustom packet) {
        playerItemTankStates.get(player.getName()).handleVisiblityPacket(packet);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        playerItemTankStates.put(event.player.getName(), new PlayerItemTankCache((EntityPlayerMP) event.player));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        if (playerItemTankStates != null) //sometimes world unloads before players logout
        {
            playerItemTankStates.remove(event.player.getName());
        }
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        playerItemTankStates.put(event.player.getName(), new PlayerItemTankCache((EntityPlayerMP) event.player));
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && playerItemTankStates != null) {
            for (Map.Entry<String, PlayerItemTankCache> entry : playerItemTankStates.entrySet()) {
                entry.getValue().update();
            }
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientUtils.inWorld()) {
                clientState.update();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote && !ServerUtils.mc().isServerRunning()) {
            playerItemTankStates = null;
        }
    }

    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote) {
            clientState = new PlayerItemTankCache();
        } else if (playerItemTankStates == null) {
            playerItemTankStates = new HashMap<String, PlayerItemTankCache>();
        }
    }
}
