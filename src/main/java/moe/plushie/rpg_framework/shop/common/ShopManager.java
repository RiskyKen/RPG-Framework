package moe.plushie.rpg_framework.shop.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.annotation.Nullable;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.ListenableFutureTask;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.api.shop.IShop;
import moe.plushie.rpg_framework.api.shop.IShopManager;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncShops;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.TableShops;
import moe.plushie.rpg_framework.core.database.stats.TableStatsShopSales;
import moe.plushie.rpg_framework.shop.common.serialize.ShopSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class ShopManager implements IShopManager {

    private static final String DIRECTORY_NAME = "shop";

    private final File shopsDirectory;

    public ShopManager(File modDirectory) {
        shopsDirectory = new File(modDirectory, DIRECTORY_NAME);
        MinecraftForge.EVENT_BUS.register(this);
        DatabaseManager.createTaskAndExecute(new Runnable() {

            @Override
            public void run() {
                TableShops.create();
                TableStatsShopSales.create();
            }
        });
    }

    @Override
    public void saveShop(IShop shop) {
        RPGFramework.getLogger().info("Saving shop: " + shop.getIdentifier());
        TableShops.updateShop(shop);
    }

    @Override
    public ListenableFutureTask<Void> saveShopAsync(IShop shop, FutureCallback<Void> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                saveShop(shop);
                return null;
            }
        }, callback);
    }

    @Override
    public IShop getShop(IIdentifier identifier) {
        return TableShops.getShop(identifier);
    }

    @Override
    public ListenableFutureTask<IShop> getShopAsync(IIdentifier identifier, @Nullable FutureCallback<IShop> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<IShop>() {

            @Override
            public IShop call() throws Exception {
                return getShop(identifier);
            }
        }, callback);
    }

    @Override
    public IShop createShop(String shopName) {
        return TableShops.createNewShop(shopName);
    }

    @Override
    public ListenableFutureTask<IShop> createShopAsync(String shopName, FutureCallback<IShop> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<IShop>() {

            @Override
            public IShop call() throws Exception {
                return createShop(shopName);
            }
        }, callback);
    }

    @Override
    public void removeShop(IIdentifier shopIdentifier) {
        TableShops.deleteShop(shopIdentifier);
    }

    @Override
    public ListenableFutureTask<Void> removeShopAsync(IIdentifier identifier, FutureCallback<Void> callback) {
        return DatabaseManager.createTaskAndExecute(new Callable<Void>() {

            @Override
            public Void call() throws Exception {
                removeShop(identifier);
                return null;
            }
        }, callback);
    }

    public void sendShopListToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        TableShops.getShopList(identifiers, names, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncShops(identifiers.toArray(new IIdentifier[identifiers.size()]), names.toArray(new String[names.size()])), player);
    }

    // \/ Shop JSON code \/

    public void exportShopJson() {
        if (!shopsDirectory.exists()) {
            shopsDirectory.mkdirs();
        }
        RPGFramework.getLogger().info("Exporting Shops");
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        TableShops.getShopList(identifiers, null, null);
        for (IIdentifier identifier : identifiers) {
            exportShop(TableShops.getShop(identifier));
        }
        RPGFramework.getLogger().info("Export Finished");
    }

    private void exportShop(IShop shop) {
        RPGFramework.getLogger().info("Exporting shop: " + shop.getIdentifier());
        JsonElement jsonData = ShopSerializer.serializeJson(shop, false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(new File(shopsDirectory, String.valueOf(shop.getIdentifier().getValue()) + ".json"), Charsets.UTF_8, gson.toJson(jsonData));
    }

    public void importShopJson() {
        if (!shopsDirectory.exists()) {
            shopsDirectory.mkdirs();
        }
        RPGFramework.getLogger().info("Importing Shops");
        File[] files = shopsDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        for (File file : files) {
            importShop(file);
        }
        RPGFramework.getLogger().info("Import Finished");
    }

    private void importShop(File shopFile) {
        RPGFramework.getLogger().info("Importing shop: " + shopFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(shopFile);
        if (jsonElement != null) {
            Shop shop = ShopSerializer.deserializeJson(jsonElement, new IdentifierString(shopFile.getName()));
            if (shop != null) {
                TableShops.addNewShop(shop);
            }
        }
    }
}
