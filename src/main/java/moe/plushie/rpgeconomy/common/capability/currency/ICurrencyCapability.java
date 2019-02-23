package moe.plushie.rpgeconomy.common.capability.currency;

import moe.plushie.rpgeconomy.common.currency.Currency;
import moe.plushie.rpgeconomy.common.currency.Wallet;
import net.minecraft.entity.player.EntityPlayerMP;

public interface ICurrencyCapability {
    
    public Wallet getWallet(Currency currency);
    
    /**
     * Syncs capability data to a player.
     * 
     * @param entityPlayer Player to sync to.
     */
    public void syncToOwner(EntityPlayerMP entityPlayer);
}
