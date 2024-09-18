package codechicken.enderstorage.network;

import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.manager.EnderStorageManager;
import codechicken.enderstorage.storage.EnderLiquidStorage;
import codechicken.lib.math.MathHelper;
import codechicken.lib.packet.PacketCustom;
import codechicken.lib.util.ClientUtils;
import codechicken.lib.util.ServerUtils;
import com.google.common.collect.Sets;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.fluids.FluidStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.UUID;

public class TankSynchroniser {

    public static abstract class TankState {

        public Frequency frequency = new Frequency();
        public FluidStack c_liquid = new FluidStack(Fluids.WATER, 0);
        public FluidStack s_liquid = new FluidStack(Fluids.WATER, 0);
        public FluidStack f_liquid = new FluidStack(Fluids.WATER, 0);

        public void setFrequency(Frequency frequency) {
            this.frequency = frequency;
        }

        public void update(boolean client) {
            FluidStack b_liquid;
            FluidStack a_liquid;
            if (client) {
                b_liquid = c_liquid.copy();

                if (s_liquid.isFluidEqual(c_liquid) || c_liquid.isEmpty()) {
                    int change = MathHelper.approachExpI(c_liquid.getAmount(), s_liquid.getAmount(), 0.1);
                    if (c_liquid.isEmpty()) {
                        c_liquid = new FluidStack(s_liquid, change);
                    } else {
                        c_liquid.setAmount(change);
                    }
                } else if (c_liquid.getAmount() > 100) {
                    c_liquid.setAmount(MathHelper.retreatExpI(c_liquid.getAmount(), 0, f_liquid.getAmount(), 0.1, 1000));
                }

                a_liquid = c_liquid;
            } else {
                s_liquid = getStorage(false).getFluid();
                b_liquid = s_liquid.copy();
                if (!s_liquid.isFluidEqual(c_liquid)) {
                    sendSyncPacket();
                    c_liquid = s_liquid.copy();
                } else if (Math.abs(c_liquid.getAmount() - s_liquid.getAmount()) > 250 || (s_liquid.getAmount() == 0 && c_liquid.getAmount() > 0)) {// Diff grater than 250 Or server no longer has liquid and client does.
                    sendSyncPacket();
                    c_liquid = s_liquid.copy();
                }

                a_liquid = s_liquid;
            }
            if ((b_liquid.getAmount() == 0) != (a_liquid.getAmount() == 0) || !b_liquid.isFluidEqual(a_liquid)) {
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

        //SERVER SIDE ONLY!
        public EnderLiquidStorage getStorage(boolean client) {
            return EnderStorageManager.instance(client).getStorage(frequency, EnderLiquidStorage.TYPE);
        }
    }

    public static class PlayerItemTankState extends TankState {

        private @Nullable ServerPlayer player;
        private boolean tracking;

        public PlayerItemTankState(ServerPlayer player, EnderLiquidStorage storage) {
            this.player = player;
            setFrequency(storage.freq);
            tracking = true;
        }

        public PlayerItemTankState() {
        }

        @Override
        public void sendSyncPacket() {
            if (!tracking) {
                return;
            }

            assert player != null;
            PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.C_TANK_SYNC);
            getStorage(false).freq.writeToPacket(packet);
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

        private final boolean client;
        private final Map<String, PlayerItemTankState> tankStates = new HashMap<>();
        //client
        private HashSet<Frequency> a_visible = new HashSet<>();
        private HashSet<Frequency> b_visible = new HashSet<>();
        //server
        private @Nullable ServerPlayer player;

        public PlayerItemTankCache(ServerPlayer player) {
            this.player = player;
            client = false;
        }

        public PlayerItemTankCache() {
            client = true;
        }

        public void track(Frequency freq, boolean t) {
            String key = freq.toString();
            PlayerItemTankState state = tankStates.get(key);
            if (state == null) {
                if (!t) {
                    return;
                }
                assert player != null;
                tankStates.put(key, state = new PlayerItemTankState(player, EnderStorageManager.instance(false).getStorage(freq, EnderLiquidStorage.TYPE)));
            }
            state.setTracking(t);
        }

        public void sync(Frequency freq, FluidStack liquid) {
            String key = freq.toString();
            PlayerItemTankState state = tankStates.computeIfAbsent(key, k -> new PlayerItemTankState());
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
                    PacketCustom packet = new PacketCustom(EnderStorageNetwork.NET_CHANNEL, EnderStorageNetwork.S_VISIBILITY);

                    packet.writeShort(new_visible.size());
                    new_visible.forEach(freq -> freq.writeToPacket(packet));

                    packet.writeShort(old_visible.size());
                    old_visible.forEach(freq -> freq.writeToPacket(packet));

                    packet.sendToServer();
                }

                HashSet<Frequency> temp = b_visible;
                temp.clear();
                b_visible = a_visible;
                a_visible = temp;
            }
        }

        public FluidStack getLiquid(Frequency freq) {
            String key = freq.toString();
            a_visible.add(freq);
            PlayerItemTankState state = tankStates.get(key);
            return state == null ? FluidStack.EMPTY : state.c_liquid;
        }

        public void handleVisiblityPacket(PacketCustom packet) {
            int k = packet.readUShort();
            for (int i = 0; i < k; i++) {
                track(Frequency.readFromPacket(packet), true);
            }
            k = packet.readUShort();
            for (int i = 0; i < k; i++) {
                track(Frequency.readFromPacket(packet), false);
            }
        }
    }

    private static Map<UUID, PlayerItemTankCache> playerItemTankStates = new HashMap<>();
    private static @Nullable PlayerItemTankCache clientState;

    public static void syncClient(Frequency freq, FluidStack liquid) {
        if (clientState != null) {
            clientState.sync(freq, liquid);
        }
    }

    public static FluidStack getClientLiquid(Frequency freq) {
        if (clientState != null) {
            return clientState.getLiquid(freq);
        }
        return FluidStack.EMPTY;
    }

    public static void handleVisiblityPacket(ServerPlayer player, PacketCustom packet) {
        playerItemTankStates.get(player.getUUID()).handleVisiblityPacket(packet);
    }

    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        playerItemTankStates.put(event.getEntity().getUUID(), new PlayerItemTankCache((ServerPlayer) event.getEntity()));
    }

    @SubscribeEvent
    public void onPlayerLogout(PlayerEvent.PlayerLoggedOutEvent event) {
        playerItemTankStates.remove(event.getEntity().getUUID());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
        playerItemTankStates.put(event.getEntity().getUUID(), new PlayerItemTankCache((ServerPlayer) event.getEntity()));
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            for (Map.Entry<UUID, PlayerItemTankCache> entry : playerItemTankStates.entrySet()) {
                entry.getValue().update();
            }
        }
    }

    @SubscribeEvent
    public void tickEnd(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (ClientUtils.inWorld() && clientState != null) {
                clientState.update();
            }
        }
    }

    @SubscribeEvent
    public void onWorldUnload(LevelEvent.Unload event) {
        if (event.getLevel() instanceof ServerLevel serverLevel && !serverLevel.getServer().isRunning()) {
            playerItemTankStates.clear();
        }
    }

    @SubscribeEvent
    public void onWorldLoad(LevelEvent.Load event) {
        if (event.getLevel().isClientSide()) {
            clientState = new PlayerItemTankCache();
        }
    }
}
