package codechicken.enderstorage.manager;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.lib.util.ServerUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class EnderStorageManager {

    public static class EnderStorageSaveHandler {

        @SubscribeEvent
        public void onWorldLoad(WorldEvent.Load event) {
            if (event.getWorld().isClientSide()) {
                reloadManager(true);
            }
        }

        @SubscribeEvent
        public void onWorldSave(WorldEvent.Save event) {
            if (!event.getWorld().isClientSide() && instance(false) != null) {
                instance(false).save(false);
            }
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            instance(false).sendClientInfo((ServerPlayerEntity) event.getPlayer());
        }

        @SubscribeEvent
        public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            instance(false).sendClientInfo((ServerPlayerEntity) event.getPlayer());
        }
    }

    public static class StorageType<T extends AbstractEnderStorage> {

        public final String name;

        public StorageType(String name) {
            this.name = name;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (super.equals(obj)) {
                return true;
            }
            if (!(obj instanceof StorageType)) {
                return false;
            }
            StorageType<?> other = (StorageType<?>) obj;
            return other.name.equals(name);
        }
    }

    private static EnderStorageManager serverManager;
    private static EnderStorageManager clientManager;
    private static Map<StorageType<?>, EnderStoragePlugin<?>> plugins = new HashMap<>();

    private Map<String, AbstractEnderStorage> storageMap;
    private Map<StorageType<?>, List<AbstractEnderStorage>> storageList;
    public final boolean client;

    private File saveDir;
    private File[] saveFiles;
    private int saveTo;
    private List<AbstractEnderStorage> dirtyStorage;
    private CompoundNBT saveTag;

    public EnderStorageManager(boolean client) {
        this.client = client;

        storageMap = Collections.synchronizedMap(new HashMap<>());
        storageList = Collections.synchronizedMap(new HashMap<>());
        dirtyStorage = Collections.synchronizedList(new LinkedList<>());

        for (StorageType<?> key : plugins.keySet()) {
            storageList.put(key, new ArrayList<>());
        }

        if (!client) {
            load();
        }
    }

    public static void init() {
        MinecraftForge.EVENT_BUS.addListener(EnderStorageManager::onServerStarted);
    }

    private static void onServerStarted(FMLServerStartedEvent event) {
        EnderStorageManager.reloadManager(false);
    }

    private void sendClientInfo(ServerPlayerEntity player) {
        for (Map.Entry<StorageType<?>, EnderStoragePlugin<?>> plugin : plugins.entrySet()) {
            plugin.getValue().sendClientInfo(player, unsafeCast(storageList.get(plugin.getKey())));
        }
    }

    @SuppressWarnings ("unchecked")
    private static <T> T unsafeCast(Object object) {
        return (T) object;
    }

    private void load() {

        saveDir = new File(ServerUtils.getSaveDirectory(), "EnderStorage");
        try {
            if (!saveDir.exists()) {
                saveDir.mkdirs();
            }
            //TODO, Ok so, This looks like cancer, but is actually quite smart, data1, and data2 are essentially backups, lock holds the current data and lock is only ever written to after a successful write to data1/2.
            //TODO, Maybe this isnt needed anymore? Maybe it should be stored via WorldSavedData..
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
                    saveTag = new CompoundNBT();
                }
            } else {
                saveTag = new CompoundNBT();
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("EnderStorage was unable to read it's data, please delete the 'EnderStorage' folder Here: %s and start the server again.", saveDir), e);
        }
    }

    private void save(boolean force) {
        if (!dirtyStorage.isEmpty() || force) {
            for (AbstractEnderStorage inv : dirtyStorage) {
                saveTag.put(inv.freq + ",type=" + inv.type(), inv.saveToTag());
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

    @SuppressWarnings ("unchecked")
    public <T extends AbstractEnderStorage> T getStorage(Frequency freq, StorageType<T> type) {
        String key = freq + ",type=" + type.name;
        AbstractEnderStorage storage = storageMap.get(key);
        if (storage == null) {
            storage = plugins.get(type).createEnderStorage(this, freq);
            if (!client && saveTag.contains(key)) {
                storage.loadFromTag(saveTag.getCompound(key));
            }
            storageMap.put(key, storage);
            storageList.get(type).add(storage);
        }
        return (T) storage;
    }

    public static void registerPlugin(EnderStoragePlugin<?> plugin) {
        plugins.put(plugin.identifier(), plugin);

        if (serverManager != null) {
            serverManager.storageList.put(plugin.identifier(), new ArrayList<>());
        }
        if (clientManager != null) {
            clientManager.storageList.put(plugin.identifier(), new ArrayList<>());
        }
    }

    public static EnderStoragePlugin<?> getPlugin(StorageType<?> identifier) {
        return plugins.get(identifier);
    }

    public static Map<StorageType<?>, EnderStoragePlugin<?>> getPlugins() {
        return ImmutableMap.copyOf(plugins);
    }

    public List<String> getValidKeys(String identifer) {
        List<String> list = new ArrayList<>();
        for (String key : saveTag.getAllKeys()) {
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
