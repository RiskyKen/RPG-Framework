package moe.plushie.rpgeconomy.common.capability.wallet;

import java.util.concurrent.Callable;

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
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class WalletCapability implements IWallet {
    
    @CapabilityInject(IWallet.class)
    public static final Capability<IWallet> WALLET_CAP = null;
    
    public WalletCapability() {
        // TODO Auto-generated constructor stub
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
    
    public static IWallet get(EntityLivingBase player) {
        return player.getCapability(WALLET_CAP, null);
    }
    
    public static class WalletStorage implements IStorage<IWallet> {

        @Override
        public NBTBase writeNBT(Capability<IWallet> capability, IWallet instance, EnumFacing side) {
            NBTTagCompound compound = new NBTTagCompound();
            return compound;
        }

        @Override
        public void readNBT(Capability<IWallet> capability, IWallet instance, EnumFacing side, NBTBase nbt) {
            NBTTagCompound compound = (NBTTagCompound) nbt;
            
        }
    }
    
    public static class WalletProvider implements ICapabilitySerializable<NBTTagCompound> {

        private final IWallet wallet;
        
        public WalletProvider() {
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
    
    public static class WalletFactory implements Callable<IWallet> {

        @Override
        public IWallet call() throws Exception {
            return null;
        }
    }
}
