package codechicken.enderstorage.api;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import codechicken.lib.config.ConfigFile;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.event.world.WorldEvent.Save;

public class EnderStorageManager
{
    public static class EnderStorageSaveHandler
    {
        @SubscribeEvent
        public void onWorldLoad(Load event) {
            if (event.world.isRemote)
                reloadManager(true);
        }

        @SubscribeEvent
        public void onWorldSave(Save event) {
            if (!event.world.isRemote && instance(false) != null)
                instance(false).save(false);
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerLoggedInEvent event) {
            instance(false).sendClientInfo(event.player);
        }

        @SubscribeEvent
        public void onPlayerChangedDimension(PlayerLoggedOutEvent event) {
            instance(false).sendClientInfo(event.player);
        }
    }

    private static EnderStorageManager serverManager;
    private static EnderStorageManager clientManager;
    private static ConfigFile config;
    private static HashMap<String, EnderStoragePlugin> plugins = new HashMap<String, EnderStoragePlugin>();

    private Map<String, AbstractEnderStorage> storageMap;
    private Map<String, List<AbstractEnderStorage>> storageList;
    public final boolean client;

    private File saveDir;
    private File[] saveFiles;
    private int saveTo;
    private List<AbstractEnderStorage> dirtyStorage;
    private NBTTagCompound saveTag;

    public EnderStorageManager(boolean client) {
        this.client = client;

        storageMap = Collections.synchronizedMap(new HashMap<String, AbstractEnderStorage>());
        storageList = Collections.synchronizedMap(new HashMap<String, List<AbstractEnderStorage>>());
        dirtyStorage = Collections.synchronizedList(new LinkedList<AbstractEnderStorage>());

        for (String key : plugins.keySet())
            storageList.put(key, new ArrayList<AbstractEnderStorage>());

        if (!client)
            load();
    }

    private void sendClientInfo(EntityPlayer player) {
        for (Entry<String, EnderStoragePlugin> plugin : plugins.entrySet())
            plugin.getValue().sendClientInfo(player, storageList.get(plugin.getKey()));
    }

    private void load() {
        saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "EnderStorage");
        try {
            if (!saveDir.exists())
                saveDir.mkdirs();
            saveFiles = new File[]{new File(saveDir, "data1.dat"), new File(saveDir, "data2.dat"), new File(saveDir, "lock.dat")};
            if (saveFiles[2].exists() && saveFiles[2].length() > 0) {
                FileInputStream fin = new FileInputStream(saveFiles[2]);
                saveTo = fin.read() ^ 1;
                fin.close();

                if (saveFiles[saveTo ^ 1].exists()) {
                    FileInputStream in = new FileInputStream(saveFiles[saveTo ^ 1]);
                    saveTag = CompressedStreamTools.readCompressed(in);
                    in.close();
                } else {
                    saveTag = new NBTTagCompound();
                }
            } else
                saveTag = new NBTTagCompound();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void save(boolean force) {
        if (!dirtyStorage.isEmpty() || force) {
            for (AbstractEnderStorage inv : dirtyStorage) {
                saveTag.setTag(inv.freq + "|" + inv.owner + "|" + inv.type(), inv.saveToTag());
                inv.setClean();
            }

            dirtyStorage.clear();

            try {
                File saveFile = saveFiles[saveTo];
                if (!saveFile.exists())
                    saveFile.createNewFile();
                DataOutputStream dout = new DataOutputStream(new FileOutputStream(saveFile));
                CompressedStreamTools.writeCompressed(saveTag, dout);
                dout.close();
                FileOutputStream fout = new FileOutputStream(saveFiles[2]);
                fout.write(saveTo);
                fout.close();
                saveTo ^= 1;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void reloadManager(boolean client) {
        EnderStorageManager newManager = new EnderStorageManager(client);
        if (client)
            clientManager = newManager;
        else
            serverManager = newManager;
    }

    public File getSaveDir() {
        return saveDir;
    }

    public static EnderStorageManager instance(boolean client) {
        return client ? clientManager : serverManager;
    }

    public AbstractEnderStorage getStorage(String owner, int freq, String type) {
        if (owner == null)
            owner = "global";
        String key = freq + "|" + owner + "|" + type;
        AbstractEnderStorage storage = storageMap.get(key);
        if (storage == null) {
            storage = plugins.get(type).createEnderStorage(this, owner, freq);
            if (!client && saveTag.hasKey(key))
                storage.loadFromTag(saveTag.getCompoundTag(key));
            storageMap.put(key, storage);
            storageList.get(type).add(storage);
        }
        return storage;
    }

    public static int getFreqFromColours(int colour1, int colour2, int colour3) {
        return ((colour1 & 0xF) << 8) + ((colour2 & 0xF) << 4) + (colour3 & 0xF);
    }

    public static int getFreqFromColours(int[] colours) {
        return ((colours[0] & 0xF) << 8) + ((colours[1] & 0xF) << 4) + (colours[2] & 0xF);
    }

    public static int getColourFromFreq(int freq, int colour) {
        switch (colour) {
            case 0:
                return freq >> 8 & 0xF;
            case 1:
                return freq >> 4 & 0xF;
            case 2:
                return freq & 0xF;
        }
        return 0;
    }

    public static int[] getColoursFromFreq(int freq) {
        int[] ai = new int[3];
        ai[0] = (freq >> 8) & 0xF;
        ai[1] = (freq >> 4) & 0xF;
        ai[2] = freq & 0xF;

        return ai;
    }

    public static void loadConfig(ConfigFile config2) {
        config = config2;
        for (Entry<String, EnderStoragePlugin> plugin : plugins.entrySet())
            plugin.getValue().loadConfig(config.getTag(plugin.getKey()));
    }

    public static void registerPlugin(EnderStoragePlugin plugin) {
        plugins.put(plugin.identifer(), plugin);
        if (config != null)
            plugin.loadConfig(config.getTag(plugin.identifer()));

        if (serverManager != null)
            serverManager.storageList.put(plugin.identifer(), new ArrayList<AbstractEnderStorage>());
        if (clientManager != null)
            clientManager.storageList.put(plugin.identifer(), new ArrayList<AbstractEnderStorage>());
    }

    public void requestSave(AbstractEnderStorage storage) {
        dirtyStorage.add(storage);
    }
}
