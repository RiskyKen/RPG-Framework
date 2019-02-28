package moe.plushie.rpgeconomy.currency.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.currency.ICurrencyManager;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncCurrency;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import moe.plushie.rpgeconomy.currency.serialize.CurrencySerializer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CurrencyManager implements ICurrencyManager {

    private static final String DIRECTORY_NAME = "currency";

    private final File currencyDirectory;
    private final HashMap<String, Currency> currencyMap;

    public CurrencyManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        currencyMap = new HashMap<String, Currency>();
        MinecraftForge.EVENT_BUS.register(this);
    }

    public void reload(boolean syncWithClients) {
        RpgEconomy.getLogger().info("Loading Currencies");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        currencyMap.clear();
        for (File file : files) {
            loadCurrency(file);
        }
        if (syncWithClients) {
            syncToAll();
        }
    }
    
    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
        if (!event.player.getEntityWorld().isRemote) {
            syncToClient((EntityPlayerMP) event.player);
        }
    }
    
    private void syncToClient(EntityPlayerMP entityPlayer) {
        RpgEconomy.getLogger().info("Sending currency list to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }
    
    private void syncToAll() {
        RpgEconomy.getLogger().info("Sending currency list to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }
    
    private IMessage getSyncMessage() {
        return new MessageServerSyncCurrency(getCurrencies());
    }

    private void loadCurrency(File currencyFile) {
        RpgEconomy.getLogger().info("Loading currency: " + currencyFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(currencyFile);
        if (jsonElement != null) {
            Currency currency = CurrencySerializer.deserializeJson(jsonElement);
            if (currency != null) {
                currencyMap.put(currency.getName(), currency);
            }
        }
    }
    
    public void gotCurrenciesFromServer(Currency[] currencies) {
        RpgEconomy.getLogger().info("Got currency list from server. " + currencies.length);
        currencyMap.clear();
        for (Currency currency : currencies) {
            currencyMap.put(currency.getName(), currency);
        }
    }
    
    @Override
    public Currency getCurrency(String name) {
        return currencyMap.get(name);
    }

    @Override
    public Currency[] getCurrencies() {
        return currencyMap.values().toArray(new Currency[currencyMap.size()]);
    }
}
