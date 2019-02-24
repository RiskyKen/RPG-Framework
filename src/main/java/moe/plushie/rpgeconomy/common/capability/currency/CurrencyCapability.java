package moe.plushie.rpgeconomy.common.capability.currency;

import java.util.HashMap;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import moe.plushie.rpgeconomy.RpgEconomy;
import moe.plushie.rpgeconomy.api.currency.ICurrencyCapability;
import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.CurrencyManager;
import moe.plushie.rpgeconomy.common.currency.Wallet;
import moe.plushie.rpgeconomy.common.currency.serialize.WalletSerializer;
import moe.plushie.rpgeconomy.common.network.PacketHandler;
import moe.plushie.rpgeconomy.common.network.server.MessageServerSyncWalletCap;
import net.minecraft.entity.EntityLivingBase;
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
        CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
        walletMap = new HashMap<String, Wallet>();
        Currency[] currencies = currencyManager.getCurrencies();
        for (Currency currency : currencies) {
            walletMap.put(currency.getName(), new Wallet(currency));
        }
    }
    
    @Override
    public Wallet getWallet(Currency currency) {
        return walletMap.get(currency.getName());
    }
    
    protected IMessage getUpdateMessage(EntityPlayerMP entityPlayer) {
        NBTTagCompound compound = (NBTTagCompound)WALLET_CAP.getStorage().writeNBT(WALLET_CAP, this, null);
        return new MessageServerSyncWalletCap(entityPlayer.getEntityId(), compound);
    }
    
    @Override
    public void syncToOwner(EntityPlayerMP entityPlayer) {
        IMessage message = getUpdateMessage(entityPlayer);
        PacketHandler.NETWORK_WRAPPER.sendTo(message, entityPlayer);
    }
    
    public static ICurrencyCapability get(EntityLivingBase player) {
        return player.getCapability(WALLET_CAP, null);
    }
    
    public static class Storage implements IStorage<ICurrencyCapability> {

        private static final String TAG_WALLET = "wallet-";
        
        @Override
        public NBTBase writeNBT(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
            Currency[] currencies = currencyManager.getCurrencies();
            for (Currency currency : currencies) {
                Wallet wallet = instance.getWallet(currency);
                JsonElement json = WalletSerializer.serializeJson(wallet);
                compound.setString(TAG_WALLET + currency.getName(), json.toString());
            }
            return compound;
        }

        @Override
        public void readNBT(Capability<ICurrencyCapability> capability, ICurrencyCapability instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            CurrencyManager currencyManager = RpgEconomy.getProxy().getCurrencyManager();
            Currency[] currencies = currencyManager.getCurrencies();
            for (Currency currency : currencies) {
                if (compound.hasKey(TAG_WALLET + currency.getName(), NBT.TAG_STRING)) { 
                    try {
                        JsonParser parser = new JsonParser();
                        JsonElement json = parser.parse(compound.getString(TAG_WALLET + currency.getName()));
                        Wallet walletNew = WalletSerializer.deserializeJson(json);
                        Wallet walletOld = instance.getWallet(currency);
                        walletOld.setAmount(walletNew.getAmount());
                    } catch (Exception e) {
                        RpgEconomy.getLogger().error("Error parsing json.");
                        RpgEconomy.getLogger().error(e.getLocalizedMessage());
                    }
                }
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
