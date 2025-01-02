package codechicken.enderstorage.manager;

import codechicken.enderstorage.api.AbstractEnderStorage;
import codechicken.enderstorage.api.EnderStoragePlugin;
import codechicken.enderstorage.api.Frequency;
import codechicken.enderstorage.api.StorageType;
import codechicken.lib.util.ServerUtils;
import com.google.common.collect.ImmutableMap;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.server.ServerStartedEvent;
import net.neoforged.neoforge.server.ServerLifecycleHooks;
import org.jetbrains.annotations.Nullable;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.*;

public class EnderStorageManager {

    public static class EnderStorageSaveHandler {

        @SubscribeEvent
        public void onWorldLoad(LevelEvent.Load event) {
            if (event.getLevel().isClientSide()) {
                reloadManager(true);
            }
        }

        @SubscribeEvent
        public void onWorldSave(LevelEvent.Save event) {
            if (!event.getLevel().isClientSide() && instance(false) != null) {
                instance(false).save(false);
            }
        }

        @SubscribeEvent
        public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
            instance(false).sendClientInfo((ServerPlayer) event.getEntity());
        }

        @SubscribeEvent
        public void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
            instance(false).sendClientInfo((ServerPlayer) event.getEntity());
        }
    }

    private static @Nullable EnderStorageManager serverManager;
    private static @Nullable EnderStorageManager clientManager;
    private static Map<StorageType<?>, EnderStoragePlugin<?>> plugins = new HashMap<>();

    private Map<String, AbstractEnderStorage> storageMap;
    private Map<StorageType<?>, List<AbstractEnderStorage>> storageList;
    public final boolean client;

    private File saveDir;
    private File[] saveFiles;
    private int saveTo;
    private List<AbstractEnderStorage> dirtyStorage;
    private CompoundTag saveTag;

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
        NeoForge.EVENT_BUS.addListener(EnderStorageManager::onServerStarted);
    }

    private static void onServerStarted(ServerStartedEvent event) {
        EnderStorageManager.reloadManager(false);
    }

    private void sendClientInfo(ServerPlayer player) {
        for (Map.Entry<StorageType<?>, EnderStoragePlugin<?>> plugin : plugins.entrySet()) {
            plugin.getValue().sendClientInfo(player, unsafeCast(storageList.get(plugin.getKey())));
        }
    }

    @SuppressWarnings ("unchecked")
    private static <T> T unsafeCast(Object object) {
        return (T) object;
    }

    private void load() {
        saveDir = new File(ServerUtils.getSaveDirectory().toFile(), "EnderStorage");
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
                    saveTag = NbtIo.readCompressed(in, NbtAccounter.unlimitedHeap());
                    in.close();
                } else {
                    saveTag = new CompoundTag();
                }
            } else {
                saveTag = new CompoundTag();
            }
        } catch (Exception e) {
            throw new RuntimeException(String.format("EnderStorage was unable to read it's data, please delete the 'EnderStorage' folder Here: %s and start the server again.", saveDir), e);
        }
    }

    private void save(boolean force) {
        if (!dirtyStorage.isEmpty() || force) {
            for (AbstractEnderStorage inv : dirtyStorage) {
                saveTag.put(inv.freq + ",type=" + inv.type(), inv.saveToTag(ServerLifecycleHooks.getCurrentServer().registryAccess()));
                inv.setClean();
            }

            dirtyStorage.clear();

            try {
                File saveFile = saveFiles[saveTo];
                if (!saveFile.exists()) {
                    saveFile.createNewFile();
                }
                DataOutputStream dout = new DataOutputStream(new FileOutputStream(saveFile));
                NbtIo.writeCompressed(saveTag, dout);
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
        String key = freq + ",type=" + type.name();
        AbstractEnderStorage storage = storageMap.get(key);
        if (storage == null) {
            storage = plugins.get(type).createEnderStorage(this, freq);
            if (!client && saveTag.contains(key)) {
                storage.loadFromTag(saveTag.getCompound(key), ServerLifecycleHooks.getCurrentServer().registryAccess());
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
