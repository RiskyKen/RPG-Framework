package moe.plushie.rpg_framework.bank.common;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

import com.google.common.base.Charsets;
import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.api.bank.IBankManager;
import moe.plushie.rpg_framework.api.core.IIdentifier;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.bank.common.serialize.BankSerializer;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.IdentifierString;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncBanks;
import moe.plushie.rpg_framework.core.common.utils.SerializeHelper;
import moe.plushie.rpg_framework.core.database.DBPlayer;
import moe.plushie.rpg_framework.core.database.DBPlayerInfo;
import moe.plushie.rpg_framework.core.database.DatabaseManager;
import moe.plushie.rpg_framework.core.database.TableBankAccounts;
import moe.plushie.rpg_framework.core.database.TablePlayers;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class BankManager implements IBankManager {

    private static final String DIRECTORY_NAME = "bank";

    private final File bankDirectory;
    private final ConcurrentHashMap<IIdentifier, IBank> bankMap;

    public BankManager(File modDirectory) {
        bankDirectory = new File(modDirectory, DIRECTORY_NAME);
        if (!bankDirectory.exists()) {
            bankDirectory.mkdir();
        }
        bankMap = new ConcurrentHashMap<IIdentifier, IBank>();
        MinecraftForge.EVENT_BUS.register(this);
        TableBankAccounts.create();
    }

    public void reload(boolean syncWithClients) {
        RPGFramework.getLogger().info("Loading Banks");
        File[] files = bankDirectory.listFiles(new FilenameFilter() {
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
        RPGFramework.getLogger().info("Sending " + bankMap.size() + " bank list(s) to player " + entityPlayer.getName() + ".");
        PacketHandler.NETWORK_WRAPPER.sendTo(getSyncMessage(), entityPlayer);
    }

    private void syncToAll() {
        RPGFramework.getLogger().info("Sending " + bankMap.size() + " bank list(s) to all players.");
        PacketHandler.NETWORK_WRAPPER.sendToAll(getSyncMessage());
    }

    private IMessage getSyncMessage() {
        return new MessageServerSyncBanks(getBanks());
    }

    public void gotBanksFromServer(IBank[] banks) {
        RPGFramework.getLogger().info("Got " + banks.length + " bank list(s) from server.");
        bankMap.clear();
        for (IBank bank : banks) {
            bankMap.put(bank.getIdentifier(), bank);
        }
    }

    private void loadBank(File bankFile) {
        RPGFramework.getLogger().info("Loading bank: " + bankFile.getName());
        JsonElement jsonElement = SerializeHelper.readJsonFile(bankFile);
        if (jsonElement != null) {
            Bank bank = BankSerializer.deserializeJson(jsonElement, new IdentifierString(bankFile.getName()));
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
    public IBank getBank(IIdentifier identifier) {
        return bankMap.get(identifier);
    }

    @Override
    public void getBankAccount(IBankAccountLoadCallback callback, IBank bank, GameProfile sourcePlayer) {
        if (bank == null | sourcePlayer == null) {
            callback.onBackAccountLoad(null);
            return;
        }
        DBPlayer dbPlayer = TablePlayers.getPlayer(sourcePlayer);
        if (dbPlayer.getId() < 0 | dbPlayer == DBPlayerInfo.MISSING_INFO) {
            callback.onBackAccountLoad(null);
            return;
        }
        getBankAccount(callback, bank, dbPlayer);
    }

    public void getBankAccount(IBankAccountLoadCallback callback, IBank bank, DBPlayer sourcePlayer) {
        if (bank == null | sourcePlayer == null) {
            callback.onBackAccountLoad(null);
            return;
        }

        DatabaseManager.createTaskAndExecute(new Callable<IBankAccount>() {

            @Override
            public IBankAccount call() throws Exception {
                return BankAccountSerializer.deserializeDatabase(sourcePlayer, bank);
            }
        }, new FutureCallback<IBankAccount>() {

            @Override
            public void onSuccess(IBankAccount result) {
                FMLCommonHandler.instance().getMinecraftServerInstance().addScheduledTask(new Runnable() {

                    @Override
                    public void run() {
                        callback.onBackAccountLoad(result);
                    }
                });
            }

            @Override
            public void onFailure(Throwable t) {
                t.printStackTrace();
            }
        });
    }

    @Override
    public IBank[] getBanks() {
        IBank[] banks = bankMap.values().toArray(new IBank[bankMap.size()]);
        Arrays.sort(banks);
        return banks;
    }

    @Override
    public String[] getBankNames() {
        return bankMap.keySet().toArray(new String[bankMap.size()]);
    }

    public void saveBank(IBank bank) {
        RPGFramework.getLogger().info("Saving bank: " + bank.getIdentifier());
        JsonElement jsonData = BankSerializer.serializeJson(bank, false);
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        SerializeHelper.writeFile(new File(bankDirectory, String.valueOf(bank.getIdentifier().getValue())), Charsets.UTF_8, gson.toJson(jsonData));
    }
}
