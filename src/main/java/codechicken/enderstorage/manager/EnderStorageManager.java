package codechicken.enderstorage.manager;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.lib.config.ConfigFile;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class EnderStorageManager {

    public static class EnderStorageSaveHandler {

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {

            if (event.getWorld().isRemote) {
                reloadManager(true);
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event) {

            if (!event.getWorld().isRemote && instance(false) != null) {
                instance(false).save(false);
            }
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {

            instance(false).sendClientInfo(event.player);
        }

        @SubscribeEvent
        public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {

            instance(false).sendClientInfo(event.player);
        }
    }

    private static EnderStorageManager serverManager;
    private static EnderStorageManager clientManager;
    private static ConfigFile config;
    private static HashMap<String, EnderStoragePlugin> plugins = new HashMap<>();

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

        for (String key : plugins.keySet()) {
            storageList.put(key, new ArrayList<>());
        }

        if (!client) {
            load();
        }
    }

    private void sendClientInfo(EntityPlayer player) {

        for (Map.Entry<String, EnderStoragePlugin> plugin : plugins.entrySet()) {
            plugin.getValue().sendClientInfo(player, storageList.get(plugin.getKey()));
        }
    }

    private void load() {

        saveDir = new File(DimensionManager.getCurrentSaveRootDirectory(), "EnderStorage");
        try {
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            saveFiles = new File[] { new File(saveDir, "data1.dat"), new File(saveDir, "data2.dat"), new File(saveDir, "lock.dat") };
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
            } else {
                saveTag = new NBTTagCompound();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void save(boolean force) {

        if (!dirtyStorage.isEmpty() || force) {
            for (AbstractEnderStorage inv : dirtyStorage) {
                saveTag.setTag(inv.freq + ",type=" + inv.type(), inv.saveToTag());
                inv.setClean();
            }

            dirtyStorage.clear();

            try {
                File saveFile = saveFiles[saveTo];
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
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
        if (client) {
            clientManager = newManager;
        } else {
            serverManager = newManager;
        }
    }

    public File getSaveDir() {

        return saveDir;
    }

    public static EnderStorageManager instance(boolean client) {

        EnderStorageManager manager = client ? clientManager : serverManager;
        if (manager == null) {
            reloadManager(client);
            manager = client ? clientManager : serverManager;
        }
        return manager;
    }

    public AbstractEnderStorage getStorage(Frequency freq, String type) {

        String key = freq + ",type=" + type;
        AbstractEnderStorage storage = storageMap.get(key);
        if (storage == null) {
            storage = plugins.get(type).createEnderStorage(this, freq);
            if (!client && saveTag.hasKey(key)) {
                storage.loadFromTag(saveTag.getCompoundTag(key));
            }
            storageMap.put(key, storage);
            storageList.get(type).add(storage);
        }
        return storage;
    }

    @Deprecated
    public static int getFreqFromColours(int colour1, int colour2, int colour3) {

        return ((colour1 & 0xF) << 8) + ((colour2 & 0xF) << 4) + (colour3 & 0xF);
    }

    @Deprecated
    public static int getFreqFromColours(int[] colours) {

        return ((colours[0] & 0xF) << 8) + ((colours[1] & 0xF) << 4) + (colours[2] & 0xF);
    }

    @Deprecated
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

    @Deprecated
    public static int[] getColoursFromFreq(int freq) {

        int[] ai = new int[3];
        ai[0] = (freq >> 8) & 0xF;
        ai[1] = (freq >> 4) & 0xF;
        ai[2] = freq & 0xF;

        return ai;
    }

    @Deprecated
    public static String getOwner(ItemStack stack) {

        String owner = "";
        if (stack.hasTagCompound()) {
            owner = stack.getTagCompound().getString("owner");
        }

        return owner.isEmpty() ? "global" : owner;
    }

    @Deprecated
    public static boolean isOwned(ItemStack stack) {

        return !getOwner(stack).equals("global");
    }

    public static void loadConfig(ConfigFile config2) {

        config = config2;
        for (Map.Entry<String, EnderStoragePlugin> plugin : plugins.entrySet()) {
            plugin.getValue().loadConfig(config.getTag(plugin.getKey()));
        }
    }

    public static void registerPlugin(EnderStoragePlugin plugin) {

        plugins.put(plugin.identifier(), plugin);
        if (config != null) {
            plugin.loadConfig(config.getTag(plugin.identifier()));
        }

        if (serverManager != null) {
            serverManager.storageList.put(plugin.identifier(), new ArrayList<>());
        }
        if (clientManager != null) {
            clientManager.storageList.put(plugin.identifier(), new ArrayList<>());
        }
    }

    public static EnderStoragePlugin getPlugin(String identifier) {

        return plugins.get(identifier);
    }

    public static Map<String, EnderStoragePlugin> getPlugins() {

        return ImmutableMap.copyOf(plugins);
    }

    public List<String> getValidKeys(String identifer) {

        List<String> list = new ArrayList<>();
        for (String key : saveTag.getKeySet()) {
            if (key.endsWith(",type=" + identifer)) {
                list.add(key.replace(",type=" + identifer, ""));
            }
        }
        return list;
    }

    public void requestSave(AbstractEnderStorage storage) {

        dirtyStorage.add(storage);
    }
}
