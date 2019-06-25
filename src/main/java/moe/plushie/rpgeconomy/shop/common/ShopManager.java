package moe.plushie.rpgeconomy.shop.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShopManager;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncShops;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.shop.common.serialize.ShopSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

public class ShopManager implements IShopManager {
	
    private static final String DIRECTORY_NAME = "shop";

    private final File currencyDirectory;
    private final HashMap<String, Shop> shopMap;

    public ShopManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        shopMap = new HashMap<String, Shop>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void reload() {
        RpgEconomy.getLogger().info("Loading Shops");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        shopMap.clear();
        for (File file : files) {
            loadShop(file);
        }
    }
    
    private void loadShop(File shopFile) {
        RpgEconomy.getLogger().info("Loading shop: " + shopFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(shopFile);
        if (jsonElement != null) {
            Shop shop = ShopSerializer.deserializeJson(jsonElement, shopFile.getName());
            if (shop != null) {
            	shopMap.put(shop.getIdentifier(), shop);
            }
        }
    }
    
	@Override
	public Shop getShop(String identifier) {
		return shopMap.get(identifier);
	}

	@Override
	public IShop[] getShops() {
		return shopMap.values().toArray(new Shop[shopMap.size()]);
	}

	@Override
	public String[] getShopNames() {
		return shopMap.keySet().toArray(new String[shopMap.size()]);
	}

    public void syncToClient(EntityPlayerMP player) {
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncShops(getShopNames()), player);
    }
}
