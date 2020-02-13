package moe.plushie.rpg_framework.shop.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.common.base.Charsets;
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
import moe.plushie.rpg_framework.shop.common.serialize.ShopSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ShopManager implements IShopManager {

    private static final String DIRECTORY_NAME = "shop";

    private final File currencyDirectory;
    private final HashMap<IIdentifier, Shop> shopMap;

    public ShopManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        shopMap = new HashMap<IIdentifier, Shop>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void exportShopJson() {
        RPGFramework.getLogger().info("Exporting Shops");
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        TableShops.getShopList(identifiers, null, null);
        for (IIdentifier identifier : identifiers) {
            exportShop(TableShops.getShop(identifier));
        }
    }

    public void importShopJson() {
        RPGFramework.getLogger().info("Importing Shops");
        RPGFramework.getLogger().info("Loading Shops");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        shopMap.clear();
        for (File file : files) {
            importShop(file);
        }
    }

    public void saveShop(IShop shop) {
        DatabaseManager.EXECUTOR.execute(new Runnable() {
            
            @Override
            public void run() {
                RPGFramework.getLogger().info("Saving shop: " + shop.getIdentifier());
                TableShops.updateShop(shop);
            }
        });
    }

    public void exportShop(IShop shop) {
        RPGFramework.getLogger().info("Exporting shop: " + shop.getIdentifier());
        JsonElement jsonData = ShopSerializer.serializeJson(shop, false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(new File(currencyDirectory, String.valueOf(shop.getIdentifier().getValue()) + ".json"), Charsets.UTF_8, gson.toJson(jsonData));
    }

    public void exportShopSql() {
        TableShops.exportShopSql();
    }

    public void importShop(File shopFile) {
        RPGFramework.getLogger().info("Importing shop: " + shopFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(shopFile);
        if (jsonElement != null) {
            Shop shop = ShopSerializer.deserializeJson(jsonElement, new IdentifierString(shopFile.getName()));
            if (shop != null) {
                TableShops.addNewShop(shop);
            }
        }
    }

    @Override
    public void getShop(IShopLoadCallback callback, IIdentifier identifier) {
        if (identifier == null) {
            callback.onShopLoad(null);
            return;
        }
        DatabaseManager.EXECUTOR.execute(new Runnable() {
            
            @Override
            public void run() {
                IShop shop = TableShops.getShop(identifier);
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {
                    
                    @Override
                    public void run() {
                        callback.onShopLoad(shop);
                    }
                });
            }
        });
    }

    @Override
    public IShop[] getShops() {
        IShop[] shops = shopMap.values().toArray(new Shop[shopMap.size()]);
        Arrays.sort(shops);
        return shops;
    }

    @Override
    public IIdentifier[] getShopIdentifier() {
        return shopMap.keySet().toArray(new IIdentifier[shopMap.size()]);
    }

    @Override
    public String[] getShopNames() {
        String[] names = new String[shopMap.size()];
        IShop[] shops = shopMap.values().toArray(new IShop[names.length]);
        for (int i = 0; i < shops.length; i++) {
            names[i] = shops[i].getName();
        }
        return names;
    }

    public void syncToClient(EntityPlayerMP player) {
        ArrayList<IIdentifier> identifiers = new ArrayList<IIdentifier>();
        ArrayList<String> names = new ArrayList<String>();
        TableShops.getShopList(identifiers, names, null);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncShops(identifiers.toArray(new IIdentifier[identifiers.size()]), names.toArray(new String[names.size()])), player);
    }

    public void addShop(String shopName) {
        TableShops.createNewShop(shopName);
    }

    public void removeShop(IIdentifier shopIdentifier) {
        TableShops.deleteShop(shopIdentifier);
    }
}
