package moe.plushie.rpg_framework.api.currency;

import moe.plushie.rpg_framework.api.core.IItemMatcher;
import net.minecraft.entity.player.EntityPlayer;

public interface ICost {

    public IWallet getWalletCost();

    public IItemMatcher[] getItemCost();

    public boolean hasWalletCost();

    public boolean hasItemCost();

    public boolean canAfford(EntityPlayer player);

    public void pay(EntityPlayer player);

    public ICost add(ICost... costs);
}
