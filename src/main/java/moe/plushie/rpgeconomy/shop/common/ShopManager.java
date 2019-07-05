package moe.plushie.rpgeconomy.shop.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.HashMap;

import com.google.common.base.Charsets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.core.IIdentifier;
import moe.plushie.rpgeconomy.api.shop.IShop;
import moe.plushie.rpgeconomy.api.shop.IShopManager;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.IdentifierString;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncShops;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.shop.common.serialize.ShopSerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;

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
            Shop shop = ShopSerializer.deserializeJson(jsonElement, new IdentifierString(shopFile.getName()));
            if (shop != null) {
            	shopMap.put(shop.getIdentifier(), shop);
            }
        }
    }
    
    public void saveShop(IShop shop) {
        RpgEconomy.getLogger().info("Saving shop: " + shop.getIdentifier());
        JsonElement jsonData = ShopSerializer.serializeJson(shop, false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(new File(currencyDirectory, String.valueOf(shop.getIdentifier().getValue())), Charsets.UTF_8, gson.toJson(jsonData));
    }
    
	@Override
	public Shop getShop(IIdentifier identifier) {
		return shopMap.get(identifier);
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
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncShops(getShopIdentifier(), getShopNames()), player);
    }
}
