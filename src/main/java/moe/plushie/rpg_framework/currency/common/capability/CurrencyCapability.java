package moe.plushie.rpg_framework.currency.common.capability;

import java.util.HashMap;
import java.util.concurrent.Callable;

import com.google.common.util.concurrent.FutureCallback;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.authlib.GameProfile;

import moe.plushie.rpg_framework.api.core.IStorageDatabase;
import moe.plushie.rpg_framework.api.currency.ICurrency;
import moe.plushie.rpg_framework.api.currency.ICurrencyCapability;
import moe.plushie.rpg_framework.api.currency.IWallet;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.database.DatabaseManager;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncWalletCap;
import moe.plushie.rpg_framework.currency.common.Currency;
import moe.plushie.rpg_framework.currency.common.CurrencyManager;
import moe.plushie.rpg_framework.currency.common.TableWallets;
import moe.plushie.rpg_framework.currency.common.Wallet;
import moe.plushie.rpg_framework.currency.common.serialize.WalletSerializer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class CurrencyCapability implements ICurrencyCapability {

    @CapabilityInject(ICurrencyCapability.class)
    public static final Capability<ICurrencyCapability> WALLET_CAP = null;

    private final HashMap<String, Wallet> walletMap;

    public CurrencyCapability() {
        CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
        walletMap = new HashMap<String, Wallet>();
        Currency[] currencies = currencyManager.getCurrencies();
        for (Currency currency : currencies) {
            walletMap.put(currency.getName(), new Wallet(currency));
        }

    }

    @Override
    public IWallet getWallet(ICurrency currency) {
        if (!walletMap.containsKey(currency.getName())) {
            walletMap.put(currency.getName(), new Wallet(currency));
        }
        return walletMap.get(currency.getName());
    }

    protected IMessage getUpdateMessage(EntityPlayerMP entityPlayer) {
        NBTTagCompound compound = (NBTTagCompound) WALLET_CAP.getStorage().writeNBT(WALLET_CAP, this, null);
        return new MessageServerSyncWalletCap(entityPlayer.getEntityId(), compound);
    }

    @Override
    public void syncToOwner(EntityPlayerMP entityPlayer, boolean dirty) {
        IMessage message = getUpdateMessage(entityPlayer);
        PacketHandler.NETWORK_WRAPPER.sendTo(message, entityPlayer);
        if (dirty) {
            IStorageDatabase<ICurrencyCapability> storageDatabase = (IStorageDatabase<ICurrencyCapability>) WALLET_CAP.getStorage();
            storageDatabase.writeAsync(WALLET_CAP, this, entityPlayer.getGameProfile(), null);
        }
    }

    public void asyncLoadWalletFromDB(EntityPlayer entityPlayer) {
        IStorageDatabase<ICurrencyCapability> storageDatabase = (IStorageDatabase<ICurrencyCapability>) WALLET_CAP.getStorage();
        storageDatabase.readAsync(WALLET_CAP, this, entityPlayer.getGameProfile(), null);
    }

    public static ICurrencyCapability get(EntityLivingBase player) {
        return player.getCapability(WALLET_CAP, null);
    }

    public static class Storage implements IStorage<ICurrencyCapability>, IStorageDatabase<ICurrencyCapability> {

        private static final String TAG_WALLET = "wallet-";

        @Override
        public NBTBase writeNBT(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
            Currency[] currencies = currencyManager.getCurrencies();
            for (Currency currency : currencies) {
                IWallet wallet = instance.getWallet(currency);
                JsonElement json = WalletSerializer.serializeJson(wallet);
                compound.setString(TAG_WALLET + currency.getName(), json.toString());
            }
            return compound;
        }

        @Override
        public void readNBT(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
            Currency[] currencies = currencyManager.getCurrencies();
            for (Currency currency : currencies) {
                if (compound.hasKey(TAG_WALLET + currency.getName(), NBT.TAG_STRING)) {
                    try {
                        JsonParser parser = new JsonParser();
                        JsonElement json = parser.parse(compound.getString(TAG_WALLET + currency.getName()));
                        IWallet walletNew = WalletSerializer.deserializeJson(json);
                        IWallet walletOld = instance.getWallet(currency);
                        walletOld.setAmount(walletNew.getAmount());
                    } catch (Exception e) {
                        RPGFramework.getLogger().error("Error parsing json.");
                        RPGFramework.getLogger().error(e.getLocalizedMessage());
                    }
                }
            }
        }

        @Override
        public void writeAsync(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, GameProfile player, FutureCallback<Void> callback) {

            DatabaseManager.createTaskAndExecute(new Callable<Void>() {
                CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
                Currency[] currencies = currencyManager.getCurrencies();

                @Override
                public Void call() throws Exception {
                    // RPGFramework.getLogger().info("saving wallet to db ");
                    for (Currency currency : currencies) {
                        TableWallets.saveWallet(player, instance.getWallet(currency));
                    }
                    return null;
                }
            }, callback);
        }

        @Override
        public void readAsync(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, GameProfile player, FutureCallback<Void> callback) {
            CurrencyManager currencyManager = RPGFramework.getProxy().getCurrencyManager();
            Currency[] currencies = currencyManager.getCurrencies();
            
            for (Currency currency : currencies) {
                DatabaseManager.createTaskAndExecute(new Runnable() {
                    @Override
                    public void run() {
                        TableWallets.loadWallet(player, instance.getWallet(currency));
                    }
                }, callback);
            }
        }
    }

    public static class Provider implements ICapabilitySerializable<NBTTagCompound> {

        private final ICurrencyCapability wallet;

        public Provider() {
            this.wallet = WALLET_CAP.getDefaultInstance();
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability != null && capability == WALLET_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (hasCapability(capability, facing)) {
                return WALLET_CAP.cast(wallet);
            }
            return null;
        }

        @Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) WALLET_CAP.getStorage().writeNBT(WALLET_CAP, wallet, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            WALLET_CAP.getStorage().readNBT(WALLET_CAP, wallet, null, nbt);
        }

    }

    public static class Factory implements Callable<ICurrencyCapability> {

        @Override
        public ICurrencyCapability call() throws Exception {
            return new CurrencyCapability();
        }
    }
}
