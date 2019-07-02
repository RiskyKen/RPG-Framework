package moe.plushie.rpgeconomy.bank.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.HashMap;

import com.google.gson.JsonElement;

import moe.plushie.rpgeconomy.api.bank.IBank;
import moe.plushie.rpgeconomy.api.bank.IBankManager;
import moe.plushie.rpgeconomy.bank.common.serialize.BankSerializer;
import moe.plushie.rpgeconomy.core.RpgEconomy;
import moe.plushie.rpgeconomy.core.common.network.PacketHandler;
import moe.plushie.rpgeconomy.core.common.network.server.MessageServerSyncBanks;
import moe.plushie.rpgeconomy.core.common.utils.SerializeHelper;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BankManager implements IBankManager {
    
    private static final String DIRECTORY_NAME = "bank";

    private final File currencyDirectory;
    private final HashMap<String, IBank> bankMap;

    public BankManager(File modDirectory) {
        currencyDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!currencyDirectory.exists()) {
            currencyDirectory.mkdir();
        }
        bankMap = new HashMap<String, IBank>();
        MinecraftForge.EVENT_BUS.register(this);
    }
    
    public void reload(boolean syncWithClients) {
        RpgEconomy.getLogger().info("Loading Banks");
        File[] files = currencyDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".json");
            }
        });
        bankMap.clear();
        for (File file : files) {
            loadBank(file);
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
    
    public void syncToClient(EntityPlayerMP entityPlayer) {
        RpgEconomy.getLogger().info("Sending " + bankMap.size() + " bank list(s) to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }
    
    private void syncToAll() {
        RpgEconomy.getLogger().info("Sending " + bankMap.size() + " bank list(s) to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }
    
    private IMessage getSyncMessage() {
        return new MessageServerSyncBanks(getBanks());
    }
    
    public void gotBanksFromServer(IBank[] banks) {
        RpgEconomy.getLogger().info("Got " + banks.length + " bank list(s) from server.");
        bankMap.clear();
        for (IBank bank : banks) {
            bankMap.put(bank.getIdentifier(), bank);
        }
    }
    
    private void loadBank(File bankFile) {
        RpgEconomy.getLogger().info("Loading bank: " + bankFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(bankFile);
        if (jsonElement != null) {
            Bank bank = BankSerializer.deserializeJson(jsonElement, bankFile.getName());
            if (bank != null) {
                bankMap.put(bank.getIdentifier(), bank);
            }
        }
    }
    
    public int getBankIndex(IBank bank) {
        if (bank == null) {
            return -1;
        }
        IBank[] banks = getBanks();
        for (int i = 0; i < banks.length; i++) {
            if (bank == banks[i]) {
                return i;
            }
        }
        return -1;
    }
    
    public IBank getBank(int index) {
        IBank[] banks = getBanks();
        if (index >= 0 & index < banks.length) {
            return banks[index];
        }
        return null;
    }
    
    @Override
    public IBank getBank(String identifier) {
        return bankMap.get(identifier);
    }

    @Override
    public IBank[] getBanks() {
        return bankMap.values().toArray(new Bank[bankMap.size()]);
    }

    @Override
    public String[] getBankNames() {
        return bankMap.keySet().toArray(new String[bankMap.size()]);
    }
}
