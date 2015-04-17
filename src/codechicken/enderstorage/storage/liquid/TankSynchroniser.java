package codechicken.enderstorage.storage.liquid;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;

import com.google.common.collect.Sets;
import com.google.common.collect.Sets.SetView;

import codechicken.core.ClientUtils;
import codechicken.core.ServerUtils;
import codechicken.lib.math.MathHelper;
import codechicken.core.fluid.FluidUtils;
import codechicken.lib.packet.PacketCustom;
import codechicken.enderstorage.api.EnderStorageManager;
import codechicken.enderstorage.internal.EnderStorageCPH;
import codechicken.enderstorage.internal.EnderStorageSPH;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import cpw.mods.fml.common.gameevent.TickEvent.ClientTickEvent;
import cpw.mods.fml.common.gameevent.TickEvent.Phase;
import cpw.mods.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Unload;
import net.minecraftforge.fluids.FluidStack;

public class TankSynchroniser
{
    public static abstract class TankState
    {
        public EnderLiquidStorage storage;
        public FluidStack c_liquid = FluidUtils.emptyFluid();
        public FluidStack s_liquid = FluidUtils.emptyFluid();
        public FluidStack f_liquid = FluidUtils.emptyFluid();
        
        public void reloadStorage(EnderLiquidStorage storage)
        {
            this.storage = storage;
        }

        public void update(boolean client)
        {
            FluidStack b_liquid;
            FluidStack a_liquid;
            if(client)
            {
                b_liquid = c_liquid.copy();
                
                if(s_liquid.isFluidEqual(c_liquid))
                    c_liquid.amount = MathHelper.approachExpI(c_liquid.amount, s_liquid.amount, 0.1);
                else if(c_liquid.amount > 100)
                    c_liquid.amount = MathHelper.retreatExpI(c_liquid.amount, 0, f_liquid.amount, 0.1, 1000);
                else
                    c_liquid = FluidUtils.copy(s_liquid, 0);
                
                a_liquid = c_liquid;
            }
            else
            {
                s_liquid = storage.getFluid();
                b_liquid = s_liquid.copy();
                if(!s_liquid.isFluidEqual(c_liquid))
                {
                    sendSyncPacket();
                    c_liquid = s_liquid;
                }
                else if(Math.abs(c_liquid.amount-s_liquid.amount) > 250 || (s_liquid.amount == 0 && c_liquid.amount > 0))
                {
                    sendSyncPacket();
                    c_liquid = s_liquid;
                }

                a_liquid = s_liquid;                
            }
            if((b_liquid.amount == 0) != (a_liquid.amount == 0) || !b_liquid.isFluidEqual(a_liquid))
                onLiquidChanged();
        }
        
        public void onLiquidChanged()
        {
        }

        public abstract void sendSyncPacket();

        public void sync(FluidStack liquid)
        {
            s_liquid = liquid;
            if(!s_liquid.isFluidEqual(c_liquid))
                f_liquid = c_liquid.copy();
        }
    }
    
    public static class PlayerItemTankState extends TankState
    {
        private EntityPlayerMP player;
        private boolean tracking;
        
        public PlayerItemTankState(EntityPlayerMP player, EnderLiquidStorage storage)
        {
            this.player = player;
            reloadStorage(storage);
            tracking = true;
        }
        
        public PlayerItemTankState()
        {
        }

        @Override
        public void sendSyncPacket()
        {
            if(!tracking)
                return;
            
            PacketCustom packet = new PacketCustom(EnderStorageSPH.channel, 4);
            packet.writeShort(storage.freq);
            packet.writeString(storage.owner);
            packet.writeFluidStack(s_liquid);
            packet.sendToPlayer(player);
        }
        
        public void setTracking(boolean t)
        {
            tracking = t;
        }
        
        @Override
        public void update(boolean client)
        {
            if(tracking || client)
                super.update(client);
        }
    }
    
    public static class PlayerItemTankCache
    {
        private boolean client;
        private HashMap<String, PlayerItemTankState> tankStates = new HashMap<String, PlayerItemTankState>();
        //client
        private HashSet<String> b_visible;
        private HashSet<String> a_visible;
        //server
        private EntityPlayerMP player;
        
        public PlayerItemTankCache(EntityPlayerMP player)
        {
            this.player = player;
            client = false;
        }
        
        public PlayerItemTankCache()
        {
            client = true;
            a_visible = new HashSet<String>();
            b_visible = new HashSet<String>();
        }

        public void track(int freq, String owner, boolean t)
        {
            String key = key(freq, owner);
            PlayerItemTankState state = tankStates.get(key);
            if(state == null)
            {
                if(!t)
                    return;
                tankStates.put(key, state = new PlayerItemTankState(player, 
                    (EnderLiquidStorage) EnderStorageManager.instance(false).getStorage(owner, freq, "liquid")));
            }
            state.setTracking(t);
        }
        
