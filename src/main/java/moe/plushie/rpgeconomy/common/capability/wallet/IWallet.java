package moe.plushie.rpgeconomy.common.capability.wallet;

import net.minecraft.entity.player.EntityPlayerMP;

public interface IWallet {
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToOwner(EntityPlayerMP entityPlayer);
}
