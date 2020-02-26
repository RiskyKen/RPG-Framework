package moe.plushie.rpg_framework.api.currency;

import net.minecraft.entity.player.EntityPlayerMP;

public interface ICurrencyCapability {
    
    public IWallet getWallet(ICurrency currency);
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToOwner(EntityPlayerMP entityPlayer, boolean dirty);
}