        public void sync(int freq, String owner, FluidStack liquid)
        {
            String key = key(freq, owner);
            PlayerItemTankState state = tankStates.get(key);
            if(state == null)
                tankStates.put(key, state = new PlayerItemTankState());
            state.sync(liquid);
        }
        
        public void update()
        {
            for(Entry<String, PlayerItemTankState> entry : tankStates.entrySet())
                entry.getValue().update(client);
            
            if(client)
            {
                SetView<String> new_visible = Sets.difference(a_visible, b_visible);
                SetView<String> old_visible = Sets.difference(b_visible, a_visible);
                
                if(!new_visible.isEmpty() || !old_visible.isEmpty())
                {
                    PacketCustom packet = new PacketCustom(EnderStorageCPH.channel, 1);
                    packet.writeShort(new_visible.size());
                    for(String s : new_visible)
                    {
                        packet.writeShort(splitKeyF(s));
                        packet.writeString(splitKeyS(s));
                    }
                    packet.writeShort(old_visible.size());
                    for(String s : old_visible)
                    {
                        packet.writeShort(splitKeyF(s));
                        packet.writeString(splitKeyS(s));
                    }
                    packet.sendToServer();
                }
                
                HashSet<String> temp = b_visible;
                temp.clear();
                b_visible = a_visible;
                a_visible = temp;
            }
        }

        public FluidStack getLiquid(int freq, String owner)
        {
            String key = key(freq, owner);
            a_visible.add(key);
            PlayerItemTankState state = tankStates.get(key);
            return state == null ? FluidUtils.emptyFluid() : state.c_liquid;
        }

        public void handleVisiblityPacket(PacketCustom packet)
        {
            int k = packet.readUShort();
            for(int i = 0; i < k; i++)
                track(packet.readUShort(), packet.readString(), true);
            k = packet.readUShort();
            for(int i = 0; i < k; i++)
                track(packet.readUShort(), packet.readString(), false);
        }
    }
    
    public static String key(int freq, String owner)
    {
        return freq+"|"+owner;
    }

    public static int splitKeyF(String s)
    {
        return Integer.parseInt(s.substring(0, s.indexOf('|')));
    }

    public static String splitKeyS(String s)
    {
        return s.substring(s.indexOf('|')+1, s.length());
    }
    
    private static HashMap<String, PlayerItemTankCache> playerItemTankStates;
    private static PlayerItemTankCache clientState;

    public static void syncClient(int freq, String owner, FluidStack liquid)
    {
        clientState.sync(freq, owner, liquid);
    }
    
    public static FluidStack getClientLiquid(int freq, String owner)
    {
        return clientState.getLiquid(freq, owner);
    }
    
    public static void handleVisiblityPacket(EntityPlayerMP player, PacketCustom packet)
    {
        playerItemTankStates.get(player.getCommandSenderName()).handleVisiblityPacket(packet);
    }
    
    @SubscribeEvent
    public void onPlayerLogin(PlayerLoggedInEvent event)
    {
        playerItemTankStates.put(event.player.getCommandSenderName(), new PlayerItemTankCache((EntityPlayerMP) event.player));
    }
    
    @SubscribeEvent
    public void onPlayerLogout(PlayerLoggedOutEvent event)
    {
        playerItemTankStates.remove(event.player.getCommandSenderName());
    }

    @SubscribeEvent
    public void onPlayerChangedDimension(PlayerChangedDimensionEvent event)
    {
        playerItemTankStates.put(event.player.getCommandSenderName(), new PlayerItemTankCache((EntityPlayerMP) event.player));
    }

    @SubscribeEvent
    public void tickEnd(ServerTickEvent event)
    {
        if(event.phase == Phase.END && playerItemTankStates != null)
            for(Entry<String, PlayerItemTankCache> entry : playerItemTankStates.entrySet())
                entry.getValue().update();
    }

    @SubscribeEvent
    public void tickEnd(ClientTickEvent event)
    {
        if(event.phase == Phase.END)
            if(ClientUtils.inWorld())
                clientState.update();
    }

    @SubscribeEvent
    public void onWorldUnload(Unload event)
    {
        if(!event.world.isRemote && !ServerUtils.mc().isServerRunning())
            playerItemTankStates = null;
    }
    
    @SubscribeEvent
    public void onWorldLoad(Load event)
    {
        if(event.world.isRemote)
            clientState = new PlayerItemTankCache();
        else if(playerItemTankStates == null)
            playerItemTankStates = new HashMap<String, PlayerItemTankCache>();
    }
}
