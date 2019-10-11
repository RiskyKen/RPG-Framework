package moe.plushie.rpg_framework.bank.common.capability;

import java.util.HashMap;
import java.util.concurrent.Callable;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import moe.plushie.rpg_framework.api.bank.IBank;
import moe.plushie.rpg_framework.api.bank.IBankAccount;
import moe.plushie.rpg_framework.api.bank.IBankCapability;
import moe.plushie.rpg_framework.bank.common.BankAccount;
import moe.plushie.rpg_framework.bank.common.BankManager;
import moe.plushie.rpg_framework.bank.common.serialize.BankAccountSerializer;
import moe.plushie.rpg_framework.core.RPGFramework;
import moe.plushie.rpg_framework.core.common.network.PacketHandler;
import moe.plushie.rpg_framework.core.common.network.server.MessageServerSyncBankAccount;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.Capability.IStorage;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.Constants.NBT;

public class BankCapability implements IBankCapability {

    @CapabilityInject(IBankCapability.class)
    public static final Capability<IBankCapability> BANK_CAP = null;

    private final HashMap<String, IBankAccount> bankAccountMap;

    public BankCapability() {
        bankAccountMap = new HashMap<String, IBankAccount>();
    }

    @Override
    public IBankAccount getBank(IBank bank) {
        if (!bankAccountMap.containsKey(bank.getIdentifier())) {
            bankAccountMap.put(bank.getIdentifier(), new BankAccount(bank));
        }
        return bankAccountMap.get(bank.getIdentifier());
    }

    @Override
    public void setBankAccount(IBankAccount bankInstance) {
        bankAccountMap.put(bankInstance.getBank().getIdentifier(), bankInstance);
    }

    @Override
    public void syncToOwner(EntityPlayerMP entityPlayer) {
        IBankAccount[] accounts = bankAccountMap.values().toArray(new IBankAccount[bankAccountMap.size()]);
        PacketHandler.NETWORK_WRAPPER.sendTo(new MessageServerSyncBankAccount(accounts), entityPlayer);
    }

    public static IBankCapability get(EntityLivingBase player) {
        return player.getCapability(BANK_CAP, null);
    }

    public static class Storage implements IStorage<IBankCapability> {

        private static final String TAG_BANK = "bank_";

        @Override
        public NBTBase writeNBT(Capability<IBankCapability> capability, IBankCapability instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            BankManager bankManager = RPGFramework.getProxy().getBankManager();
            for (IBank bank : bankManager.getBanks()) {
                IBankAccount bankInstance = instance.getBank(bank);
                JsonElement json = BankAccountSerializer.serializeJson(bankInstance, true);
                compound.setString(TAG_BANK + bank.getIdentifier(), json.toString());
            }
            return compound;
        }

        @Override
        public void readNBT(Capability<IBankCapability> capability, IBankCapability instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            BankManager bankManager = RPGFramework.getProxy().getBankManager();
            for (IBank bank : bankManager.getBanks()) {
                if (compound.hasKey(TAG_BANK + bank.getIdentifier(), NBT.TAG_STRING)) {
                    try {
                        JsonParser parser = new JsonParser();
                        JsonElement json = parser.parse(compound.getString(TAG_BANK + bank.getIdentifier()));
                        IBankAccount bankNew = BankAccountSerializer.deserializeJson(json, bank);
                        instance.setBankAccount(bankNew);
                    } catch (Exception e) {
                        RPGFramework.getLogger().error("Error parsing json.");
                        RPGFramework.getLogger().error(e.getLocalizedMessage());
                    }
                }
            }
        }

        public static void writeToDatabase(EntityPlayer player, IBankCapability instance) {
            BankManager bankManager = RPGFramework.getProxy().getBankManager();
            for (IBank bank : bankManager.getBanks()) {
                BankAccountSerializer.serializeDatabase(player, instance.getBank(bank));
            }
        }

        public static void readFromDatabase(EntityPlayer player, IBankCapability instance) {
            BankManager bankManager = RPGFramework.getProxy().getBankManager();
            for (IBank bank : bankManager.getBanks()) {
                instance.setBankAccount(BankAccountSerializer.deserializeDatabase(player, bank));
            }
        }
    }

    public static class Provider implements ICapabilityProvider {

        private final EntityPlayer player;

        public Provider(EntityPlayer player) {
            this.player = player;
        }

        @Override
        public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
            return capability != null && capability == BANK_CAP;
        }

        @Override
        public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
            if (hasCapability(capability, facing)) {
                IBankCapability bankCapability = BANK_CAP.getDefaultInstance();
                Storage.readFromDatabase(player, bankCapability);
                return BANK_CAP.cast(bankCapability);
            }
            return null;
        }

        /*@Override
        public NBTTagCompound serializeNBT() {
            return (NBTTagCompound) BANK_CAP.getStorage().writeNBT(BANK_CAP, bankCapability, null);
        }

        @Override
        public void deserializeNBT(NBTTagCompound nbt) {
            BANK_CAP.getStorage().readNBT(BANK_CAP, bankCapability, null, nbt);
        }*/
    }

    public static class Factory implements Callable<IBankCapability> {

        @Override
        public IBankCapability call() throws Exception {
            return new BankCapability();
        }
    }
}
